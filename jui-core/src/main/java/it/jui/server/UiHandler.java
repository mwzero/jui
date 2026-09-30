package it.jui.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.jui.app.JuiProvider;
import it.jui.UIContext;
import it.jui.input.WidgetSpec;

import java.io.IOException;
import java.util.Map;
import java.util.Locale;

final class UiHandler implements HttpHandler {
    private static final int MAX_REQUEST_BYTES = 8 * 1024 * 1024;
    private final ISessionManager sessions;
    private final JuiProvider appProvider;
    private final SessionCookies cookies;
    private final Gson gson = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();

    UiHandler(ISessionManager sessions, JuiProvider appProvider, SessionCookies cookies) {
        this.sessions = sessions;
        this.appProvider = appProvider;
        this.cookies = cookies;
    }

    @Override public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        try {
            if (!"/ui".equals(exchange.getRequestURI().getPath())) {
                HttpSupport.text(exchange, 404, "Not found");
                return;
            }
            switch (exchange.getRequestMethod().toUpperCase(Locale.ROOT)) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                default -> HttpSupport.methodNotAllowed(exchange, "GET, POST");
            }
        } catch (HttpSupport.BodyTooLargeException e) {
            HttpSupport.text(exchange, 413, e.getMessage());
        } catch (JsonParseException | IllegalArgumentException | ArithmeticException e) {
            HttpSupport.text(exchange, 400, "Invalid request");
        } catch (Exception e) {
            HttpSupport.text(exchange, 500, "JUI server error");
        }
    }

    private String viewId(HttpExchange exchange) {
        String viewId = HttpSupport.queryParam(exchange, "viewId");
        if (HttpSupport.queryParam(exchange, "sessionId") != null
                || viewId == null || !viewId.matches("[A-Za-z0-9_-]{1,128}"))
            throw new IllegalArgumentException("Invalid view");
        return viewId;
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String viewId = viewId(exchange);
        SessionState session = sessions.findSession(cookies.read(exchange)).orElseGet(() -> {
            SessionState created = sessions.createSession();
            cookies.write(exchange, created);
            return created;
        });
        synchronized (session) {
            if (!current(exchange, session)) return;
            writeUi(exchange, render(session, viewId, true, null));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String viewId = viewId(exchange);
        SessionState session = sessions.findSession(cookies.read(exchange)).orElse(null);
        if (session == null) {
            HttpSupport.text(exchange, 401, "Session missing or expired");
            return;
        }
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !"application/json".equalsIgnoreCase(contentType.split(";", 2)[0].trim()))
            throw new IllegalArgumentException("JSON required");
        String body = HttpSupport.readBody(exchange, MAX_REQUEST_BYTES);
        Map<String, Object> data = gson.fromJson(body, new TypeToken<Map<String, Object>>() {}.getType());
        if (data == null || !(data.get("id") instanceof String id) || id.isBlank()
                || !data.containsKey("value") || !(data.get("revision") instanceof Number revision))
            throw new IllegalArgumentException("Invalid update");
        long suppliedRevision = new java.math.BigDecimal(revision.toString()).longValueExact();
        synchronized (session) {
            if (!current(exchange, session)) return;
            if (!session.acceptsCsrf(exchange.getRequestHeaders().getFirst("X-JUI-CSRF"))) {
                HttpSupport.text(exchange, 403, "Invalid CSRF token");
                return;
            }
            ViewState view = session.findView(viewId).orElse(null);
            if (view == null || view.revision() != suppliedRevision) {
                HttpSupport.text(exchange, 409, "View is out of date");
                return;
            }
            WidgetSpec spec = view.widgets().get(id);
            if (spec == null) {
                HttpSupport.text(exchange, 403, "Widget is not available");
                return;
            }
            Object value = spec.validate(data.get("value"));
            if (spec.kind() == WidgetSpec.Kind.LOGOUT) {
                SessionState anonymous = sessions.rotateSession(session, null);
                cookies.write(exchange, anonymous);
                synchronized (anonymous) {
                    writeUi(exchange, render(anonymous, viewId, false, id));
                }
                return;
            }
            view.widgetValues().put(id, value);
            try {
                writeUi(exchange, render(session, viewId, false, null));
            } finally {
                if (spec.kind() == WidgetSpec.Kind.ACTION) view.widgetValues().remove(id);
            }
        }
    }

    private boolean current(HttpExchange exchange, SessionState session) throws IOException {
        if (sessions.findSession(session.id()).isPresent()) return true;
        HttpSupport.text(exchange, 401, "Session missing or expired");
        return false;
    }

    private UiResponse render(SessionState session, String viewId, boolean fullPage, String logoutId) {
        UIContext ui = new UIContext(session, viewId, logoutId);
        boolean successful = false;
        try {
            String notice = session.consumeNotice();
            if (notice != null) ui.info(notice);
            appProvider.getApp().run(ui);
            successful = true;
        } catch (Exception e) {
            // Discard partial markup and its input allowlist on any application failure.
            ui = new UIContext(session, viewId);
            ui.title("Runtime Error");
            ui.error("The application could not render this page.");
        } finally {
            ui.completeRender(successful);
        }
        return new UiResponse(ui.getHtml(), ui.getHtmlDependencies(), fullPage,
                session.csrfToken(), session.view(viewId).revision());
    }

    private void writeUi(HttpExchange exchange, UiResponse response) throws IOException {
        HttpSupport.json(exchange, 200, gson.toJson(response));
    }

    private record UiResponse(String html, Map<String, String> htmlDependencies, boolean fullPage,
                              String csrfToken, long revision) {}
}
