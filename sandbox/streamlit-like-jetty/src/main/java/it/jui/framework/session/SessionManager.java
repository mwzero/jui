package it.jui.framework.session;

import java.util.Map;

public interface SessionManager {

    Map<String, Object> getState(String sessionId);

    void updateState(String sessionId, String widgetId, Object value);

    void removeSession(String sessionId);
}