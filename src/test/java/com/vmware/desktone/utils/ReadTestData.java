package com.vmware.desktone.utils;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;


public class ReadTestData {

 public static JSONObject getTestDataFile() throws IOException{
     ClassLoader cl = ReadTestData.class.getClassLoader();
     InputStream is = cl.getResourceAsStream("testData.json");
     String data = IOUtils.toString(is);
     JSONObject testData = (JSONObject) JSONSerializer.toJSON(data);

     return testData;
 }

}
