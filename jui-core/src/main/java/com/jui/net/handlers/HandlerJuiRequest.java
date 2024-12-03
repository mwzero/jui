package com.jui.net.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.jui.JuiApp;
import com.jui.model.JuiContent;
import com.jui.model.JuiMessage;
import com.sun.net.httpserver.HttpExchange;

import lombok.extern.java.Log;

@Log
public class HandlerJuiRequest extends HandlerBase {
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		log.fine("Handling page");
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Oppure specifica un'origine specifica
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Content-Range, Content-Disposition, Content-Description");
		
		if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        int pageStatus = 200;
        String response = null;
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
	        String requestBody = reader.lines().collect(Collectors.joining());
	            
			JuiMessage msg = JuiMessage.parseOf(requestBody);
			
			 if ("init".compareTo(msg.getAction()) == 0 ) {
				 
				 response = JuiApp.jui.render().toJsonString();
				 
			 } else if ("click".equals(msg.getAction())) {
				 JuiApp.jui.executeServerAction(msg.getId());
				 
				 if (JuiApp.jui.getJuiResponse() == null ) {
						response = JuiContent.builder("OK","OK").toJsonString();
				 } else {
					 response = JuiContent.builder("KO",JuiApp.jui.getJuiResponse()).toJsonString(); 
				 }
					
				 
			 }
			
			
			
		} catch (Exception e) {
			
			pageStatus = 500;
			log.severe("Request handling Err[%s]".formatted(e.getLocalizedMessage()));
			response = """
					{
						"error": "%s"
					}
					""".formatted(JuiApp.jui.getJuiResponse());
		}
		

        
        
        try {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(pageStatus, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes("UTF-8"));
            os.close();

        } catch (IOException e) {
        	log.severe("Something wrong sending Error.[%s]".formatted(e.getLocalizedMessage()));
        	
        }
    }

}
