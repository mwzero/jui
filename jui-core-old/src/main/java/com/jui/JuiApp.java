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
	
	//JUI Application - 
	public static final JuiApp jui = new JuiApp();
	
	//String juiResponse;
	
	WebContainer sidebar;
	
	JuiServer server;
	
	JuiSession session;
	
	protected JuiApp() {
		
		super("core");
		
		try {
			LogManager.getLogManager().readConfiguration(JuiApp.class.getResourceAsStream("/logging.properties"));
		} catch (SecurityException | IOException e) {
			log.log(Level.SEVERE, "Error reading logging properties file", e);
		}
		
		log.fine("JUI App: Start Initialization");
		sidebar = new WebContainer("sidebar");
		server = new JuiServer();
	}

}
