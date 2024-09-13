package com.jui;

import java.io.IOException;

import com.google.gson.Gson;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class MyWebSocketEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connessione WebSocket aperta: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
    	
    	Gson gson = new Gson();
    	Messaggio msg = gson.fromJson(message, Messaggio.class);
        if ("click".equals(msg.action)) {
            Button button = App.getButtonById(msg.id);
            if (button != null) {
                button.click();
            }
        } else if ("init".equals(msg.action)) {
            // Invia il rendering iniziale al client
            App.render(session);
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Connessione WebSocket chiusa: " + session.getId());
    }
    
 // Classe helper per deserializzare il messaggio JSON
    private class Messaggio {
        String action;
        String id;
    }
}

