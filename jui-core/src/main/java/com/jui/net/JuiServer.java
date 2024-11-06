package com.jui.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.jui.net.http.BaseHandler;
import com.jui.net.http.FileHandler;
import com.jui.net.http.RequestHandler;

import com.jui.net.wss.EchoWebSocketHandler;
import com.jui.net.wss.SimpleWebSocketServer;
import com.jui.net.wss.WebSocketHandler;
import com.sun.net.httpserver.HttpServer;

import lombok.Builder;
import lombok.extern.java.Log;

@Log
@Builder
public class JuiServer {
	
	BaseHandler juiHttpHandler;
	WebSocketHandler juiWebSocketHandler;
	
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
			
			server.createContext("/", new FileHandler(docRoot));
			server.createContext("/jui", juiHttpHandler);
			server.createContext("/css", new FileHandler(docRoot));
	        server.createContext("/js", new FileHandler(docRoot));
	        server.createContext("/send_get", new RequestHandler());
	        server.createContext("/send_post", new RequestHandler());
	        server.createContext("/favicon.ico", new FileHandler(docRoot));

	        server.setExecutor(null); // creates a default executor
	        server.start();
	        
	        if ( useWSS) {

	        	
		        SimpleWebSocketServer wss = SimpleWebSocketServer.create(new InetSocketAddress(wssPort), 8025);
				wss.createContext("/echo", new EchoWebSocketHandler());
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
