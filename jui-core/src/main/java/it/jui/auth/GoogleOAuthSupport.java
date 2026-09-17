package it.jui.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import it.jui.server.HttpSupport;
import it.jui.server.ISessionManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

public final class GoogleOAuthSupport {

    private static final long STATE_MAX_AGE_SECONDS = 600;
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private GoogleOAuthSupport() {}

    public static void install(HttpServer server, ISessionManager sessions, GoogleOAuthConfig config) {
        server.createContext("/auth/google/login", exchange -> {
            if (!exactPath(exchange, "/auth/google/login")) return;
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpSupport.methodNotAllowed(exchange, "GET");
                return;
            }

            String sessionId = HttpSupport.queryParam(exchange, "sessionId");
            if (sessionId == null || sessionId.isBlank()) {
                HttpSupport.text(exchange, 400, "Missing sessionId");
                return;
            }

            String state = createState(sessionId, config.clientSecret());
            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                    + "?client_id=" + enc(config.clientId())
                    + "&redirect_uri=" + enc(config.callbackUrl())
                    + "&response_type=code"
                    + "&scope=" + enc("openid profile email")
                    + "&state=" + enc(state);
            HttpSupport.redirect(exchange, authUrl);
        });

        server.createContext(config.callbackPath(), exchange -> {
            if (!exactPath(exchange, config.callbackPath())) return;
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                HttpSupport.methodNotAllowed(exchange, "GET");
                return;
            }

            String code = HttpSupport.queryParam(exchange, "code");
            String state = HttpSupport.queryParam(exchange, "state");
            if (code == null || state == null) {
                HttpSupport.text(exchange, 400, "Missing OAuth callback parameters");
                return;
            }

            String sessionId = verifyState(state, config.clientSecret());
            if (sessionId == null) {
                HttpSupport.text(exchange, 400, "Invalid or expired OAuth state");
                return;
            }

            try {
                String accessToken = exchangeCode(code, config);
                AuthUser user = fetchUser(accessToken);
                sessions.updateState(sessionId, AuthUser.SESSION_KEY, user);
                HttpSupport.redirect(exchange, "/");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                HttpSupport.text(exchange, 500, "OAuth request interrupted");
            } catch (RuntimeException e) {
                HttpSupport.text(exchange, 500, "Google authentication failed");
            }
        });
    }

    private static boolean exactPath(HttpExchange exchange, String expected) throws IOException {
        if (expected.equals(exchange.getRequestURI().getPath())) return true;
        HttpSupport.text(exchange, 404, "Not found");
        return false;
    }

    private static String exchangeCode(String code, GoogleOAuthConfig config) throws IOException, InterruptedException {
        String body = "code=" + enc(code)
                + "&client_id=" + enc(config.clientId())
                + "&client_secret=" + enc(config.clientSecret())
                + "&redirect_uri=" + enc(config.callbackUrl())
                + "&grant_type=authorization_code";

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Google token endpoint returned " + response.statusCode());
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("access_token")) throw new IllegalStateException("Google access token missing");
        return json.get("access_token").getAsString();
    }

    private static AuthUser fetchUser(String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://openidconnect.googleapis.com/v1/userinfo"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Google userinfo endpoint returned " + response.statusCode());
        }
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return new AuthUser(
                string(json, "sub"),
                string(json, "email"),
                string(json, "name"),
                string(json, "picture"));
    }

    private static String string(JsonObject json, String key) {
        return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString() : null;
    }

    private static String createState(String sessionId, String secret) {
        String payload = sessionId + "|" + Instant.now().getEpochSecond();
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encoded + "." + sign(encoded, secret);
    }

    private static String verifyState(String state, String secret) {
        try {
            String[] parts = state.split("\\.", 2);
            if (parts.length != 2) return null;
            byte[] expected = sign(parts[0], secret).getBytes(StandardCharsets.US_ASCII);
            byte[] supplied = parts[1].getBytes(StandardCharsets.US_ASCII);
            if (!MessageDigest.isEqual(expected, supplied)) return null;

            String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            int separator = payload.lastIndexOf('|');
            if (separator <= 0) return null;
            long issuedAt = Long.parseLong(payload.substring(separator + 1));
            long age = Instant.now().getEpochSecond() - issuedAt;
            if (age < 0 || age > STATE_MAX_AGE_SECONDS) return null;
            return payload.substring(0, separator);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String sign(String value, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to sign OAuth state", e);
        }
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
