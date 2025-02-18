package com.jui.modules;

import static com.jui.JuiApp.jui;

import com.jui.modules.google.GoogleAuthenticator;

public class ServerTest {
	
	public static void main(String... args) {
		
		//jui.setTemplate("templates/bootstrap-simple");
		jui.markdown("## Server Tests");
		jui.divider().color("blue");
		
    	jui.server()
			.enableWebSocket(false)
			.enableHttps(false)
			.enableAuthentication(new GoogleAuthenticator(
					OAuth2Authentication.builder()
						.callbackUrl("https://127.0.0.1:8443/google/callback")
						.callbackUrlContext("/google/callback")
						.clientId(System.getenv("GOOGLE_CLIENT_ID"))
						.clientSecret(System.getenv("GOOGLE_CLIENT_SECRET"))
						.build()))
			.start();
	}
	

}
