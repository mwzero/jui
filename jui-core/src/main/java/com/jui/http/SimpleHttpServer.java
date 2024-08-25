package com.jui.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.jui.JuiPage;
import com.sun.net.httpserver.HttpServer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class SimpleHttpServer {
	
	static final int SERVER_PORT = 8000;
	
    public static void start(JuiPage page) throws IOException {
    	
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        
        //Router router = new Router(page);
        server.createContext("/css", new FileHandler());
        server.createContext("/js", new FileHandler());
        server.createContext("/html", new FileHandler());
        server.createContext("/send_get", new RequestHandler(page.getContext()));
        server.createContext("/send_post", new RequestHandler(page.getContext()));
        server.createContext("/jui", new PageHandler(page));
        
        server.createContext("/favicon.ico", new FileHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
        
        log.info("Server is running on port [{}]", SERVER_PORT);
    }


}
