package com.jui;

import java.io.IOException;

import com.google.gson.Gson;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@ServerEndpoint("/jui")
@Slf4j
public class WebSocketEndpoint {
	
    @OnOpen
    public void onOpen(Session session) {
        log.info("WebSocket connection ready[{}]",session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
    	
    	Gson gson = new Gson();
    	JuiMessagge msg = gson.fromJson(message, JuiMessagge.class);
    	
        if ("click".equals(msg.action)) {
        	JuiApp.jui.executeServerAction(msg.id);
        	
        } else if ("init".equals(msg.action)) {
        	
        	session.getAsyncRemote().sendText(JuiApp.jui.render());
            
        }
    }

    @OnClose
    public void onClose(Session session) {
    	log.info("WebSocket connection closed[{}]",session.getId());
    }
    
    private class JuiMessagge {
        String action;
        String id;
    }
}

