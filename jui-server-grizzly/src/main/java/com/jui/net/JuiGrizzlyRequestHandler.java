package com.jui.net;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import com.jui.JuiApp;
import com.jui.model.JuiMessage;


public class JuiGrizzlyRequestHandler extends HttpHandler {

    public JuiGrizzlyRequestHandler() {
    	;
	}

	@Override
	public void service(Request request, Response response) throws Exception {
		
        if ( Method.GET == request.getMethod() ) {
        	
        	response.setContentType("text/html");
        	response.getWriter().write("OK");
        	
        	
        } else if ( Method.POST == request.getMethod() ) {
        	
        	InputStream inputStream = request.getInputStream();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            JuiMessage msg = JuiMessage.parseOf(requestBody);
        	
            if ("click".compareTo(msg.getAction()) == 0 ) {
            	
            	response.setContentType("text/html");
            	JuiApp.jui.executeServerAction(msg.getId());
            	
            } else if ("init".equals(msg.getAction())) {
            	
            	response.setContentType("text/html");
            	response.getWriter().write(JuiApp.jui.render().toJsonString());
                
            }
            
        }
	}
}