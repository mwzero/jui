
package it.example.streamlitlike.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.example.streamlitlike.app.MyApp;
import it.example.streamlitlike.ui.UIContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MainPageHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public MainPageHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionId = CookieUtil.ensureSessionId(exchange);
        Map<String, String> state = sessionManager.getSession(sessionId);

        Map<String, String> params = parseQuery(exchange.getRequestURI());
        if (!params.isEmpty()) {
            sessionManager.updateSession(sessionId, params);
        }

        UIContext ctx = new UIContext(state, params);
        new MyApp().render(ctx);

        String html = """
            <html>
            <head>
              <meta charset="utf-8"/>
              <style>
                body { font-family: sans-serif; }
              </style>
              <script>
                function sendEvent(componentId) {
                  const el = document.getElementById(componentId);
                  const value = el ? el.value : '';
                  fetch('/event?component=' + encodeURIComponent(componentId) + '&value=' + encodeURIComponent(value))
                    .then(r => r.text())
                    .then(html => { document.getElementById('messages').innerHTML = html; });
                }
                function sendEventCheckbox(componentId) {
                  const el = document.getElementById(componentId);
                  const value = el && el.checked ? 'true' : 'false';
                  fetch('/event?component=' + encodeURIComponent(componentId) + '&value=' + encodeURIComponent(value))
                    .then(r => r.text())
                    .then(html => { document.getElementById('messages').innerHTML = html; });
                }
                function sendFile(componentId) {
                  const el = document.getElementById(componentId);
                  if (!el || el.files.length === 0) { alert('Select a file first'); return; }
                  const file = el.files[0];
                  const formData = new FormData();
                  formData.append('component', componentId);
                  formData.append('file', file, file.name);
                  fetch('/upload', { method: 'POST', body: formData })
                    .then(r => r.text())
                    .then(html => { document.getElementById('messages').innerHTML = html; });
                }
                function addProduct() {
                  fetch('/event?component=addButton')
                    .then(r => r.text())
                    .then(html => { document.getElementById('messages').innerHTML = html; });
                }
              </script>
            </head>
            <body>
              <form onsubmit="return false;">
                %s
                <button type="button" onclick="addProduct()">Add Product</button><br>
                <hr>
                <div id="messages"></div>
              </form>
            </body>
            </html>
            """.formatted(ctx.render());

        byte[] response = html.getBytes();
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
