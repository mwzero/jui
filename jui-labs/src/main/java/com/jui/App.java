package com.jui;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.websocket.Session;

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
}
