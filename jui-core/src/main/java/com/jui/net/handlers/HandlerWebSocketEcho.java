package com.jui.net.handlers;

import java.io.IOException;

public class HandlerWebSocketEcho implements HandlerWebSocket {
	
    @Override
    public String handleMessage(String message) throws IOException {
        String response = "Echo: " + message;
        return response;
    }
}

