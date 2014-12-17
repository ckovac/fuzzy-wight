package com.vmware.desktone;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Cookie;
import com.vmware.desktone.utils.ReadTestData;
import org.testng.annotations.*;
import java.io.IOException;

import net.sf.json.JSONObject;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import com.vmware.desktone.utils.*;


public class updateGoldPatternNotesTest extends ReadTestData {

    String goldPatternById;
    JSONObject jsonObject;
    com.jayway.restassured.response.Cookie cookie;
    private String str;
    private Cookie userCookie;

    @BeforeClass
    public void loginAsUser() throws IOException {
        userCookie=LoginUser.loginUser();
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
        updateNotes.put("notes", "test5");
        updateNotes.remove("DtLink");

        given().contentType("application/json").cookie(userCookie).
                and().body(updateNotes.toString()).
        when().put("/infrastructure/pattern/gold/G.1001.2/update").
        then().statusCode(200);
    }

    @Test
    public void validateNotesForGoldPattern(){

       given().header("Accept", "application/json")
               .and().cookie(userCookie)
       .when().get("/infrastructure/pattern/gold/G.1001.2")
       .then().body("notes", containsString("test5"));

    }
}
