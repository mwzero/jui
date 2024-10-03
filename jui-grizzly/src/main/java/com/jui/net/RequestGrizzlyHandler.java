package com.jui.net;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;


public class RequestGrizzlyHandler extends HttpHandler {

    public RequestGrizzlyHandler() {
	}

	@Override
	public void service(Request request, Response response) throws Exception {
		
		final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        final String date = format.format(new Date(System.currentTimeMillis()));
        response.setContentType("text/plain");
        response.setContentLength(date.length());
        response.getWriter().write(date);
		
		/*
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
        */
		
	}
}