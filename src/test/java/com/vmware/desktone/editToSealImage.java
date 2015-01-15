package com.vmware.desktone;

import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class editToSealImage {

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

            if (pools.getString("name").equalsIgnoreCase("ars-Win-81-Ent-64b-GP")) {
                patternId = pools.getString("id");
                System.out.println("Found valid gold Pattern with id: " + patternId);
            }
        }
        return patternId;
    }

    @Test
    public void getGoldPatternById() {
        goldPatternById = given(authToken).when().get("/infrastructure/pattern/gold/"+patternId).asString();
        System.out.println("Got Gold Pattern: \n"+goldPatternById+"\n");
    }

    @Test
    public void verifyDaasAgentAvailable() throws InterruptedException {
        int count = 0;
        boolean flag = false;

        if (flag != true) {
            given(authToken).when().get("/infrastructure/vm/"+patternId).then()
                    .body("daaSAgentState", is("ACTIVE"));
            flag = true;
        } else {
            while (count <=10){
                Thread.sleep(5000);
                given(authToken).when().get("/infrastructure/vm/"+patternId).then()
                        .body("daaSAgentState", is("ACTIVE"));
                flag = true;
                count++;
            }
        }
    }

    @Test
    public void getGoldPatternTest() {
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("id", hasItem(patternId));
    }

    // @Test : TODO: Correct test after taking latest builds where POST changed to GET
    public void getPatterns(){
        given(authToken).
                when().get("/infrastructure/pool/desktop/1001/patterns").
                then().body("id", hasItem(patternId));
    }

    @Test
    public void validateGoldPatternReadiness(){
        given(authToken).
                when().get("/infrastructure/pattern/static/"+patternId+"/validateForGoldPattern").
                then().statusCode(200);
    }

    @Test
    public void convertToGoldPattern(){
        // https://10.31.15.81/dt-rest/v100/pool/manager/convert/gold?spid=G.1001.6&cn=vmw&key=&au=administrator&ap=Desktone1&tz=EST
        given(authToken).
                when().get("/pool/manager/convert/gold?spid="+patternId+"&cn=vmw&key=&au=administrator&ap=Desktone1&tz=EST").
                then().body("type", hasToString("ConvertGoldPattern"));

        int percentageComplete=0;
        given(authToken).
                when().get("/pool/manager/convert/gold?spid="+patternId+"&cn=vmw&key=&au=administrator&ap=Desktone1&tz=EST").
                then().body("percentageComplete", lessThan(100));

        /* Call https://10.31.15.81/dt-rest/v100/infrastructure/pool/task/46 with id from previous call
        under percentageComplete=100*/
    }

    @Test
    public void repeatGetGoldPattern() {
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("id", hasItem(patternId));
    }

    // @Test : TODO: Correct test after taking latest builds where POST changed to GET
    public void repeatGetPatterns(){
        given(authToken).
                when().get("/infrastructure/pool/desktop/1001/patterns").
                then().body("id", hasItem(patternId));
    }

    // @Test : TODO : Find a way to call system/platform and add validations
    public void getPlatformVersionDetails(){
        // https://10.31.15.81
        given(authToken).
                when().get("/dt-rest/system/platform");
    }


    // NOW Publish Gold Pattern Test Steps
    @Test
    public void publishGoldPattern(){

        getGoldPatternById(); //TODO : Change method to return Gold Pattern object
        Object GP = null;
        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(GP /*Pass Gold Pattern object by ID*/).
                when().put("/infrastructure/pattern/gold/G.1001.6/update").
                then().statusCode(200);
    }

    @Test
    public void checkStateForGoldPatternVM(){
        /*GET https://10.31.15.81/dt-rest/v100/infrastructure/pattern/gold/G.1001.6/vm
        * and check that daaSAgentState: "INACTIVE" and vmpowerState: "POWERED_OFF"*/
    }

    // @Test : TODO : Verify if required here.
    public void reGetPlatformVersionDetails(){
        // https://10.31.15.81
        given(authToken).
                when().get("/dt-rest/system/platform");
    }

    @Test
    public void reRepeatGetGoldPattern() {
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("id", hasItem(patternId));
    }

    // @Test : TODO: Correct test after taking latest builds where POST changed to GET
    public void reRepeatGetPatterns(){
        given(authToken).
                when().get("/infrastructure/pool/desktop/1001/patterns").
                then().body("id", hasItem(patternId));
    }


    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
