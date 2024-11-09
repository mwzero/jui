package com.jui.net.handlers;

import java.io.IOException;
import java.io.OutputStream;

public interface HandlerWebSocket {
	
    String handleMessage(String message) throws IOException;
}
