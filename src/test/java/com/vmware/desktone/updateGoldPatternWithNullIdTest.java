package com.vmware.desktone;

import com.jayway.restassured.response.Cookie;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONObject;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;


public class updateGoldPatternWithNullId {
    String goldPatternById;
    JSONObject jsonObject;
    com.jayway.restassured.response.Cookie cookie;
    private String str;
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
    }

    @Test
    public void updateGoldPatternNotesById(){

        JSONObject updateNotes = JSONObject.fromObject(goldPatternById);
        updateNotes.put("id", "");
        updateNotes.remove("DtLink");

        System.out.println(updateNotes + "\n");

        given().header("Content-Type", "application/json").accept("application/json").cookie(userCookie).
                and().body(updateNotes.toString()).
                when().put("/infrastructure/pattern/gold/G.1001.2/update").
                then().statusCode(500).
                and().body("errorMessage", containsString("Unable to find gold pattern with id"));

        System.out.println("Sent PUT request with blank Gold Pattern Id \n");
    }

    @Test
    public void validateNotesForGoldPattern(){

        given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("id", containsString("G.1001.2"));

        System.out.println("Validated that id value not updated \n");

    }
}
