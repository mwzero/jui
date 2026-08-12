package it.jui.framework.server;

import java.util.Map;

public interface ISessionManager {

    Map<String, Object> getState(String sessionId);

    void updateState(String sessionId, String widgetId, Object value);

    void removeSession(String sessionId);
}