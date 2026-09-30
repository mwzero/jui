package it.jui.server;

import com.sun.net.httpserver.HttpExchange;

/** Configured by the server, never by forwarded request headers. */
public final class SessionCookies {
    public static final String NAME = "JUI_SESSION";
    private boolean secure;

    public void secure(boolean secure) { this.secure = secure; }

    public String read(HttpExchange exchange) {
        String found = null;
        for (String header : exchange.getRequestHeaders().getOrDefault("Cookie", java.util.List.of())) {
            for (String entry : header.split(";")) {
                String[] parts = entry.trim().split("=", 2);
                if (parts.length == 2 && NAME.equals(parts[0])) {
                    if (found != null || !parts[1].matches("[A-Za-z0-9_-]{43}")) return null;
                    found = parts[1];
                }
            }
        }
        return found;
    }

    public void write(HttpExchange exchange, SessionState session) {
        exchange.getResponseHeaders().add("Set-Cookie", NAME + "=" + session.id()
                + "; Path=/; HttpOnly; SameSite=Lax" + (secure ? "; Secure" : ""));
    }
}
