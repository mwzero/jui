package com.jui.net.wss;

import java.io.IOException;
import java.io.OutputStream;

public interface WebSocketHandler {
	
    String handleMessage(String message) throws IOException;
}
