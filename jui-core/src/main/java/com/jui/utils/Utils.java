package com.jui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                result.append(", ");  // Aggiungo la virgola tra gli elementi
            }
        }

        return result.toString();
    }
	
	public static String buildJsonString( LinkedHashMap<String, ?> map, String keyLabel, String valueLabel ) throws JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
	    
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		boolean notFirst = false;
		for ( String key : map.keySet() ) {
			if ( notFirst) sb.append(",");
			sb.append("""
					{
						"%s" :"%s", "%s": [%s]
					}
					""".formatted(keyLabel, key, valueLabel, objectMapper.writeValueAsString(map.get(key))));
			notFirst = true;
		}
		sb.append("]");
		
		return sb.toString();
	}

}
