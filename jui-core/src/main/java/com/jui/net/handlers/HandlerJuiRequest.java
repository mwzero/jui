package com.jui.net.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.jui.JuiApp;
import com.jui.JuiHtmlRenderer;
import com.jui.JuiListener;
import com.jui.html.WebElement;
import com.jui.model.JuiContent;
import com.jui.model.JuiMessage;
import com.sun.net.httpserver.HttpExchange;

import lombok.extern.java.Log;

@Log
public class HandlerJuiRequest extends HandlerBase {
	
	JuiHtmlRenderer renderer = new JuiHtmlRenderer();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		log.fine("Handling page");
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); 
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Content-Range, Content-Disposition, Content-Description");
		
		if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        
        int pageStatus = 200;
        String response = null;
		try {
			
			log.fine("Retrieving payload from POST request");
			BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
	        String requestBody = reader.lines().collect(Collectors.joining());
			JuiMessage msg = JuiMessage.parseOf(requestBody);
			log.fine("payload[%s]".formatted(requestBody));
			
			 if ("init".compareTo(msg.getAction()) == 0 ) {
				 
				log.fine("Start to render JUI Page");
				response = renderer.render().toJsonString();
				 
			 } else if ("update".compareTo(msg.getAction()) == 0 ) {
				 
				log.fine("Update");
				
				response = JuiContent.builder().main(renderer.renderWebContainer(msg.getId())).build().toJsonString();
				
				 
			 } else {

				 //TODO: why we are still using Jui Respone attribute?
				 JuiListener.listener.executeServerAction(msg.getId(), msg.getAction(), msg.getPayload());
				 response = JuiContent.builder()
						 .main("OK")
						 .sidebar("OK")
						 .build()
						 .toJsonString();
				 /*
				 if (JuiApp.jui.juiResponse() == null ) {
					response = JuiContent.builder("OK","OK").toJsonString();
				 } else {
					response = JuiContent.builder("KO",JuiApp.jui.juiResponse()).toJsonString(); 
				 }
				 */
					
				 
			 }
			
			
			
		} catch (Throwable e) {
			
			pageStatus = 500;
			log.severe("Request handling Err[%s]".formatted(e.getLocalizedMessage()));
			response = """
					{
						"error": "%s"
					}
					""".formatted(e.getLocalizedMessage());
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
