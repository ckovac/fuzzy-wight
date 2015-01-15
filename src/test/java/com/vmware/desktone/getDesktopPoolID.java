package com.vmware.desktone;

import apple.laf.JRSUIConstants;
import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class getDesktopPoolID {

    RequestSpecification authToken;
    JSONArray poolJson;
    String response;
    int poolId;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");

        // Get all pools and find id for test pools
        response = given(authToken).when().get("/infrastructure/manager/pools").asString();
        poolJson = (JSONArray) JSONSerializer.toJSON(response);

        for (int i = 0; i < poolJson.size(); i++) {
            JSONObject pools = (JSONObject) poolJson.get(i);

            if (pools.getString("name").equalsIgnoreCase("ars-win-81-")) {
                System.out.println("Found valid pool with id: " + pools.getString("id"));
            //    poolId = new int[poolJson.size()];
                poolId = pools.getInt("id");
            }
        }
    }

    @Test
    public void getDesktopPoolGroups() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/"+poolId+"/groups").
                then().body("$", hasItem(hasEntry("name", "cn=ars_test_users,cn=users,dc=qa-tenantb,dc=dt,dc=vmware,dc=com")));
        // TODO: Move user groups to test data file.
    }

    @Test
    public void validatePatternIdForVM() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/"+poolId+"/vms").
                then().body("$", hasItem(hasEntry("patternId", "S.1001.2.5")));
    }

    @Test
    public void getDesktopPoolById() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/"+poolId+"").
                then().body("id", hasToString("1003"));
    }

    @Test
    public void validatePoolType() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/"+poolId).
                then().body("poolSessionType", hasToString("desktop"));
    }

    @Test
    public void getDesktopPoolUsers() {
        given(authToken).
                when().get("/infrastructure/pool/desktop/"+poolId+"/users").
                then().body("$", hasItem(hasEntry("loginName", "ars_test6")));
    }



    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
