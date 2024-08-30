package com.jui.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.jui.JuiWebApplication;
import com.sun.net.httpserver.HttpServer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class SimpleHttpServer {
	
	static final int SERVER_PORT = 8000;
	
    public static void start(JuiWebApplication application) throws IOException {
    	
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        
        //Router router = new Router(page);
        server.createContext("/css", new FileHandler());
        server.createContext("/js", new FileHandler());
        server.createContext("/html", new FileHandler());
        server.createContext("/send_get", new RequestHandler());
        server.createContext("/send_post", new RequestHandler());
        server.createContext("/jui", new JuiWebApplicationHandler(application));
        
        server.createContext("/favicon.ico", new FileHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
        
        log.info("Server is running on port [{}]", SERVER_PORT);
    }


}
