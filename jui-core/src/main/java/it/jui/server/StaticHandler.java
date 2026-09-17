package it.jui.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

final class StaticHandler implements HttpHandler {

    private final byte[] indexHtml;

    StaticHandler() throws IOException {
        try (InputStream input = StaticHandler.class.getResourceAsStream("/static/index.html")) {
            if (input == null) throw new IOException("Missing /static/index.html");
            indexHtml = input.readAllBytes();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpSupport.methodNotAllowed(exchange, "GET");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!"/".equals(path) && !"/index.html".equals(path)) {
            HttpSupport.text(exchange, 404, "Not found");
            return;
        }

        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        HttpSupport.html(exchange, 200, indexHtml);
    }
}
