package com.jui;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.jui.http.FileHandler;
import com.jui.http.JuiWebApplicationHandler;
import com.jui.http.RequestHandler;
import com.sun.net.httpserver.HttpHandler;

public class Router {
	
    private final Map<String, HttpHandler> routes = new HashMap<>();
    
    public Router(JuiWebApplication application) {
    	
    	addRoute("/css", new FileHandler());
    	addRoute("/js", new FileHandler());
    	addRoute("/html", new FileHandler());
    	addRoute("/send_get", new RequestHandler());
    	addRoute("/send_post", new RequestHandler());
    	addRoute("/", new JuiWebApplicationHandler(application));
    }

    public void addRoute(String path, HttpHandler handler) {
        routes.put(path, handler);
    }

    public HttpHandler getHandler(String path) {
        return routes.getOrDefault(path, exchange -> {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
    }
}
