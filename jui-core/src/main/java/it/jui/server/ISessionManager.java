package it.jui.server;

import it.jui.auth.AuthUser;
import java.util.Map;
import java.util.Optional;

public interface ISessionManager extends AutoCloseable {
    String UI_DEFAULT_VIEW = "default";
    SessionState createSession();
    Optional<SessionState> findSession(String sessionId);
    /** Trusted Java entry point, also used by standalone UIContext instances. */
    SessionState getOrCreateSession(String sessionId);
    SessionState rotateSession(SessionState previous, AuthUser user);

    default Map<String, Object> getState(String sessionId) {
        return getOrCreateSession(sessionId).view(UI_DEFAULT_VIEW).internalValues();
    }

    default void updateState(String sessionId, String key, Object value) {
        if (value == null) getState(sessionId).remove(key);
        else getState(sessionId).put(key, value);
    }

    void removeSession(String sessionId);
    default void startCleanup() {}
    @Override default void close() {}
}
