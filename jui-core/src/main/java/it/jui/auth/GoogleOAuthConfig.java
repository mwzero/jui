package it.jui.auth;

import java.net.URI;

public record GoogleOAuthConfig(String clientId, String clientSecret, String callbackUrl) {

    public GoogleOAuthConfig {
        if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("Google clientId is required");
        if (clientSecret == null || clientSecret.isBlank()) throw new IllegalArgumentException("Google clientSecret is required");
        if (callbackUrl == null || callbackUrl.isBlank()) throw new IllegalArgumentException("Google callbackUrl is required");
        URI uri = URI.create(callbackUrl);
        String host = uri.getHost();
        boolean local = "localhost".equalsIgnoreCase(host) || "[::1]".equals(host) || "::1".equals(host)
                || (host != null && host.matches("127\\.([0-9]{1,3}\\.){2}[0-9]{1,3}")
                    && java.util.Arrays.stream(host.split("\\.")).allMatch(part -> Integer.parseInt(part) <= 255));
        if (host == null || uri.getRawUserInfo() != null || uri.getRawFragment() != null
                || uri.getRawQuery() != null || uri.getPath() == null || uri.getPath().isBlank()
                || "/".equals(uri.getPath()) || "/ui".equals(uri.getPath())
                || "/auth/google/login".equals(uri.getPath())
                || !("https".equalsIgnoreCase(uri.getScheme()) || (local && "http".equalsIgnoreCase(uri.getScheme()))))
            throw new IllegalArgumentException("Google callback must be an absolute HTTPS URL (HTTP is allowed only on loopback), with a dedicated path");
    }

    public static GoogleOAuthConfig fromEnv() {
        return new GoogleOAuthConfig(
                System.getenv("GOOGLE_CLIENT_ID"),
                System.getenv("GOOGLE_CLIENT_SECRET"),
                System.getenv("GOOGLE_CALLBACK_URL"));
    }

    public String callbackPath() {
        String path = URI.create(callbackUrl).getPath();
        return path == null || path.isBlank() ? "/auth/google/callback" : path;
    }
}
