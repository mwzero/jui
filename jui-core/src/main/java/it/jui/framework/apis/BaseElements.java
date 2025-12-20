package it.jui.framework.apis;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.jui.framework.session.SessionManager;

public class BaseElements {

    protected StringBuilder htmlOutput;
    protected final String sessionId;
    protected SessionManager sessionManager;
    protected Map<String, Object> state;
    protected AtomicInteger widgetCounter;

    public BaseElements(String sessionId, SessionManager sessionManager, StringBuilder htmlOutput, AtomicInteger widgetCounter) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        this.widgetCounter = widgetCounter;
        this.htmlOutput = htmlOutput;
        this.state = sessionManager.getState(sessionId);
    }
    protected String getNextWidgetId() { return "widget-" + widgetCounter.getAndIncrement(); }
    
    protected int getInt(String widgetId, int defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        if (value instanceof Number) return ((Number) value).intValue();
        return defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T getValue(String widgetId, T defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        return (T) value;
    }
}