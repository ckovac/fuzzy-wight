package com.vmware.desktone;

import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class getPatternByTypeTest {

    RequestSpecification authToken;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getGoldPatternTest() {

/*        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("$", hasItem(hasEntry("name","ars-win-81-64b")));*/

        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("id", hasItems("G.1001.2","G.1001.4"));
    }

    @Test
    public void getStaticPatternTest() {
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=S").
                then().body("name", hasItems("ars-win-81-105", "ars-win-81-106"));
    }

    @Test
    public void getDynamicPatternTest() {
        given(authToken).
                when().get("/infrastructure/manager/patterns?type=D").
                then().body("id", hasItems("D.1001.4.1"));
        //TODO: See if this assertion can be set to something else.
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
