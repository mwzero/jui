package com.jui.net.http;

import java.io.IOException;

import com.jui.net.wss.WebSocketHandler;

public class JuiWebSocketHandler implements WebSocketHandler {
	
    @Override
    public String handleMessage(String message) throws IOException {
        String response = "Jui: " + message;
        return response;
    }
}

