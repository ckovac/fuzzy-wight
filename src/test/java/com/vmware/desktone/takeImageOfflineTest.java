package com.vmware.desktone;

import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.hasValue;

public class takeImageOfflineTest {

    RequestSpecification authToken;
    JSONArray goldPatternArray;
    String response;
    String patternId;
    private String goldPatternById;

    @BeforeClass
    public String getIdForGoldPattern() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternArray = JSONArray.fromObject(response);

        for (int i = 0; i < goldPatternArray.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternArray.get(i);

            if (pools.getString("name").equalsIgnoreCase("ars-win-81-64b")) {
                patternId = pools.getString("id");
                System.out.println("Found valid gold Pattern with id: " + patternId);
            } else {
                System.out.println("Could not find an expected Gold Pattern. Exiting Test");
                break;
            }
        }
        return patternId;
    }

    private String getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given(authToken).when().get("/infrastructure/pattern/gold/"+patternId).asString();
        System.out.println("Got Gold Pattern: \n"+goldPatternById+"\n");

        return goldPatternById;
    }

    @Test
    public void disableGoldPattern() {

        JSONObject updateGoldPattern = JSONObject.fromObject(getGoldPatternById());
        updateGoldPattern.put("enabled", false);
        updateGoldPattern.remove("DtLink");

        System.out.println("Sending PUT request with body : \n"+updateGoldPattern.toString()+"\n");

        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(updateGoldPattern.toString()).
                when().put("/infrastructure/pattern/gold/"+patternId+"/update").
                then().statusCode(200);
    }

    @Test
    public void getVMNameForGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/vm").
                then().body("name", hasValue("ars-win-81-64b"));

    }

    @Test
    public void getPlatformVersion(){

    }

    @Test
    public void getGoldPatterns(){
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("name", hasItems("ars-Win2012-RDS-GP","ars-win-81-64b", "ars-Win-81-Ent-64b-GP", "ars-Win2012-Non-RDS-GP"));

    }

    // @Test : Commented out since UI invokes this as a POST right now.
    public void getPatternsForId(){
        given(authToken).
                when().get("/infrastructure/pool/desktop/1005/patterns").
                then().body("desktopPoolId", hasValue("1005"));

    }

    @Test
    public void powerOnGoldPattern(){
        //Check patternId in response is same above.
        given(authToken).
                when().post("/infrastructure/vm/"+patternId+"/perform/POWERON").
                then().statusCode(200).and().body("patternId", hasValue("ars-win-81-64b"));


    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
