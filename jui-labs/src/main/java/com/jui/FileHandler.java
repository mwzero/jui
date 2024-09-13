package com.jui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import com.sun.net.httpserver.HttpExchange;

public class FileHandler extends HttpHandler {

    public void handle(HttpExchange exchange) throws IOException {
    	
        
    }

	@Override
	public void service(Request request, Response response) throws Exception {
		String endpoint = request.getRequestURI();
        
        
        InputStream is = getClass().getResourceAsStream(endpoint);
        if ( is == null ) {
        	
			return;
		} 

        response.setStatus(HttpStatus.ACCEPTED_202);
		OutputStream output = response.getOutputStream();
		final byte[] buffer = new byte[0x10000];
		int count = 0;
		while ((count = is.read(buffer)) >= 0) {
		    output.write(buffer, 0, count);
		}
		output.flush();
		output.close();
		
	}
    
}