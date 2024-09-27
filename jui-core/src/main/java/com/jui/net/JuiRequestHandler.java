package com.jui.net;

import java.io.IOException;
import java.io.OutputStream;

import com.jui.JuiApp;
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
			
			response = JuiApp.jui.render();
			
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
