package com.jui.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileHandler extends BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    	
        String endpoint = exchange.getRequestURI().getPath();
        log.debug("HTTP GET file[{}]", endpoint);
        
        
        InputStream is = getClass().getResourceAsStream(endpoint);
        if ( is == null ) {
        	
			log.error("HTTP GET [{}] not found", endpoint);
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