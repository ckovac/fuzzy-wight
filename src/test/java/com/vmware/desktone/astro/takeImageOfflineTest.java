package com.vmware.desktone.astro;

import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasToString;

public class takeImageOfflineTest {

    RequestSpecification authToken;
    JSONArray goldPatternArray;
    String response;
    String patternId;
    private String goldPatternById;
    String getVM;
    private String modifyGoldPatternName;

    @BeforeClass
    public void getIdForGoldPattern() throws IOException {
        authToken = LoginUser.loginUser();
        Reporter.log("Starting Tests in : " + getClass().toString() + "\n", true);

        modifyGoldPatternName = LoginUser.testData.getString("editGoldPattern");

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternArray = JSONArray.fromObject(response);

        for (int i = 0; i < goldPatternArray.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternArray.get(i);

            if (pools.getString("name").equalsIgnoreCase(modifyGoldPatternName)) {
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
    public void disableGoldPattern() {

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
    public void getVMNameForGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/vm").
                then().body("name", hasToString(modifyGoldPatternName));

        getVM = given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/vm").
                prettyPrint();
    }

/*  @Test
    public void getPlatformVersion(){

    }*/

    @Test(dependsOnMethods = "getVMNameForGoldPatternTest")
    public void getGoldPatterns(){
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("name", hasItems("ars-Win2012-RDS-GP","ars-win-81-64b","ars-Win2012-gp"));

    }

/*  @Test : Commented out since UI invokes this as a POST right now.
    public void getPatternsForId(){
        given(authToken).
                when().get("/infrastructure/pool/desktop/1005/patterns").
                then().body("desktopPoolId", hasValue("1005"));

    }*/

    @Test(dependsOnMethods = "getGoldPatterns")
    public void powerOnGoldPattern(){
        //Check patternId in response is same above.
        given(authToken).body(getVM).accept("application/json").contentType("application/json").log().everything().
                when().post("/infrastructure/vm/"+patternId+"/perform/POWERON").
                then().statusCode(200).and().body("name", hasToString(modifyGoldPatternName));


    }

    @AfterClass
    public void completedTest() {
        Reporter.log("Completed Tests in : " + getClass().toString(), true);
    }
}
