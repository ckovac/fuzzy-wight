package com.vmware.desktone.astro;

import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.testng.Reporter;
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
    private String editGoldPatternName;

    @BeforeClass
    public void getGoldPatternId() throws IOException {
        authToken = LoginUser.loginUser();
        Reporter.log("Starting Tests in : " + getClass().toString() + "\n", true);

        // Read Test Data
        editGoldPatternName = LoginUser.testData.getString("editGoldPattern");

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternJson = (JSONArray) JSONSerializer.toJSON(response);
        //TODO: Refactor to use fromObject to serialize into JSONArray.

        for (int i = 0; i < goldPatternJson.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternJson.get(i);

            if (pools.getString("name").equalsIgnoreCase(editGoldPatternName)) {
                patternId = pools.getString("id");
                Reporter.log("Found valid gold Pattern with id: " + patternId, true);
            }
        }
    }

    private String getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given(authToken).when().get("/infrastructure/pattern/gold/"+patternId).asString();
        Reporter.log("Got Gold Pattern: \n"+goldPatternById+"\n", true);

        return goldPatternById;
    }

    @Test
    public void disableGoldPattern(){

        JSONObject updateGoldPattern = JSONObject.fromObject(getGoldPatternById());
        updateGoldPattern.put("enabled", false);
        updateGoldPattern.remove("DtLink");

        Reporter.log("Sending PUT request with body : \n"+updateGoldPattern.toString()+"\n", true);

        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(updateGoldPattern.toString()).
                when().put("/infrastructure/pattern/gold/"+patternId+"/update").
                then().statusCode(200);
    }

    @Test(dependsOnMethods = "disableGoldPattern")
    public void validateGoldPatternDisabled(){

        Reporter.log("Completed PUT request to Enable Flag \n", true);
        given(authToken)
                .when().get("/infrastructure/pattern/gold/"+patternId)
                .then().body("enabled",is(false));
    }

    @Test(dependsOnMethods = "validateGoldPatternDisabled")
    public void updateGoldPatternNotesById(){

        JSONObject updateNotes = JSONObject.fromObject(getGoldPatternById());
        updateNotes.put("notes", "test5");
        updateNotes.remove("DtLink");

        given(authToken).contentType("application/json").
                and().body(updateNotes.toString()).
                when().put("/infrastructure/pattern/gold/"+patternId+"/update").
                then().statusCode(200);
    }

    @Test(dependsOnMethods = "updateGoldPatternNotesById")
    public void validateNotesForGoldPattern(){

        given(authToken)
                .and()
                .when().get("/infrastructure/pattern/gold/"+patternId)
                .then().body("notes", containsString("test5"));
        /* TODO: Abstract value of notes to be updated in test data file*/
    }

    @Test(dependsOnMethods = "validateNotesForGoldPattern")
    public void updateGoldPatternWithNullId(){

        JSONObject updateId = JSONObject.fromObject(getGoldPatternById());
        updateId.put("id", "");
        updateId.remove("DtLink");

        Reporter.log(updateId + "\n", true);

        given(authToken).header("Content-Type", "application/json").
                and().body(updateId.toString()).
                when().put("/infrastructure/pattern/gold/"+patternId+"/update").
                then().statusCode(500).
                and().body("errorMessage", containsString("Unable to find gold pattern with id"));

        Reporter.log("Sent PUT request with blank Gold Pattern Id \n", true);
    }

    @Test(dependsOnMethods = "updateGoldPatternWithNullId")
    public void validateGoldPatternId(){

        given(authToken)
                .when().get("/infrastructure/pattern/gold/"+patternId)
                .then().body("id", containsString("G.1001.4"));

        Reporter.log("Validated that id value not updated \n", true);

    }

    @AfterClass
    public void resetGoldPatternAfterTest() {
        Reporter.log("Completed Tests in : " + getClass().toString(), true);

        String goldPattern = getGoldPatternById();
        JSONObject resetEnableFlag = JSONObject.fromObject(goldPattern);
        resetEnableFlag.put("enabled", true);
        resetEnableFlag.put("notes", "");
        resetEnableFlag.remove("DtLink");

        Reporter.log("Sending PUT request with body : \n"+resetEnableFlag.toString()+"\n", true);

        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(resetEnableFlag.toString()).
                when().put("/infrastructure/pattern/gold/"+patternId+"/update").
                then().statusCode(200);

    }
}
