package com.jui;

import java.io.IOException;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
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
    	
    	JuiMessage msg = JuiMessage.parseOf(message);
    	
        if ("click".compareTo(msg.action) == 0 ) {
        	
        	JuiApp.jui.executeServerAction(msg.id);
        	
        } else if ("init".equals(msg.action)) {
        	
        	session.getAsyncRemote().sendText(JuiApp.jui.render());
            
        }
    }

    @OnClose
    public void onClose(Session session) {
    	log.info("WebSocket connection closed[{}]",session.getId());
    }
}

