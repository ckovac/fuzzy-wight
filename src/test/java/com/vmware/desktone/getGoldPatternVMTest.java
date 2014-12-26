package com.vmware.desktone;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.restassured.specification.RequestSpecification;
import com.sun.media.sound.SoftAbstractResampler;
import com.vmware.desktone.utils.LoginUser;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class getGoldPatternVMTest {

    RequestSpecification authToken;


    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getVMForGoldPatternTest() {

        given(authToken).log().ifValidationFails().
                when().get("/infrastructure/pattern/gold/G.1001.2/vm").
                then().body("name", hasToString("ars-win-81-64b"));

    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
