package com.jui.net.wss;

import java.io.IOException;

public class EchoWebSocketHandler implements WebSocketHandler {
	
    @Override
    public String handleMessage(String message) throws IOException {
        String response = "Echo: " + message;
        return response;
    }
}

