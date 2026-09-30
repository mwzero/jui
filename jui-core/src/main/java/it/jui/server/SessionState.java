package it.jui.server;

import it.jui.auth.AuthUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Identity and protocol metadata never reside in application or widget values. */
public final class SessionState {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final String id;
    private final String csrfToken = randomToken();
    private final Instant createdAt;
    private Instant lastAccess;
    private boolean active = true;
    private AuthUser user;
    private String oauthState;
    private long oauthGeneration;
    private Instant oauthExpiresAt;
    private String notice;
    private final Map<String, ViewState> views = new HashMap<>();

    SessionState(String id, Instant now) {
        this.id = id;
        createdAt = lastAccess = now;
    }

    public static String randomToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String id() { return id; }
    public String csrfToken() { return csrfToken; }
    public synchronized Optional<AuthUser> user() { return Optional.ofNullable(user); }

    /** For trusted authentication providers only; never called by browser updates. */
    public synchronized void authenticate(AuthUser user) {
        if (user == null || user.id() == null || user.id().isBlank())
            throw new IllegalArgumentException("Authenticated user requires an id");
        this.user = user;
    }

    public synchronized ViewState view(String viewId) {
        return views.computeIfAbsent(viewId, ignored -> new ViewState());
    }

    public synchronized Optional<ViewState> findView(String viewId) {
        return Optional.ofNullable(views.get(viewId));
    }

    synchronized boolean valid(Instant now, Duration idle, Duration maximum) {
        return active && now.isBefore(lastAccess.plus(idle)) && now.isBefore(createdAt.plus(maximum));
    }

    synchronized void touch(Instant now) { lastAccess = now; }

    synchronized void invalidate() {
        active = false;
        user = null;
        oauthState = null;
        views.values().forEach(ViewState::clear);
        views.clear();
        notice = null;
    }

    public synchronized String beginOAuth(Instant now) {
        oauthGeneration++;
        oauthState = randomToken();
        oauthExpiresAt = now.plus(Duration.ofMinutes(10));
        return oauthState;
    }

    public synchronized long oauthGeneration() { return oauthGeneration; }

    public synchronized boolean consumeOAuth(String supplied, Instant now) {
        if (oauthState == null || !constantTimeEquals(oauthState, supplied)) return false;
        oauthState = null;
        return active && now.isBefore(oauthExpiresAt);
    }

    public boolean acceptsCsrf(String supplied) { return constantTimeEquals(csrfToken, supplied); }
    public synchronized void notice(String message) { notice = message; }
    public synchronized String consumeNotice() {
        String message = notice;
        notice = null;
        return message;
    }

    private static boolean constantTimeEquals(String expected, String supplied) {
        return supplied != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8));
    }
}
