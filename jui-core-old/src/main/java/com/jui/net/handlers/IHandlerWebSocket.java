package com.jui.net.handlers;

import java.io.IOException;

public interface IHandlerWebSocket {
	
    String handleMessage(String message) throws IOException;
}
