package it.jui.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import it.jui.JuiApp;
import it.jui.app.JuiProvider;
import java.net.*;
import java.net.http.*;
import java.time.Clock;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/** Exercises actual JDK HTTP exchanges on an ephemeral loopback port. */
public final class HttpTestRuntime implements AutoCloseable {
    public final InMemorySessionManager sessions;
    public final SessionCookies cookies = new SessionCookies();
    public final HttpServer server;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private final Gson gson = new Gson();
    public final String origin;

    public HttpTestRuntime(JuiApp app) throws Exception { this(app, Clock.systemUTC()); }
    public HttpTestRuntime(JuiApp app, Clock clock) throws Exception {
        sessions = new InMemorySessionManager(clock);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.setExecutor(executor);
        origin = "http://" + (server.getAddress().getAddress() instanceof Inet6Address ? "[::1]" : "127.0.0.1")
                + ":" + server.getAddress().getPort();
        server.createContext("/ui", new UiHandler(sessions, new JuiProvider(app), cookies));
        server.createContext("/", new StaticHandler());
    }
    public void start() { server.start(); }
    public Browser browser() { return new Browser(new CookieJar(), "tab-a"); }
    public static String id(String key) { return "widget-" + Integer.toString(key.hashCode() & 0x7fffffff, 36); }
    @Override public void close() {
        server.stop(0);
        executor.shutdownNow();
        client.close();
        sessions.close();
    }
    private static class CookieJar { String cookie; }

    public final class Browser {
        private final CookieJar jar;
        public final String viewId;
        public String csrf;
        public long revision;
        public JsonObject payload;
        Browser(CookieJar jar, String viewId) { this.jar = jar; this.viewId = viewId; }
        public Browser tab(String viewId) { return new Browser(jar, viewId); }
        public Browser snapshot() {
            CookieJar other = new CookieJar();
            other.cookie = jar.cookie;
            Browser browser = new Browser(other, viewId);
            browser.csrf = csrf;
            browser.revision = revision;
            return browser;
        }
        public String cookie() { return jar.cookie; }
        public SessionState session() { return sessions.findSession(jar.cookie.split("=", 2)[1]).orElseThrow(); }
        public String html() { return payload.get("html").getAsString(); }
        public HttpResponse<String> getUi() throws Exception { return get("/ui?viewId=" + viewId); }
        public HttpResponse<String> get(String path) throws Exception {
            return send(HttpRequest.newBuilder(URI.create(origin + path)).GET());
        }
        public HttpResponse<String> update(String id, Object value) throws Exception {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("id", id);
            body.put("value", value);
            body.put("revision", revision);
            return postRaw(gson.toJson(body), csrf, "application/json");
        }
        public HttpResponse<String> postRaw(String body, String token, String contentType) throws Exception {
            var request = HttpRequest.newBuilder(URI.create(origin + "/ui?viewId=" + viewId))
                    .header("Content-Type", contentType).POST(HttpRequest.BodyPublishers.ofString(body));
            if (token != null) request.header("X-JUI-CSRF", token);
            return send(request);
        }
        private HttpResponse<String> send(HttpRequest.Builder builder) throws Exception {
            builder.timeout(Duration.ofSeconds(5));
            if (jar.cookie != null) builder.header("Cookie", jar.cookie);
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            response.headers().firstValue("Set-Cookie").ifPresent(value -> jar.cookie = value.split(";", 2)[0]);
            if (response.statusCode() == 200 && response.headers().firstValue("Content-Type").orElse("").contains("application/json")) {
                payload = JsonParser.parseString(response.body()).getAsJsonObject();
                csrf = payload.get("csrfToken").getAsString();
                revision = payload.get("revision").getAsLong();
            }
            return response;
        }
    }
}
