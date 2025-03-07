package com.jui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;

import com.jui.html.WebContainer;
import com.jui.net.JuiServer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class JuiApp extends WebContainer {
	
	public static final JuiApp jui = new JuiApp();
	
	String juiResponse;
	
	//useful for page structure
	WebContainer sidebar;
	JuiAppSettings page;
	
	//Jui Http/Https/WSS Server
	JuiServer server;
	
	//Jui Session: user information
	JuiSession session;
	
	protected JuiApp() {
		
		super("core");
		
		try {
			//LogManager.getLogManager().readConfiguration(new FileInputStream("./logging.properties"));
			LogManager.getLogManager().readConfiguration(JuiApp.class.getResourceAsStream("/logging.properties"));
		} catch (SecurityException | IOException e) {
			log.log(Level.SEVERE, "Error reading logging properties file", e);
		}
		
		log.fine("JUI App: Start Initialization");
		sidebar = new WebContainer("sidebar");
		page =  new JuiAppSettings();
		server = new JuiServer(page);

	}

}
