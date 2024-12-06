package com.jui.net.handlers;

import java.io.IOException;
import java.io.OutputStream;

public interface IHandlerWebSocket {
	
    String handleMessage(String message) throws IOException;
}
