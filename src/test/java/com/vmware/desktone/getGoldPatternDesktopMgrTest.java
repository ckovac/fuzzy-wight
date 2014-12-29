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

public class getGoldPatternDesktopMgrTest {

    RequestSpecification authToken;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getDesktopMgrTest() {
/*        given(authToken).
                when().get("/infrastructure/pattern/gold/G.1001.2/tenant/desktopmanagerid").
                then().body("$", not(isEmptyOrNullString()));*/

        given(authToken).header("Accept", "application/xml").
                when().get("/infrastructure/pattern/gold/G.1001.2/tenant/desktopmanagerid").
                then().body("DtResult.String", hasToString("A6695644AE"));
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
