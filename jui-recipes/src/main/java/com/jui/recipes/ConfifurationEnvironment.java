package com.jui.recipes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfifurationEnvironment  {

	public static void settingProxy() {
		
		boolean useProxy=System.getenv("USE_PROXY").compareTo("1") == 0? true : false;
		String proxyUrl = System.getenv("PROXY_URL");
		String proxyPort = System.getenv("PROXY_PORT");
		String proxyUsername = System.getenv("PROXY_USERNAME");
        String proxyPassword = System.getenv("PROXY_PASSWORD");

        if ( useProxy ) {
        	
        	log.info("Setting proxy to[{}:{}]", proxyUrl, proxyPort );
        	System.setProperty("http.proxyHost", proxyUrl);
        	System.setProperty("http.proxyPort", proxyPort);
        	System.setProperty("https.proxyHost", proxyUrl);
        	System.setProperty("https.proxyPort", proxyPort);
        	System.setProperty("https.proxyUser", proxyUsername);
        	System.setProperty("https.proxyPassword", proxyPassword);
        	System.setProperty("http.proxyUser", proxyUsername);
        	System.setProperty("http.proxyPassword", proxyPassword);
        }
	}
}