package com.jui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Utils {

	public static String getStackTraceAsString(Throwable throwable) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}

	public static String buildString(Object[] array) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            result.append("\"").append(array[i].toString()).append("\"");
            if (i < array.length - 1) {
                result.append(", ");  
            }
        }

        return result.toString();
    }
	
	public static String buildJsonString( LinkedHashMap<String, ?> map, String keyLabel, String valueLabel ) {
		
		JsonArray jobj = new JsonArray();
		for ( String key : map.keySet() ) {
			
			JsonObject item = new JsonObject();
			item.addProperty(keyLabel,key);
			item.add(valueLabel, new Gson().toJsonTree(map.get(key)));
			jobj.add(item);
		}
		
		return jobj.toString();
	}
}
