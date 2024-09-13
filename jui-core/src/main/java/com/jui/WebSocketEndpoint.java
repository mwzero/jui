package com.jui;

import java.io.IOException;

import com.google.gson.Gson;
import com.jui.html.WebComponent;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class WebSocketEndpoint {
	
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connessione WebSocket aperta: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
    	
    	Gson gson = new Gson();
    	Messaggio msg = gson.fromJson(message, Messaggio.class);
    	
        if ("click".equals(msg.action)) {
        	
        	WebComponent component = JuiApp.getInstance().getWebComponentById(msg.id);
            if (component != null) {
            	component.executeServerAction();
            }
            
        } else if ("init".equals(msg.action)) {
            // Invia il rendering iniziale al client
        	JuiApp.getInstance().render(session);
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connessione WebSocket chiusa: " + session.getId());
    }
    
    private class Messaggio {
        String action;
        String id;
    }
}

