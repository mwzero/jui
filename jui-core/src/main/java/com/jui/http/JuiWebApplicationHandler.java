package com.jui.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jui.JuiWebApplication;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JuiWebApplicationHandler extends BaseHandler implements HttpHandler {
	
	JuiWebApplication application;
	
	public JuiWebApplicationHandler(JuiWebApplication application) {
		
		log.debug("Initializing PageHandler");
		this.application = application;
	}
	
	public void handle(HttpExchange exchange) throws IOException {

		log.debug("Handling page");
		
        int pageStatus = 200;
        String response = null;
		try {
			
			Map<String, Object> variables = new HashMap<String, Object>();
			
			variables.put("main_contexts", application.getMain());
			variables.put("sidebar_context", application.getSidebar().getContext().getLinkedMapContext());
			variables.put("header_context", application.getHeader().getContext().getLinkedMapContext());
			
			variables.put("elementMapping", application.getMain().get(1).getContext().elementMapping());
			variables.put("elementPostData", application.getMain().get(1).getContext().elementPostData);
			variables.put("queryParams", this.queryToMap(exchange));
			
			response = application.getEngine().renderTemplate(application.getTemplate() , variables);
			
		} catch (Exception e) {
			
			pageStatus = 500;
			log.error("Request handling Err[{}]", e.getLocalizedMessage());
			response = getErrorPage(e);
		}

        
        
        try {
            byte[] bs = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(pageStatus, bs.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bs);
            os.close();

        } catch (IOException e) {
        	log.error("Something wrong sending Error",  e.getLocalizedMessage());
        	
        }
    }

}