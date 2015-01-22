package com.vmware.desktone.astro;

import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import com.vmware.desktone.utils.ReadTestData;
import net.sf.json.JSONArray;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static com.vmware.desktone.utils.ReadTestData.getTestDataFile;
import static org.hamcrest.Matchers.*;

public class showImagesTest {

    RequestSpecification authToken;
    JSONArray goldPatterns;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        goldPatterns = getTestDataFile().getJSONArray("goldPatterns");
        Reporter.log("Starting Tests in : " + getClass().toString() + "\n", true);
    }

    @Test
    public void getGoldPatternTest() {

        given(authToken).
                when().get("/infrastructure/manager/patterns?type=G").
                then().body("name", hasItems(goldPatterns.toArray()));
    }

/*  @Test : Commented out since UI invokes this as a POST right now. Need to
    public void getPatterns() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/1005/patterns").
                then().body("desktopPoolId", hasValue("1005"));
    }*/

    @AfterClass
    public void completedTest() {
        Reporter.log("Completed Tests in : " + getClass().toString(), true);
    }
}
