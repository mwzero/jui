package com.jui.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.jui.net.handlers.HandlerBase;
import com.jui.net.handlers.HandlerFile;
import com.jui.net.handlers.HandlerRequest;
import com.jui.net.handlers.IHandlerWebSocket;
import com.sun.net.httpserver.HttpServer;

import lombok.Builder;
import lombok.extern.java.Log;

@Log
@Builder
public class JuiServer {
	
	HandlerBase juiHttpHandler;
	IHandlerWebSocket juiWebSocketHandler;
	
	String docRoot;
	boolean classLoading;
	String host;
	int port;
	int wssPort;
	boolean useWSS;
	
	public void start() {
		
		log.info("Starting JUI server. DocRoot[%s]".formatted(docRoot));
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			
			server.createContext("/", new HandlerFile(docRoot));
			server.createContext("/jui", juiHttpHandler);
			server.createContext("/css", new HandlerFile(docRoot));
	        server.createContext("/js", new HandlerFile(docRoot));
	        server.createContext("/send_get", new HandlerRequest());
	        server.createContext("/send_post", new HandlerRequest());
	        server.createContext("/favicon.ico", new HandlerFile(docRoot));

	        server.setExecutor(null); // creates a default executor
	        server.start();
	        
	        if ( useWSS) {

	        	
		        JuiSimpleWebSocketServer wss = JuiSimpleWebSocketServer.create(new InetSocketAddress(wssPort), 8025);
				wss.createContext("/ws/jui", juiWebSocketHandler);
		        wss.start();    
		        log.info("WSS listening on port[%d]".formatted(wssPort));
	        }
	        
	        log.info("Server is running on port [%s]".formatted(port));
	        
		} catch (IOException e) {
			
			log.severe("HttpServer not started. Error[%s]".formatted(e.getLocalizedMessage()));
		}
	}
		

}
