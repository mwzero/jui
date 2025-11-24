package it.jui.framework.core;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.jui.framework.apis.TextElements;
import it.jui.framework.session.SessionManager;
import lombok.experimental.Delegate;

public class UIContext {

    private final StringBuilder htmlOutput = new StringBuilder();
    
    private final String sessionId;
    private final SessionManager sessionManager;
    private final Map<String, Object> state;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    @Delegate
    private final TextElements textApis;
    
    public UIContext(String sessionId, SessionManager sessionManager) {

        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        this.state = sessionManager.getState(sessionId);

        textApis = new TextElements(sessionId, sessionManager, htmlOutput, widgetCounter);
    }

}