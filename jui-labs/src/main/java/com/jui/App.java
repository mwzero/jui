package com.jui;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class App {
	
    private static List<UIComponent> components = new ArrayList<>();
    private static Map<String, Button> buttons = new ConcurrentHashMap<>();

    public static void button(String label, Runnable onClick) {
    	
        Button button = new Button(label, onClick);
        components.add(button);
        buttons.put(button.getId(), button);
    }

    public static Button getButtonById(String id) {
        return buttons.get(id);
    }

    public static void render(Session session) {
    	
        StringBuilder html = new StringBuilder();
        
        for (UIComponent component : components) {
            html.append(component.render());
        }
        // Invia l'HTML al client tramite WebSocket
        try {
            session.getAsyncRemote().sendText(html.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void start() {
    	
    	HttpServer httpserver = HttpServer.createSimpleServer("src/main/resources/html", 8080);
		httpserver.getServerConfiguration().addHttpHandler(new FileHandler(), "/js");
		try {
			
			httpserver.start();
			System.out.println("--- httpserver is running");
			
			Server server;
			server = new Server("localhost", 8025, "/ws", null, MyWebSocketEndpoint.class);
			server.start();
			System.out.println("--- websocket server is running");
			
			System.out.println("Press any key to stop the server...");
			System.in.read();
				
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
    }
    
    
}
