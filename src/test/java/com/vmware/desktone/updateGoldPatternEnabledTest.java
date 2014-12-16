package com.vmware.desktone;

import com.jayway.restassured.response.Cookie;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


public class updateGoldPatternEnabledTest {

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
    public void getGoldPatterns() {

        given().header("Accept", "application/json")
                .cookie(userCookie).when().get("/infrastructure/manager/patterns?type=G").then().
                body("name[0]", containsString("ars-win-81-64b"));

    }

    @Test
    public void getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2").asString();

    }

    @Test
    public void updateGoldPatternEnableFlagById(){

        JSONObject updateEnableFlag = JSONObject.fromObject(goldPatternById);
        updateEnableFlag.put("enabled", true);
        updateEnableFlag.remove("DtLink");

        System.out.println(updateEnableFlag.toString());

        given().contentType("application/json").cookie(userCookie).
                and().body(updateEnableFlag.toString()).
                when().put("/infrastructure/pattern/gold/G.1001.2/update").
                then().statusCode(200);

    }

    @Test
    public void validateEnableFlagForGoldPattern(){

        Boolean flag = false;
        given().header("Accept", "application/json")
                .and().cookie(userCookie)
                .when().get("/infrastructure/pattern/gold/G.1001.2")
                .then().body("enabled",is(true));

        //    System.out.println(val);

    }
}
