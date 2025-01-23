package com.jui.apis;

import static com.jui.JuiApp.jui;

public class ServerTest {
	
	public static void main(String... args) {
		
		//jui.setTemplate("templates/bootstrap-simple");
		jui.markdown("## Server Tests");
		jui.divider().color("blue");
    	
    	jui.server()
			.enableWebSocket(false)
			.enableHttps(false)
			.enableAuthentication(true)
			.start();
	}
	

}
