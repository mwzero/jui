package com.jui.net;

import java.io.IOException;

import com.jui.JuiApp;
import com.jui.model.JuiMessage;

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
    	
    	JuiMessage msg = JuiMessage.parseOf(message);
    	
        if ("click".compareTo(msg.getAction()) == 0 ) {
        	
        	try {
				JuiApp.jui.executeServerAction(msg.getId(), null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        } else if ("init".equals(msg.getAction())) {
        	
        	session.getAsyncRemote().sendText(JuiApp.jui.render().toJsonString());
            
        }
    }

    @OnClose
    public void onClose(Session session) {
    	log.info("WebSocket connection closed[{}]",session.getId());
    }
}

