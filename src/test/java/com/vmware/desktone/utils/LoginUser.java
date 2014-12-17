package com.vmware.desktone.utils;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Cookie;
import net.sf.json.JSONObject;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.post;
import static com.vmware.desktone.utils.ReadTestData.*;


/**
 * Created by apoorvas on 12/15/14.
 */
public class LoginUser {

    static Cookie cookie;

    public static Cookie loginUser() throws IOException {
        JSONObject testData = getTestDataFile();

        JSONObject getEnvironment = (JSONObject)testData.get("environment");
        JSONObject getUser = (JSONObject)testData.get("user");

        String uri = getEnvironment.get("baseURI").toString();
        String path = getEnvironment.get("basePath").toString();

        String user = getUser.get("username").toString();
        String password = getUser.get("password").toString();
        String domain = getUser.get("domain").toString();

        RestAssured.baseURI = uri;
        RestAssured.basePath = path;

        RestAssured.config.getHttpClientConfig().reuseHttpClientInstance();
        // RestAssured.config.getSSLConfig().port(80);
        RestAssured.useRelaxedHTTPSValidation();

        cookie= post("/system/login?domain="+domain+"&user="+user+"&pw="+password).getDetailedCookie("JSESSIONID");

        return cookie;
    }
}
