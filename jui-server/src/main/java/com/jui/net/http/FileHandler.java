package com.jui.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.java.Log;

@Log
public class FileHandler extends BaseHandler implements HttpHandler {

	String docRoot;
	
	public FileHandler(String docRoot) {
		this.docRoot = docRoot;
	}
	
    @Override
    public void handle(HttpExchange exchange) throws IOException {
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Oppure specifica un'origine specifica
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Content-Range, Content-Disposition, Content-Description");
		
		if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
		
        String endpoint = exchange.getRequestURI().getPath();
        log.fine("HTTP GET file[%s]".formatted(endpoint));
        
        if ( docRoot != "" ) endpoint = docRoot + endpoint.substring(1);
        
        InputStream is = getClass().getResourceAsStream("/" + endpoint);
        if ( is == null ) {
        	
			log.severe("HTTP GET [%s] not found".formatted(endpoint));
			HttpStatus.RESOURCE_NOT_FOUND(exchange, "Resource %s not found".formatted(endpoint));
			return;
		} 

        exchange.sendResponseHeaders(200, 0);
		OutputStream output = exchange.getResponseBody();
		final byte[] buffer = new byte[0x10000];
		int count = 0;
		while ((count = is.read(buffer)) >= 0) {
		    output.write(buffer, 0, count);
		}
		output.flush();
		output.close();
    }
    
}
