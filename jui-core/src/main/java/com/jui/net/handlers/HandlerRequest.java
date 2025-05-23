package com.jui.net.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

public class HandlerRequest extends HandlerBase  {

    public HandlerRequest() {
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
        
        if ("POST".equals(exchange.getRequestMethod())) {
        	
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
            );
            String requestBody = reader.lines().collect(Collectors.joining());
            
            // Stampa i dati ricevuti (puoi fare ulteriori elaborazioni qui)
            System.out.println("Dati ricevuti: " + requestBody);

            // Risposta al client
            String response = requestBody;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            
        }
        else if ("GET".equals(exchange.getRequestMethod())) {
			 
	        Map<String, String> params = queryToMap(exchange);
	        
	        StringBuffer sb = new StringBuffer();
	        
	        if ( params != null ) {
		        for ( String key : params.keySet() ) {
		        	sb.append(String.format("%s=%s",  key, params.get(key)));
		        }
	        }
	        
	        try {
	            byte[] bs = sb.toString().getBytes("UTF-8");
	            exchange.sendResponseHeaders(200, bs.length);
	            OutputStream os = exchange.getResponseBody();
	            os.write(bs);
	            os.close();
	
	        } catch (IOException ex) {
	            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
	        }
        }  else {
            // Risposta per metodi diversi da POST
            exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
        }
    }
}
