package com.jui;

import java.io.IOException;

import com.jui.http.SimpleHttpServer;

public class JuiCore {

	public static JuiPage jui = new JuiPage();
	
	public static void startJuiServer() {
		try {
			SimpleHttpServer.start(jui);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
