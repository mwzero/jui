package com.jui.playground.config;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyWebSocketHandler extends TextWebSocketHandler {
    
    // Mantieni una lista delle sessioni WebSocket connesse
    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Aggiungi la sessione quando si connette
        sessions.add(session);
        System.out.println("Connessione stabilita: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Gestisci il messaggio ricevuto dal client
        System.out.println("Messaggio ricevuto: " + message.getPayload());

        // Invia una risposta al client
        session.sendMessage(new TextMessage("Ricevuto: " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // Rimuovi la sessione quando si chiude la connessione
        sessions.remove(session);
        System.out.println("Connessione chiusa: " + session.getId());
    }

    // Metodo per inviare messaggi a tutte le sessioni connesse
    public void broadcastMessage(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
