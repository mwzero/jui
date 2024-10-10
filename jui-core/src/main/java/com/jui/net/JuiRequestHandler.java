package com.jui.net;

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
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JuiRequestHandler extends BaseHandler implements HttpHandler {
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		log.debug("Handling page");
		
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
			
			/*
			Map<String, Object> variables = new HashMap<String, Object>();
			
			variables.put("main_context", application.getMain().getContext().getLinkedMapContext());
			variables.put("sidebar_context", application.getSidebar().getContext().getLinkedMapContext());
			variables.put("header_context", application.getHeader().getContext().getLinkedMapContext());
			
			variables.put("elementMapping", application.getContext().elementMapping());
			variables.put("elementPostData", application.getContext().elementPostData);
			variables.put("queryParams", this.queryToMap(exchange));
			*/
			
		} catch (Exception e) {
			
			pageStatus = 500;
			log.error("Request handling Err[{}]", e.getLocalizedMessage());
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
        	log.error("Something wrong sending Error",  e.getLocalizedMessage());
        	
        }
    }

}
