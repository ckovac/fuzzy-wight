package com.vmware.desktone;

import com.jayway.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import java.io.IOException;

import net.sf.json.JSONObject;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import com.vmware.desktone.utils.*;


public class updateGoldPatternNotesTest {

    String goldPatternById;
    private RequestSpecification authToken;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken=LoginUser.loginUser();
        System.out.println("Starting Tests in : "+getClass().toString()+"\n");
    }

    @Test
    public void getGoldPatternById(){
        // Get Gold Pattern by Id and create JSON to update Gold Pattern
        goldPatternById = given(authToken)
                .and()
                .when().get("/infrastructure/pattern/gold/G.1001.2").asString();

    }

    @Test
    public void updateGoldPatternNotesById(){

        JSONObject updateNotes = JSONObject.fromObject(goldPatternById);
        updateNotes.put("notes", "test5");
        updateNotes.remove("DtLink");

        given(authToken).contentType("application/json").
                and().body(updateNotes.toString()).
        when().put("/infrastructure/pattern/gold/G.1001.2/update").
        then().statusCode(200);
    }

    @Test
    public void validateNotesForGoldPattern(){

       given(authToken)
               .and()
       .when().get("/infrastructure/pattern/gold/G.1001.2")
       .then().body("notes", containsString("test5"));

    }

    @AfterClass
    public void completedTest(){
        System.out.println("Completed Tests in : "+getClass().toString()+"\n");
    }
}
