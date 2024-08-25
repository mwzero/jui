package com.jui.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileHandler extends BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    	
        String endpoint = exchange.getRequestURI().getPath().substring(1);
        File file = null;
		try {
			file = Paths.get(getFileFromClassLoader(endpoint).toURI()).toFile();
			
		} catch (URISyntaxException | IOException e) {
		
			log.error("Request handling Err[{}]", e.getLocalizedMessage());
			
			HttpStatus.writeResponse(exchange, 500, getErrorPage(e));
			return;
		}

        
        if (file == null) {
        	
        	HttpStatus.writeResponse (exchange, 404, "Error 404 File not found.");
            
        } else {
        	
            exchange.sendResponseHeaders(200, 0);
            OutputStream output = exchange.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                output.write(buffer, 0, count);
            }
            output.flush();
            output.close();
            fs.close();
        }
    }
    
}