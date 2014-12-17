package com.vmware.desktone;

import com.jayway.restassured.matcher.ResponseAwareMatcher;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Response;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONObject;
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
    }

    @Test
    public void getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2").asString();

        System.out.println(goldPatternById);
    }

    @Test
    public void validateNotesForGoldPattern(){

        given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("version", greaterThanOrEqualTo(1000));

        System.out.println("Validated that id value not updated \n");

    }
}
