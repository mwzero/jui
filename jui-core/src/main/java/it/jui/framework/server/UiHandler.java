package it.jui.framework.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

final class UiHandler implements HttpHandler {

    private static final int MAX_REQUEST_BYTES = 8 * 1024 * 1024;

    private final ISessionManager sessionManager;
    private final JuiProvider appProvider;
    private final Gson gson = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    UiHandler(ISessionManager sessionManager, JuiProvider appProvider) {
        this.sessionManager = sessionManager;
        this.appProvider = appProvider;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                default -> HttpSupport.methodNotAllowed(exchange, "GET, POST");
            }
        } catch (HttpSupport.BodyTooLargeException e) {
            HttpSupport.text(exchange, 413, e.getMessage());
        } catch (JsonParseException | IllegalArgumentException e) {
            HttpSupport.text(exchange, 400, "Invalid request");
        } catch (Exception e) {
            HttpSupport.text(exchange, 500, "JUI server error");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String sessionId = HttpSupport.queryParam(exchange, "sessionId");
        if (sessionId == null || sessionId.isBlank()) sessionId = UUID.randomUUID().toString();
        writeUi(exchange, render(sessionId, true));
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String sessionId = HttpSupport.queryParam(exchange, "sessionId");
        if (sessionId == null || sessionId.isBlank()) {
            HttpSupport.text(exchange, 400, "Missing sessionId");
            return;
        }

        String body = HttpSupport.readBody(exchange, MAX_REQUEST_BYTES);
        Map<String, Object> data = gson.fromJson(body, new TypeToken<Map<String, Object>>() {}.getType());
        if (data == null) throw new IllegalArgumentException("Missing JSON object");

        Object rawId = data.get("id");
        if (rawId instanceof String widgetId && !widgetId.isBlank() && data.containsKey("value")) {
            sessionManager.updateState(sessionId, widgetId, data.get("value"));
        }

        writeUi(exchange, render(sessionId, false));
    }

    private UiResponse render(String sessionId, boolean fullPage) throws IOException {
        JuiApp app = appProvider.getApp();
        UIContext ui = new UIContext(sessionId, sessionManager);

        try {
            app.run(ui);
        } catch (Exception e) {
            ui.title("Runtime Error");
            ui.error(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }

        return new UiResponse(ui.getHtml(), ui.getHtmlDependencies(), fullPage);
    }

    private void writeUi(HttpExchange exchange, UiResponse response) throws IOException {
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        HttpSupport.json(exchange, 200, gson.toJson(response));
    }

    private record UiResponse(String html, Map<String, String> htmlDependencies, boolean fullPage) {}
}
