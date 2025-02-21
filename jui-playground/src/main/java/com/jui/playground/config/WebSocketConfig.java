package com.jui.playground.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer, com.jui.toolkits.OutputListener {

    private final MyWebSocketHandler myWebSocketHandler = new MyWebSocketHandler();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler, "/ws").setAllowedOrigins("*");
    }

    public MyWebSocketHandler getWebSocketHandler() {
        return myWebSocketHandler;
    }

	@Override
	public void println(String line) throws IOException {
		
		myWebSocketHandler.broadcastMessage(line);
		
	}
}




