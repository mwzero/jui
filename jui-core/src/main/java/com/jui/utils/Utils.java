package com.jui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

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

}
