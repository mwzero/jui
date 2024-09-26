package com.jui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;


public class JuiRequestHandler extends HttpHandler {

    public JuiRequestHandler() {
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
        	
            if ("click".compareTo(msg.action) == 0 ) {
            	
            	response.setContentType("text/html");
            	JuiApp.jui.executeServerAction(msg.id);
            	
            } else if ("init".equals(msg.action)) {
            	
            	response.setContentType("text/html");
            	response.getWriter().write(JuiApp.jui.render());
                
            }
            
        }
	}
}