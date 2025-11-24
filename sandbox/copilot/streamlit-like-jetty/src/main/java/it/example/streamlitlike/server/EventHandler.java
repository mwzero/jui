
package it.example.streamlitlike.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.example.streamlitlike.app.MyApp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class EventHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public EventHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionId = CookieUtil.ensureSessionId(exchange);
        Map<String, String> state = sessionManager.getSession(sessionId);

        Map<String, String> params = parseQuery(exchange.getRequestURI());
        String component = params.getOrDefault("component", "");
        String value = params.getOrDefault("value", "");

        StringBuilder partialHtml = new StringBuilder();

        switch (component) {
            case "productUrl" -> {
                state.put("productUrl", value);
                partialHtml.append("<p>URL aggiornato.</p>");
            }
            case "notifyEmail" -> {
                state.put("notifyEmail", value);
                partialHtml.append("<p>Notifiche email: ").append(value).append("</p>");
            }
            case "category" -> {
                state.put("category", value);
                partialHtml.append("<p>Categoria selezionata: ").append(value).append("</p>");
            }
            case "addButton" -> {
                String productUrl = state.getOrDefault("productUrl", "");
                if (productUrl.isEmpty()) {
                    partialHtml.append("<p style='color:#b00020;'>Please enter a product URL</p>");
                } else if (!MyApp.isValidUrl(productUrl)) {
                    partialHtml.append("<p style='color:#b00020;'>Please enter a valid URL</p>");
                } else {
                    partialHtml.append("<p style='color:#0b7a0b;'>Product is now being tracked!</p>");
                    if ("true".equalsIgnoreCase(state.getOrDefault("notifyEmail", "false"))) {
                        partialHtml.append("<p>(Email notifications enabled)</p>");
                    }
                    partialHtml.append("<p>Category: ").append(state.getOrDefault("category", "n/a")).append("</p>");
                    String uploaded = state.getOrDefault("productImageName", "");
                    if (!uploaded.isEmpty()) {
                        partialHtml.append("<p>Uploaded image: ").append(uploaded).append("</p>");
                    }
                }
            }
            default -> partialHtml.append("<p>Evento non gestito: ").append(component).append("</p>");
        }

        byte[] response = partialHtml.toString().getBytes();
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private Map<String, String> parseQuery(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getQuery();
        if (query != null && !query.isEmpty()) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                params.put(decode(kv[0]), kv.length > 1 ? decode(kv[1]) : "");
            }
        }
        return params;
    }

    private String decode(String s) {
        try { return java.net.URLDecoder.decode(s, "UTF-8"); }
        catch (Exception e) { return s; }
    }
}
