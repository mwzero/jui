
package it.example.streamlitlike.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebSocket
public class ProductSocket {
    private final Map<String, Map<String, String>> sessions;
    private Session session;
    private String sessionId;

    public ProductSocket(Map<String, Map<String, String>> sessions) {
        this.sessions = sessions;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        this.session = session;
        this.sessionId = UUID.randomUUID().toString();
        sessions.putIfAbsent(sessionId, new java.util.HashMap<>());
        session.getRemote().sendString("{\"type\":\"session\",\"id\":\"" + sessionId + "\"}");
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        // protocollo minimale: JSON {"type":"event","component":"productUrl","value":"...","sessionId":"..."}
        try {
            Map<String,String> evt = parseJson(message);
            if (!"event".equals(evt.get("type"))) return;

            String sid = evt.get("sessionId");
            Map<String,String> state = sessions.computeIfAbsent(sid, k -> new java.util.HashMap<>());
            String component = evt.get("component");
            String value = evt.getOrDefault("value", "");

            String reply;
            switch (component) {
                case "productUrl" -> { state.put("productUrl", value); reply = "<p>URL aggiornato</p>"; }
                case "notifyEmail" -> { state.put("notifyEmail", value); reply = "<p>Notify: " + value + "</p>"; }
                case "category" -> { state.put("category", value); reply = "<p>Category: " + value + "</p>"; }
                case "addButton" -> {
                    String url = state.getOrDefault("productUrl", "");
                    if (url.isEmpty()) reply = "<p style='color:#b00020;'>Please enter a product URL</p>";
                    else if (!isValidUrl(url)) reply = "<p style='color:#b00020;'>Please enter a valid URL</p>";
                    else {
                        reply = "<p style='color:#0b7a0b;'>Product is now being tracked!</p>";
                    }
                }
                default -> reply = "<p>Evento non gestito: " + component + "</p>";
            }
            session.getRemote().sendString(reply);
        } catch (Exception e) {
            try { session.getRemote().sendString("<p style='color:#b00020;'>Errore</p>"); } catch (IOException ignored) {}
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        // cleanup se necessario
    }

    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    // parser JSON super minimale per demo (usa una lib in produzione)
    private Map<String,String> parseJson(String s) {
        Map<String,String> m = new java.util.HashMap<>();
        s = s.trim().replaceAll("[{}\"]","");
        for (String pair : s.split(",")) {
            String[] kv = pair.split(":",2);
            if (kv.length==2) m.put(kv[0].trim(), kv[1].trim());
        }
        return m;
    }
}
