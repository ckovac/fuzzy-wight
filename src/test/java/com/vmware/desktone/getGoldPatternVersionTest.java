package com.vmware.desktone;

import com.jayway.restassured.matcher.ResponseAwareMatcher;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Response;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class getGoldPatternVersionTest {
    String goldPatternById;
    com.jayway.restassured.response.Cookie cookie;
    private Cookie userCookie;

    @BeforeClass
    public void loginAsUser() throws IOException {
        userCookie= LoginUser.loginUser();
        System.out.println("Starting Tests in : "+getClass().toString()+"\n");
    }

    @Test
    public void getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2").asString();

        System.out.println("Got Gold Pattern: \n"+goldPatternById+"\n");
    }

    @Test
    public void validateNotesForGoldPattern(){

        given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("version", greaterThanOrEqualTo(1000));

        System.out.println("Validated that version field has value greater than 1000. \n");

    }

    @AfterClass
    public void completedTest(){
        System.out.println("Completed Tests in : "+getClass().toString());
    }
}
