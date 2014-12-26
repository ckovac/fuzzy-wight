package com.vmware.desktone;

import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class updateGoldPatternEnabledTest {

    String goldPatternById;
    private String str;
    private RequestSpecification authToken;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : "+getClass().toString()+"\n");
    }

    @Test
    public void getGoldPatterns() {
        given(authToken)
        .when().get("/infrastructure/manager/patterns?type=G")
        .then().body("name", hasItem("ars-win-81-64b"));

        System.out.println("Validated presence of Gold Pattern with Name ars-win-81-64b \n");
    }

    @Test
    public void getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given(authToken).when().get("/infrastructure/pattern/gold/G.1001.2").asString();

        System.out.println("Got Gold Pattern: \n"+goldPatternById+"\n");
    }

    @Test
    public void updateGoldPatternEnableFlagById(){

        JSONObject updateEnableFlag = JSONObject.fromObject(goldPatternById);
        updateEnableFlag.put("enabled", true);
        updateEnableFlag.remove("DtLink");

        System.out.println("Sending PUT request with body : \n"+updateEnableFlag.toString()+"\n");

        given(authToken).log().ifValidationFails().contentType("application/json").
                and().body(updateEnableFlag.toString()).
                when().put("/infrastructure/pattern/gold/G.1001.2/update").
                then().statusCode(200);

    }

    @Test
    public void validateEnableFlagForGoldPattern(){

        System.out.println("Completed PUT request to Enable Flag \n");

        given(authToken)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("enabled",is(true));

    }

    @AfterClass
    public void completedTest(){
        System.out.println("Validated that enabled flag updated with value true. \n ");

        System.out.println("Completed Tests in : "+getClass().toString()+"\n");
    }
}
