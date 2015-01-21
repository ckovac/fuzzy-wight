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

public class renameImagesTest {

    RequestSpecification authToken;
    JSONArray goldPatternJson;
    String response;
    String patternId;
    private String initialImageName;
    private String modifiedImageName;

    @BeforeClass
    public void getGoldPatternId() throws IOException {
        authToken = LoginUser.loginUser();
        initialImageName = LoginUser.testData.getJSONObject("renameGoldPattern").getString("initialName");
        modifiedImageName = LoginUser.testData.getJSONObject("renameGoldPattern").getString("modifiedName");
        Reporter.log("Starting Tests in : " + getClass().toString() + "\n", true);

        // Get all Gold Patterns and find id for Gold Pattern
        response = given(authToken).when().get("/infrastructure/manager/patterns?type=G").asString();
        goldPatternJson = (JSONArray) JSONSerializer.toJSON(response); //TODO: Refactor to use fromObject to serialize into JSONArray.

        for (int i = 0; i < goldPatternJson.size(); i++) {
            JSONObject pools = (JSONObject) goldPatternJson.get(i);

            if (pools.getString("name").equalsIgnoreCase(initialImageName)) {
                patternId = pools.getString("id");
                Reporter.log("Found valid gold Pattern with id: " + patternId, true);
            } else {
                Reporter.log("Could not find an expected Gold Pattern. Exiting Test", true);
                i++;
            }
//            i = goldPatternJson.size();
        }
    }

    @Test
    public void renameImage(){
        given(authToken).log().ifValidationFails().
                when().put("/infrastructure/pattern/gold/"+patternId+"/rename?name="+modifiedImageName).
                then().statusCode(200);
    }

    @Test(dependsOnMethods = "renameImage")
    public void validateImageNameAfterRename(){

        given(authToken)
                .when().get("/infrastructure/pattern/gold/"+patternId)
                .then().body("name", hasToString(modifiedImageName));
    }


    @AfterClass
    public void resetImageAfterTest() {
        Reporter.log("Completed Tests in : " + getClass().toString(), true);

        given(authToken).log().ifValidationFails().
                when().put("/infrastructure/pattern/gold/"+patternId+"/rename?name="+initialImageName).
                then().statusCode(200);

    }
}