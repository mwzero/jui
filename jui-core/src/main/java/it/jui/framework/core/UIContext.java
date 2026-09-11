package it.jui.framework.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.jui.framework.apis.DataElements;
import it.jui.framework.apis.FormElements;
import it.jui.framework.apis.LayoutElements;
import it.jui.framework.apis.MapElements;
import it.jui.framework.apis.NavigationElements;
import it.jui.framework.apis.StatusElements;
import it.jui.framework.apis.TextElements;
import it.jui.framework.server.ISessionManager;
import lombok.experimental.Delegate;

public class UIContext {

    private final StringBuilder htmlOutput = new StringBuilder();
    private final Map<String, String> htmlDependencies = new HashMap<>();

    private final String sessionId;
    private final ISessionManager sessionManager;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    @Delegate
    private final TextElements textApis;

    @Delegate
    private final StatusElements statusApis;

    @Delegate
    private final LayoutElements layoutApis;

    @Delegate
    private final NavigationElements navigationApis;

    @Delegate
    private final DataElements dataApis;

    @Delegate
    private final FormElements formApis;

    @Delegate
    private final MapElements mapApis;

    public UIContext(String sessionId, ISessionManager sessionManager) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;

        textApis = new TextElements(this);
        statusApis = new StatusElements(this);
        layoutApis = new LayoutElements(this);
        navigationApis = new NavigationElements(this);
        dataApis = new DataElements(this);
        formApis = new FormElements(this);
        mapApis = new MapElements(this);
    }

    void logSessionState() {
        System.out.println("Session State: " + sessionManager.getState(sessionId));
        System.out.println("Session Id: " + sessionId);
        System.out.println("sessionManager: " + sessionManager);
    }

    public String getHtml() {
        return htmlOutput.toString();
    }

    /**
     * Returns a render-order based id. Prefer {@link #getNextWidgetId(String)} for
     * interactive widgets so their state survives a re-render deterministically.
     */
    public String getNextWidgetId() {
        return "widget-" + widgetCounter.getAndIncrement();
    }

    /**
     * Returns a deterministic widget id for a stable application-level key such as
     * a label. The same key produces the same id on every render.
     */
    public String getNextWidgetId(String key) {
        if (key == null || key.isBlank()) {
            return getNextWidgetId();
        }

        int positiveHash = key.hashCode() & 0x7FFFFFFF;
        return "widget-" + Integer.toString(positiveHash, 36);
    }

    public void addHtml(String html) {
        htmlOutput.append(html).append("\n");
    }

    public void addHtmlDependency(String key, String html) {
        htmlDependencies.putIfAbsent(key, html);
    }

    public Map<String, String> getHtmlDependencies() {
        return new HashMap<>(htmlDependencies);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String widgetId, T defaultValue) {
        Object value = sessionManager.getState(sessionId).get(widgetId);
        if (value == null && defaultValue != null) {
            sessionManager.updateState(sessionId, widgetId, defaultValue);
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * Consumes a boolean event from session state. This is intentionally different
     * from {@link #getValue(String, Object)}: event state is removed immediately so
     * actions such as buttons are true for exactly one render.
     */
    public boolean consumeBoolean(String widgetId) {
        Object value = sessionManager.getState(sessionId).remove(widgetId);
        return Boolean.TRUE.equals(value);
    }
}
