package com.vmware.desktone.astro;

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
    String getVM;

    @BeforeClass
    public String getIdForGoldPattern() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternArray = JSONArray.fromObject(response);

        for (int i = 0; i < goldPatternArray.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternArray.get(i);

            if (pools.getString("name").equalsIgnoreCase("ars-Win2012-Non-RDS-GP")) {
                patternId = pools.getString("id");
                System.out.println("Found valid gold Pattern with id: " + patternId);
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

    @Test(dependsOnMethods = "disableGoldPattern")
    public void getVMNameForGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/vm").
                then().body("name", hasToString("ars-Win2012-Non-RDS-GP")).toString();

        getVM = given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/vm").
                prettyPrint();
    }

    // @Test
    public void getPlatformVersion(){

    }

    @Test(dependsOnMethods = "getVMNameForGoldPatternTest")
    public void getGoldPatterns(){
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("name", hasItems("ars-Win2012-RDS-GP","ars-win-81-64b","ars-Win2012-Non-RDS-GP"));

    }

    // @Test : Commented out since UI invokes this as a POST right now.
    public void getPatternsForId(){
        given(authToken).
                when().get("/infrastructure/pool/desktop/1005/patterns").
                then().body("desktopPoolId", hasValue("1005"));

    }

    @Test(dependsOnMethods = "getGoldPatterns")
    public void powerOnGoldPattern(){
        //Check patternId in response is same above.
        given(authToken).body(getVM).accept("application/json").contentType("application/json").log().everything().
                when().post("/infrastructure/vm/"+patternId+"/perform/POWERON").
                then().statusCode(200).and().body("name", hasToString("ars-Win2012-Non-RDS-GP"));


    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
