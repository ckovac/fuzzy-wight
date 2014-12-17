package com.vmware.desktone.utils;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;


public class TestData {

 public static JSONObject getTestDataFile() throws IOException{
     ClassLoader cl = TestData.class.getClassLoader();
     InputStream is = cl.getResourceAsStream("testData.json");
     String data = IOUtils.toString(is);
     JSONObject env = (JSONObject) JSONSerializer.toJSON(data);

     return env;
 }

}
