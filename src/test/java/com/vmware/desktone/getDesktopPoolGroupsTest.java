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

public class getDesktopPoolGroupsTest {

    RequestSpecification authToken;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getDesktopPoolGroups() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/1003/groups").
                then().body("$", hasItem(hasEntry("name", "cn=ars_test_users,cn=users,dc=qa-tenantb,dc=dt,dc=vmware,dc=com")));
    }

    @Test
    public void validateTest() {
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}