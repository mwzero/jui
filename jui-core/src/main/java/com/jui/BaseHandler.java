package com.jui;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.jui.utils.Utils;
import com.sun.net.httpserver.HttpExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseHandler {
	
	
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
        log.debug("Managing endpoint[{}] parameters[{}]", endpoint, queryParameters);
        
        return queryToMap(queryParameters);
	}
	
	protected Map<String, String> queryToMap(String query) {
        
		if(query == null) {
			log.warn("Query params is null");
            return null;
        }
        
		return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(
                    entry -> entry[0], 
                    entry -> entry.length > 1 ? entry[1] : ""
                ));
		
    }
	
	
	protected String getErrorPage(Exception e) {
		
		log.error("Request handling Err[{}]", e.getLocalizedMessage());
		return """
					<html>
						<body>
							<p>Something wrong:[%s]</p>
						</body>
					</html>
				""".formatted(Utils.getStackTraceAsString(e));
		
	}
	
	protected URL getFileFromClassLoader (String fileName) throws IOException {
    	
		return BaseHandler.class.getResource(fileName);
		
	}

}
