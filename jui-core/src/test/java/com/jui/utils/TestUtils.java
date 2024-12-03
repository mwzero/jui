package com.jui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class TestUtils {
	
	//VM arguments -Djava.util.logging.config.file=src/test/resources/logging.properties
	public static void initializedLogManager() {
		
		try (InputStream inputStream = TestUtils.class.getResourceAsStream("/logging.properties")) {
		    LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			System.err.println("logging.properties file not loaded. Err: " + e.getMessage());
		}
	}

}
