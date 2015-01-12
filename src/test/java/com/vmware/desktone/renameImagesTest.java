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

public class renameImagesTest {

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
            } else {
                System.out.println("Could not find an expected Gold Pattern. Exiting Test");
                break;
            }
        }
    }

    @Test
    public void renameImage(){

        given(authToken).log().ifValidationFails().
                when().put("/infrastructure/pattern/gold/G.1001.2/rename?name=ars-win-81-renamed").
                then().statusCode(200);
    }

    @Test(dependsOnMethods = "renameImage")
    public void validateImageNameAfterRename(){

        given(authToken)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("name", hasValue("ars-win-81-renamed"));
    }


    @AfterClass
    public void resetImageAfterTest() {
        System.out.println("Completed Tests in : " + getClass().toString());

        given(authToken).log().ifValidationFails().
                when().put("/infrastructure/pattern/gold/G.1001.2/rename?name=ars-win-81-64b").
                then().statusCode(200);

    }
}