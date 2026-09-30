package it.jui.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import it.jui.server.HttpSupport;
import it.jui.server.ISessionManager;
import it.jui.server.SessionCookies;
import it.jui.server.SessionState;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;

public final class GoogleOAuthSupport {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).followRedirects(HttpClient.Redirect.NEVER).build();

    private GoogleOAuthSupport() {}

    public static void install(HttpServer server, ISessionManager sessions, GoogleOAuthConfig config) {
        install(server, sessions, config, new SessionCookies());
    }

    public static void install(HttpServer server, ISessionManager sessions, GoogleOAuthConfig config,
                               SessionCookies cookies) {
        install(server, sessions, config, cookies, GoogleOAuthSupport::authenticate, Clock.systemUTC());
    }

    static void install(HttpServer server, ISessionManager sessions, GoogleOAuthConfig config,
                        SessionCookies cookies, GoogleOAuthClient client, Clock clock) {
        cookies.secure("https".equalsIgnoreCase(URI.create(config.callbackUrl()).getScheme()));
        server.createContext("/auth/google/login", exchange -> {
            if (!validRequest(exchange, "/auth/google/login")) return;
            SessionState session = sessions.findSession(cookies.read(exchange)).orElse(null);
            if (session == null) {
                HttpSupport.text(exchange, 401, "Open the application before signing in");
                return;
            }
            synchronized (session) {
                if (sessions.findSession(session.id()).isEmpty()) {
                    HttpSupport.text(exchange, 401, "Session expired");
                    return;
                }
                String state = session.beginOAuth(clock.instant());
                String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                        + "?client_id=" + enc(config.clientId())
                        + "&redirect_uri=" + enc(config.callbackUrl())
                        + "&response_type=code&scope=" + enc("openid profile email")
                        + "&state=" + enc(state);
                HttpSupport.redirect(exchange, authUrl);
            }
        });

        server.createContext(config.callbackPath(), exchange -> {
            if (!validRequest(exchange, config.callbackPath())) return;
            SessionState session = sessions.findSession(cookies.read(exchange)).orElse(null);
            String state = HttpSupport.queryParam(exchange, "state");
            String code = HttpSupport.queryParam(exchange, "code");
            String error = HttpSupport.queryParam(exchange, "error");
            if (state == null || (code == null && error == null)) {
                HttpSupport.text(exchange, 400, "Missing OAuth callback parameters");
                return;
            }
            if (session == null) {
                HttpSupport.text(exchange, 403, "Invalid OAuth session");
                return;
            }
            long generation;
            synchronized (session) {
                if (sessions.findSession(session.id()).isEmpty() || !session.consumeOAuth(state, clock.instant())) {
                    HttpSupport.text(exchange, 403, "Invalid or expired OAuth state");
                    return;
                }
                generation = session.oauthGeneration();
                if (error != null) {
                    session.notice("Google sign-in was cancelled or denied.");
                    HttpSupport.redirect(exchange, "/");
                    return;
                }
            }
            try {
                if (code.isBlank()) throw new IllegalArgumentException("Missing code");
                // Network calls stay outside the session lock. Revalidate before changing identity.
                AuthUser user = client.authenticate(code, config);
                if (user == null || user.id() == null || user.id().isBlank())
                    throw new IllegalArgumentException("Missing Google subject");
                synchronized (session) {
                    if (sessions.findSession(session.id()).isEmpty() || session.oauthGeneration() != generation) {
                        HttpSupport.text(exchange, 403, "OAuth session is no longer current");
                        return;
                    }
                    SessionState authenticated = sessions.rotateSession(session, user);
                    cookies.write(exchange, authenticated);
                    HttpSupport.redirect(exchange, "/");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                signInFailed(exchange, session);
            } catch (IOException | RuntimeException e) {
                signInFailed(exchange, session);
            }
        });
    }

    private static void signInFailed(HttpExchange exchange, SessionState session) throws IOException {
        session.notice("Google sign-in could not be completed. Please try again.");
        HttpSupport.redirect(exchange, "/");
    }

    private static boolean validRequest(HttpExchange exchange, String path) throws IOException {
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("Referrer-Policy", "no-referrer");
        if (!path.equals(exchange.getRequestURI().getPath())) {
            HttpSupport.text(exchange, 404, "Not found");
            return false;
        }
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpSupport.methodNotAllowed(exchange, "GET");
            return false;
        }
        return true;
    }

    private static AuthUser authenticate(String code, GoogleOAuthConfig config) throws IOException, InterruptedException {
        String body = "code=" + enc(code) + "&client_id=" + enc(config.clientId())
                + "&client_secret=" + enc(config.clientSecret()) + "&redirect_uri=" + enc(config.callbackUrl())
                + "&grant_type=authorization_code";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .timeout(Duration.ofSeconds(15)).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        JsonObject token = readJson(HTTP.send(request, HttpResponse.BodyHandlers.ofString()));
        String accessToken = string(token, "access_token");
        if (accessToken == null || accessToken.isBlank()) throw new IOException("Google token missing");
        HttpRequest userRequest = HttpRequest.newBuilder(URI.create("https://openidconnect.googleapis.com/v1/userinfo"))
                .timeout(Duration.ofSeconds(15)).header("Authorization", "Bearer " + accessToken).GET().build();
        JsonObject user = readJson(HTTP.send(userRequest, HttpResponse.BodyHandlers.ofString()));
        return new AuthUser(string(user, "sub"), string(user, "email"), string(user, "name"), string(user, "picture"));
    }

    private static JsonObject readJson(HttpResponse<String> response) throws IOException {
        if (response.statusCode() / 100 != 2) throw new IOException("Google service unavailable");
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static String string(JsonObject json, String key) {
        return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString() : null;
    }

    private static String enc(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
