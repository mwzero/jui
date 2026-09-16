package it.jui.framework.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.jui.framework.apis.AuthElements;
import it.jui.framework.apis.ChartElements;
import it.jui.framework.apis.CrudElements;
import it.jui.framework.apis.DataElements;
import it.jui.framework.apis.FormElements;
import it.jui.framework.apis.InputElements;
import it.jui.framework.apis.LayoutElements;
import it.jui.framework.apis.MapElements;
import it.jui.framework.apis.MediaElements;
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

    @Delegate private final TextElements textApis;
    @Delegate private final InputElements inputApis;
    @Delegate private final StatusElements statusApis;
    @Delegate private final LayoutElements layoutApis;
    @Delegate private final NavigationElements navigationApis;
    @Delegate private final DataElements dataApis;
    @Delegate private final FormElements formApis;
    @Delegate private final CrudElements crudApis;
    @Delegate private final MapElements mapApis;
    @Delegate private final MediaElements mediaApis;
    @Delegate private final ChartElements chartApis;
    @Delegate private final AuthElements authApis;

    public UIContext(String sessionId, ISessionManager sessionManager) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        textApis = new TextElements(this);
        inputApis = new InputElements(this);
        statusApis = new StatusElements(this);
        layoutApis = new LayoutElements(this);
        navigationApis = new NavigationElements(this);
        dataApis = new DataElements(this);
        formApis = new FormElements(this);
        crudApis = new CrudElements(this);
        mapApis = new MapElements(this);
        mediaApis = new MediaElements(this);
        chartApis = new ChartElements(this);
        authApis = new AuthElements(this);
    }

    void logSessionState() {
        System.out.println("Session State: " + sessionManager.getState(sessionId));
        System.out.println("Session Id: " + sessionId);
        System.out.println("sessionManager: " + sessionManager);
    }

    public String getHtml() {
        return htmlOutput.toString();
    }

    public String getNextWidgetId() {
        return "widget-" + widgetCounter.getAndIncrement();
    }

    public String getNextWidgetId(String key) {
        if (key == null || key.isBlank()) return getNextWidgetId();
        int positiveHash = key.hashCode() & 0x7FFFFFFF;
        return "widget-" + Integer.toString(positiveHash, 36);
    }

    public void addHtml(String html) {
        htmlOutput.append(html).append("\n");
    }

    /**
     * Renders a nested UI fragment using this same context, then removes the
     * fragment from the main stream so a composite component can wrap it.
     */
    public String capture(Runnable renderer) {
        if (renderer == null) return "";
        int start = htmlOutput.length();
        renderer.run();
        String fragment = htmlOutput.substring(start);
        htmlOutput.setLength(start);
        return fragment;
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

    public Object getRawValue(String widgetId) {
        return sessionManager.getState(sessionId).get(widgetId);
    }

    public void setValue(String widgetId, Object value) {
        if (value == null) removeValue(widgetId);
        else sessionManager.updateState(sessionId, widgetId, value);
    }

    public void removeValue(String widgetId) {
        sessionManager.getState(sessionId).remove(widgetId);
    }

    public boolean consumeBoolean(String widgetId) {
        Object value = sessionManager.getState(sessionId).remove(widgetId);
        return Boolean.TRUE.equals(value);
    }
}
