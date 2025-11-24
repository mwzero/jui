
package it.example.streamlitlike.server;

import java.util.HashMap;
import java.util.Map;

public class InMemorySessionManager implements SessionManager {
    private final Map<String, Map<String, String>> sessions = new HashMap<>();

    @Override
    public Map<String, String> getSession(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new HashMap<>());
    }

    @Override
    public void updateSession(String sessionId, Map<String, String> updates) {
        getSession(sessionId).putAll(updates);
    }
}
