package com.jui.ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/ws")
public class WebSocketEndpoint {
	
    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("Connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("Message received: " + message);
        session.getBasicRemote().sendText("Echo: " + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("Disconnected: " + session.getId());
    }
}
