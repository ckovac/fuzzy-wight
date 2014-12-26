package com.vmware.desktone;

import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;

public class getGoldPatternPoolTest {

    RequestSpecification authToken;
    String val;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getVMForGoldPatternTest() {

         given(authToken).
                when().get("/infrastructure/pattern/gold/G.1001.2/pools").
                then().body("[0].sessionBased", equalTo(false));
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
