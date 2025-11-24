
package it.example.streamlitlike.server;

import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.UUID;

public class CookieUtil {
    public static String ensureSessionId(HttpExchange exchange) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        String sessionId = null;
        if (cookies != null) {
            for (String cookie : cookies) {
                for (String part : cookie.split(";")) {
                    String trimmed = part.trim();
                    if (trimmed.startsWith("SESSIONID=")) {
                        sessionId = trimmed.substring("SESSIONID=".length());
                    }
                }
            }
        }
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            exchange.getResponseHeaders().add("Set-Cookie", "SESSIONID=" + sessionId + "; Path=/; HttpOnly");
        }
        return sessionId;
    }
}
