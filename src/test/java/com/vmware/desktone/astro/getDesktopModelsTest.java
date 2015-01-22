package com.vmware.desktone.astro;

import com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.specification.RequestSpecification;
import com.vmware.desktone.utils.LoginUser;
import net.sf.json.JSONArray;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class getDesktopModelsTest {

    RequestSpecification authToken;
    JSONArray dataCentersArray;
    private String backBoneNetwork;
    Object[] desktopModelArray;

    @BeforeClass
    public void loginAsUser() throws IOException {
        authToken = LoginUser.loginUser();
        Reporter.log("Starting Tests in : " + getClass().toString() + "\n", true);

        backBoneNetwork = LoginUser.testData.getJSONObject("tenant").get("backBoneNetworkId").toString();
        desktopModelArray = LoginUser.testData.getJSONArray("desktopModels").toArray();
    }

    @Test
    public void validateBackBoneNetworkForDC() {
        given(authToken).
                when().get("/infrastructure/manager/dcs").
                then().body("$", hasItems(hasEntry("backBoneNetworkId", backBoneNetwork)));
        // TODO: Move out to test data file.
    }

    @Test
    public void getDesktopMgrForDataCenters() {

        /* This test does the following:
        Parse the Data Center JSON Array response to extract the Data Center ID
         */
        String response = given(authToken).when().get("/infrastructure/manager/dcs").asString();
        dataCentersArray = JSONArray.fromObject(response);

        int i = 0;
        while (i < dataCentersArray.size()) {
            String desktopMgrs = given(authToken).
                        when().get("/infrastructure/manager/tenant/desktopmanagersbydatacenter?dataCenterId="
                        + dataCentersArray.getJSONObject(0).get("id")).asString();

            // Then get Desktop Managers for each Data Center
            JSONArray desktopMgrsArray = JSONArray.fromObject(desktopMgrs);
            int j=0;

            // Parse the Desktop Manager JSON Array response to verify that Desktop Manager ID attribute value matches that in the GET Request.
            while (j < desktopMgrsArray.size()){
                boolean check = desktopMgrsArray.getJSONObject(j).get("dataCenterId").equals(dataCentersArray.getJSONObject(0).get("id"));
                if (check){
                    System.out.println("dataCenterId value in the desktopMgr Array: "+ desktopMgrsArray.getJSONObject(j).get("dataCenterId")+
                            " matches dataCenter : "+ dataCentersArray.getJSONObject(0).get("id") +" of GET request for " + (j+1)+ " Desktop Manager");
                } else {
                    System.out.println("dataCenterId value in the desktopMgr Array: "+ desktopMgrsArray.getJSONObject(j).get("dataCenterId")+
                            " matches dataCenter : "+ dataCentersArray.getJSONObject(0).get("id") +" of GET request for " + (j+1)+ " Desktop Manager");
                }
                j++;
            }
            i++;
        }
    }

    @Test
    public void getDesktopModels(){
        // Verifies names of Desktop Models returned by the Desktop Model JSONArray.
        given(authToken).
                when().get("/infrastructure/manager/models").
                then().body("name", hasItems(desktopModelArray));
    }


    @Test
    public void getDesktopQuotas(){
        // Verifies names of Desktop Models returned by the Desktop Model JSONArray.
        given(authToken).
                when().get("/infrastructure/manager/quotas").
                then().body("quota", hasItems(greaterThanOrEqualTo(0)));
    }


    @AfterClass
    public void completedTest() {
        System.out.println("Completed Tests in : " + getClass().toString());
    }
}
