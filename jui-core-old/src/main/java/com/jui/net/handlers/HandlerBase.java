package com.jui.net.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.java.Log;

@Log
public abstract class HandlerBase implements HttpHandler {
	
	
	class HttpStatus {
		
		public static void writeResponse(HttpExchange exchange, int httpStatus, String response) throws IOException {
		
	        exchange.sendResponseHeaders(httpStatus, response.length());
	        OutputStream output = exchange.getResponseBody();
	        output.write(response.getBytes());
	        output.flush();
	        output.close();
		}
		
		public static void RESOURCE_NOT_FOUND(HttpExchange exchange, String response) throws IOException {
			
	        exchange.sendResponseHeaders(404, response.length());
	        OutputStream output = exchange.getResponseBody();
	        output.write(response.getBytes());
	        output.flush();
	        output.close();
		}
		
        
	}
	
	protected  Map<String, String> queryToMap(HttpExchange exchange) {
		
		String endpoint = exchange.getRequestURI().getPath();
        String queryParameters =  exchange.getRequestURI().getQuery();
        log.fine("Managing endpoint[%s] parameters[%s]".formatted(endpoint, queryParameters));
        
        return queryToMap(queryParameters);
	}
	
	protected Map<String, String> queryToMap(String query) {
        
		if(query == null) {
			log.warning("Query params is null");
            return null;
        }
        
		return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                    entry -> entry[0], 
                    entry -> entry.length > 1 ? entry[1] : ""
                ));
		
    }
	
	
	
	
	protected URL getFileFromClassLoader (String fileName) throws IOException {
    	
		return HandlerFile.class.getResource(fileName);
		
	}

}
