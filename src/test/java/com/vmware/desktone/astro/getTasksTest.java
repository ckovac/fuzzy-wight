package com.vmware.desktone.astro;

import com.vmware.desktone.utils.LoginUser;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import static com.jayway.restassured.RestAssured.*;

import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

public class getTasksTest {

    RequestSpecification authToken;
    JSONObject tf = new JSONObject();
    String tasksURI = "/task/manager/tasks";
    String taskFilterURI = "/task/manager/filter";
    String taskLimit = "5";
    Long   poolId = 1007L;
    String taskStatus = "FAILED";

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        System.out.println("Starting Tests in : " + getClass().toString() + "\n");
    }

    @Test
    public void getTaskFilter() {

        given(authToken).when().get(taskFilterURI).
        then().
                statusCode(HttpStatus.SC_OK).
                log().body();
    }

    @Test
    public void getTasksByEmptyFilter() {

        given(authToken).log().ifValidationFails().contentType(ContentType.JSON).
                and().body(tf).
                when().post(tasksURI).
                then().statusCode(HttpStatus.SC_OK);

        System.out.println("Sent POST request with empty body: " + tf + "\n");

    }

    @Test
    public void getTasksByLimit() {

        tf.put("limit", taskLimit);

        given(authToken).log().ifValidationFails().contentType(ContentType.JSON).
                and().body(tf).
                when().post(tasksURI).
                then().statusCode(HttpStatus.SC_OK);

        System.out.println("Sent POST request with limit:" + taskLimit + "\n");

   }

    @Test
    public void getTasksByPoolId() {

        tf.put("poolId",Collections.singleton(poolId).toArray());

        given(authToken).log().ifValidationFails().contentType(ContentType.JSON).
                and().body(tf).
                when().post(tasksURI).
                then().statusCode(HttpStatus.SC_OK);

        System.out.println("Sent POST request with poolId: " + poolId.toString() + "\n");
    }

    @Test
    public void getTasksByStatus() {

        tf.put("taskStatus",Collections.singleton(taskStatus).toArray());

        given(authToken).log().ifValidationFails().contentType(ContentType.JSON).
                and().body(tf).
                when().post(tasksURI).
                then().statusCode(HttpStatus.SC_OK);

        System.out.println("Sent POST request with status:" + taskStatus + "\n");
    }

    @Test
    public void getTasksByFromDate() {

        Calendar fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DAY_OF_YEAR, -7); // One week old
        fromDate.set(Calendar.HOUR_OF_DAY, 0);
        fromDate.set(Calendar.MINUTE, 0);
        fromDate.set(Calendar.SECOND, 0);
        fromDate.set(Calendar.MILLISECOND, 0);
        tf.put("fromDate", fromDate.get(Calendar.DATE));

        given(authToken).log().ifValidationFails().contentType(ContentType.JSON).
                and().body(tf).
                when().post(tasksURI).
                then().statusCode(HttpStatus.SC_OK);

        System.out.println("Sent POST request with fromDate:" + fromDate.getTime().toString()+ "\n");
    }

    @Test
    public void getTasksByFromAndToDate() {

        Calendar fromDate = Calendar.getInstance();
        fromDate.set(Calendar.YEAR, 2014);
        fromDate.set(Calendar.MONTH, Calendar.DECEMBER);
        fromDate.set(Calendar.DAY_OF_MONTH, 29);
        fromDate.set(Calendar.HOUR_OF_DAY, 0);
        fromDate.set(Calendar.MINUTE, 0);
        fromDate.set(Calendar.SECOND, 0);
        fromDate.set(Calendar.MILLISECOND, 0);
        Calendar toDate = Calendar.getInstance();
        toDate.set(Calendar.YEAR, 2015);
        toDate.set(Calendar.MONTH, Calendar.JANUARY);
        toDate.set(Calendar.DAY_OF_MONTH, 1);
        toDate.set(Calendar.HOUR_OF_DAY, 0);
        toDate.set(Calendar.MINUTE, 0);
        toDate.set(Calendar.SECOND, 0);
        toDate.set(Calendar.MILLISECOND, 0);
        tf.put("fromDate", fromDate.get(Calendar.DATE));
        tf.put("toDate", toDate.get(Calendar.DATE));

        given(authToken).log().ifValidationFails().contentType(ContentType.JSON).
                and().body(tf).
                when().post(tasksURI).
                then().statusCode(HttpStatus.SC_OK);

        System.out.println("Sent POST request with fromDate and toDate:" + fromDate.getTime().toString() + " and " + toDate.getTime().toString()+ "\n");
    }

    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }

}
