package it.jui.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** Small helpers around the JDK HttpServer API. */
public final class HttpSupport {

    private HttpSupport() {}

    public static String queryParam(HttpExchange exchange, String name) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.isBlank()) return null;

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            if (name.equals(key)) {
                return parts.length == 2 ? decode(parts[1]) : "";
            }
        }
        return null;
    }

    public static String readBody(HttpExchange exchange, int maxBytes) throws IOException {
        long declared = exchange.getRequestHeaders().getFirst("Content-Length") == null
                ? -1
                : parseContentLength(exchange.getRequestHeaders().getFirst("Content-Length"));
        if (declared > maxBytes) throw new BodyTooLargeException();

        byte[] bytes = exchange.getRequestBody().readNBytes(maxBytes + 1);
        if (bytes.length > maxBytes) throw new BodyTooLargeException();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void json(HttpExchange exchange, int status, String json) throws IOException {
        send(exchange, status, "application/json; charset=utf-8", json.getBytes(StandardCharsets.UTF_8));
    }

    public static void html(HttpExchange exchange, int status, byte[] html) throws IOException {
        send(exchange, status, "text/html; charset=utf-8", html);
    }

    public static void text(HttpExchange exchange, int status, String text) throws IOException {
        send(exchange, status, "text/plain; charset=utf-8", text.getBytes(StandardCharsets.UTF_8));
    }

    public static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    public static void methodNotAllowed(HttpExchange exchange, String allow) throws IOException {
        exchange.getResponseHeaders().set("Allow", allow);
        text(exchange, 405, "Method not allowed");
    }

    private static void send(HttpExchange exchange, int status, String contentType, byte[] body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(status, body.length);
        try (var out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static long parseContentLength(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static final class BodyTooLargeException extends IOException {
        public BodyTooLargeException() {
            super("Request body is too large");
        }
    }
}
