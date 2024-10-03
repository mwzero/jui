package com.jui.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JuiServer {
	
	public static HttpServer start(String docRoot, boolean classLoading, String host, int port) {
		
		log.info("Starting JUI server. DocRoot[{}]", docRoot);
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", new FileHandler(docRoot));
			server.createContext("/jui", new JuiRequestHandler());
			server.createContext("/css", new FileHandler(docRoot));
	        server.createContext("/js", new FileHandler(docRoot));
	        server.createContext("/send_get", new RequestHandler());
	        server.createContext("/send_post", new RequestHandler());
	        server.createContext("/favicon.ico", new FileHandler(docRoot));

	        server.setExecutor(null); // creates a default executor
	        server.start();
	        
	        log.info("Server is running on port [{}]", port);
	        
		} catch (IOException e) {
			
			log.error("HttpServer not started", e.getLocalizedMessage());
		}
		
		return server;
        
		
	}

}
