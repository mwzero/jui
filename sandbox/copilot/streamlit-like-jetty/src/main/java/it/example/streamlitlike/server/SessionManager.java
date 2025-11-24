
package it.example.streamlitlike.server;

import java.util.Map;

public interface SessionManager {
    Map<String, String> getSession(String sessionId);
    void updateSession(String sessionId, Map<String, String> updates);
}
