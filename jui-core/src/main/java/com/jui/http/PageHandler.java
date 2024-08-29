package com.jui.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.jui.JuiPage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PageHandler extends BaseHandler implements HttpHandler {
	
	JuiPage page;
	
	public PageHandler(JuiPage page) {
		
		log.debug("Initializing PageHandler");
		this.page = page;
	}
	
	public void handle(HttpExchange exchange) throws IOException {

		log.debug("Handling page");
		
        int pageStatus = 200;
        String response = null;
		try {
			
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("context", page.getContext().getContext());
			variables.put("elementMapping", page.getContext().elementMapping());
			variables.put("elementPostData", page.getContext().elementPostData);
			variables.put("queryParams", this.queryToMap(exchange));
			
			response = page.getEngine().renderTemplate(page.getTemplate() , variables);
			
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