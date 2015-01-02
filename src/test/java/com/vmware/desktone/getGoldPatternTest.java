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

public class getGoldPatternTest {

    RequestSpecification authToken;
    JSONArray goldPatternJson;
    String response;
    String patternId;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternJson = (JSONArray) JSONSerializer.toJSON(response);

        for (int i = 0; i < goldPatternJson.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternJson.get(i);

            if (pools.getString("name").equalsIgnoreCase("ars-win-81-64b")) {
                //    poolId = new int[poolJson.size()];
                patternId = pools.getString("id");
                System.out.println("Found valid gold Pattern with id: " + patternId);
            }
        }
    }

    @Test
    public void getVMForGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/dcs").then().body("$", hasItem(hasEntry("frontNetworkId", "1013")));
    }

    @Test
    public void getDesktopMgrTest() {
/*        given(authToken).
                when().get("/infrastructure/pattern/gold/G.1001.2/tenant/desktopmanagerid").
                then().body("$", not(isEmptyOrNullString()));*/

        given(authToken).header("Accept", "application/xml").
                when().get("/infrastructure/pattern/gold/"+patternId+"/tenant/desktopmanagerid").
                then().body("DtResult.String", hasToString("A6695644AE"));
    }

    @Test
    public void getPoolsForGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/pools").
                then().body("[0].sessionBased", equalTo(false));
    }

    @Test
    public void getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        String goldPatternById = given(authToken)
                .when().get("/infrastructure/pattern/gold/"+patternId).asString();

        System.out.println("Got Gold Pattern: \n"+goldPatternById+"\n");
    }

    @Test
    public void validateNotesForGoldPattern(){

        given(authToken).header("Accept", "application/json")
                .and()
                .when().get("/infrastructure/pattern/gold/"+patternId)
                .then().body("version", greaterThanOrEqualTo(1000));

        System.out.println("Validated that version field has value greater than 1000. \n");

    }

    @Test
    public void getVMNameForGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/pattern/gold/"+patternId+"/vm").
                then().body("name", hasToString("ars-win-81-64b"));

    }



    @Test
    public void validateTest() {
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
