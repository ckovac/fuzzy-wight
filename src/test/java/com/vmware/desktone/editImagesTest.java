package com.vmware.desktone;

import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class editImagesTest {

    RequestSpecification authToken;
    JSONArray goldPatternJson;
    String response;
    String patternId;
    private String goldPatternById;

    @BeforeClass
    public void getGoldPatternId() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternJson = (JSONArray) JSONSerializer.toJSON(response); //TODO: Refactor to use fromObject tp serialize into JSONArray.

        for (int i = 0; i < goldPatternJson.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternJson.get(i);

            if (pools.getString("name").equalsIgnoreCase("ars-win-81-64b")) {
                //    poolId = new int[poolJson.size()];
                patternId = pools.getString("id");
                System.out.println("Found valid gold Pattern with id: " + patternId);
            }
        }
    }

    private String getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given(authToken).when().get("/infrastructure/pattern/gold/"+patternId).asString();
        System.out.println("Got Gold Pattern: \n"+goldPatternById+"\n");

        return goldPatternById;
    }

    @Test
    public void disableGoldPattern(){

    //    String goldPattern = getGoldPatternById();

        JSONObject updateGoldPattern = JSONObject.fromObject(getGoldPatternById());
        updateGoldPattern.put("enabled", false);
        updateGoldPattern.remove("DtLink");

        System.out.println("Sending PUT request with body : \n"+updateGoldPattern.toString()+"\n");

        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(updateGoldPattern.toString()).
                when().put("/infrastructure/pattern/gold/"+patternId+"/update").
                then().statusCode(200);
    }

    @Test(dependsOnMethods = "disableGoldPattern")
    public void validateGoldPatternDisabled(){

        System.out.println("Completed PUT request to Enable Flag \n");
        given(authToken)
                .when().get("/infrastructure/pattern/gold/"+patternId)
                .then().body("enabled",is(false));
    }

    @Test(dependsOnMethods = "validateGoldPatternDisabled")
    public void updateGoldPatternNotesById(){

    //    String goldPattern = getGoldPatternById();

        JSONObject updateNotes = JSONObject.fromObject(getGoldPatternById());
        updateNotes.put("notes", "test5");
        updateNotes.remove("DtLink");

        given(authToken).contentType("application/json").
                and().body(updateNotes.toString()).
                when().put("/infrastructure/pattern/gold/G.1001.2/update").
                then().statusCode(200);
    }

    @Test(dependsOnMethods = "updateGoldPatternNotesById")
    public void validateNotesForGoldPattern(){

        given(authToken)
                .and()
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("notes", containsString("test5"));
        /* TODO: Abstract value of notes to be updated in test data file*/
    }

    @Test(dependsOnMethods = "validateNotesForGoldPattern")
    public void updateGoldPatternWithNullId(){

    //    String goldPattern_2 = getGoldPatternById();
        JSONObject updateId = JSONObject.fromObject(getGoldPatternById());
        updateId.put("id", "");
        updateId.remove("DtLink");

        System.out.println(updateId + "\n");

        given(authToken).header("Content-Type", "application/json").
                and().body(updateId.toString()).
                when().put("/infrastructure/pattern/gold/G.1001.2/update").
                then().statusCode(500).
                and().body("errorMessage", containsString("Unable to find gold pattern with id"));

        System.out.println("Sent PUT request with blank Gold Pattern Id \n");
    }

    @Test(dependsOnMethods = "updateGoldPatternWithNullId")
    public void validateGoldPatternId(){

        given(authToken)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("id", containsString("G.1001.2"));

        System.out.println("Validated that id value not updated \n");

    }

    @AfterClass
    public void resetGoldPatternAfterTest() {
        System.out.println("Completed Tests in : " + getClass().toString());

        String goldPattern = getGoldPatternById();
        JSONObject resetEnableFlag = JSONObject.fromObject(goldPattern);
        resetEnableFlag.put("enabled", true);
        resetEnableFlag.put("notes", "");
        resetEnableFlag.remove("DtLink");

        System.out.println("Sending PUT request with body : \n"+resetEnableFlag.toString()+"\n");

        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(resetEnableFlag.toString()).
                when().put("/infrastructure/pattern/gold/G.1001.2/update").
                then().statusCode(200);

    }
}
