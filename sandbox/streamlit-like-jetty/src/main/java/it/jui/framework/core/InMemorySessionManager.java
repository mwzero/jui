package it.jui.framework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionManager implements SessionManager {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> sessions = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> getState(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
    }

    @Override
    public void updateState(String sessionId, String widgetId, Object value) {
        getState(sessionId).put(widgetId, value);
    }

    @Override
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}