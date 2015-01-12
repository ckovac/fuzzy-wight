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

public class showImagesTest {

    RequestSpecification authToken;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("name", hasItems("ars-Win2012-RDS-GP","ars-win-81-64b", "ars-Win-81-Ent-64b-GP", "ars-Win2012-Non-RDS-GP"));

        // "ars-Win2012-RDS-GP","ars-win-81-64b", "ars-Win-81-Ent-64b-GP", "ars-Win2012-Non-RDS-GP"

    }

    // @Test : Commented out since UI invokes this as a POST right now. Need to
    public void getPatterns() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/1005/patterns").
                then().body("desktopPoolId", hasValue("1005"));
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
