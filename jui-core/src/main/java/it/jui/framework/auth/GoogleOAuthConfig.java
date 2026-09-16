package it.jui.framework.auth;

import java.net.URI;

public record GoogleOAuthConfig(String clientId, String clientSecret, String callbackUrl) {

    public GoogleOAuthConfig {
        if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("Google clientId is required");
        if (clientSecret == null || clientSecret.isBlank()) throw new IllegalArgumentException("Google clientSecret is required");
        if (callbackUrl == null || callbackUrl.isBlank()) throw new IllegalArgumentException("Google callbackUrl is required");
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
