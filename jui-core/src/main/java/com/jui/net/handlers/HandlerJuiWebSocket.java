package com.jui.net.handlers;

import java.io.IOException;

public class HandlerJuiWebSocket implements IHandlerWebSocket {
	
    @Override
    public String handleMessage(String message) throws IOException {
    	
    	//here arrive all ws message
        String response = message;
        return response;
        
        
    }
}

