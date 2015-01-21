package com.vmware.desktone.utils;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.specification.RequestSpecification;
import net.sf.json.JSONObject;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.post;
import static com.jayway.restassured.RestAssured.with;
import static com.vmware.desktone.utils.ReadTestData.*;


/**
 * Created by apoorvas on 12/15/14.
 */
public class LoginUser {
    public static JSONObject testData;

    public static RequestSpecification loginUser() throws IOException {
        testData = getTestDataFile();

        RestAssured.baseURI = testData.getJSONObject("environment").get("baseURI").toString();
        RestAssured.basePath = testData.getJSONObject("environment").get("basePath").toString();

        RestAssured.config.getHttpClientConfig().reuseHttpClientInstance();
        // RestAssured.config.getSSLConfig().port(80);
        RestAssured.useRelaxedHTTPSValidation();

        String user =  testData.getJSONObject("user").get("username").toString();
        String password = testData.getJSONObject("user").get("password").toString();
        String domain = testData.getJSONObject("user").get("domain").toString();

        String auth= post("/system/login?domain=" + domain + "&user=" + user + "&pw=" + password).getHeader("Authorization");
        String csrf_header= post("/system/login?domain=" + domain + "&user=" + user + "&pw=" + password).getHeader("x-dt-csrf-header");

        RequestSpecification requestSpec = with().headers("Authorization", auth, "Accept", "application/json", "x-dt-csrf-header", csrf_header);
        return requestSpec;
    }
}
