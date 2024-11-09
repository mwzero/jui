package com.jui.net.handlers;

import java.io.IOException;

public class HandlerJuiWebSocket implements HandlerWebSocket {
	
    @Override
    public String handleMessage(String message) throws IOException {
        String response = "Jui: " + message;
        return response;
    }
}

