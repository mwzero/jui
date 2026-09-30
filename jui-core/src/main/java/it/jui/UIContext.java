package it.jui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.jui.apis.AuthElements;
import it.jui.apis.ChartElements;
import it.jui.apis.CrudElements;
import it.jui.apis.DataElements;
import it.jui.apis.FormElements;
import it.jui.apis.InputElements;
import it.jui.apis.LayoutElements;
import it.jui.apis.ListElements;
import it.jui.apis.MapElements;
import it.jui.apis.MediaElements;
import it.jui.apis.NavigationElements;
import it.jui.apis.StatusElements;
import it.jui.apis.TextElements;
import it.jui.server.ISessionManager;
import it.jui.server.SessionState;
import it.jui.server.ViewState;
import it.jui.auth.AuthUser;
import it.jui.input.WidgetSpec;
import java.util.Optional;
import lombok.experimental.Delegate;

public class UIContext {

    private final StringBuilder htmlOutput = new StringBuilder();
    private final Map<String, String> htmlDependencies = new HashMap<>();
    private final SessionState session;
    private final ViewState view;
    private final Map<String, WidgetSpec> renderedWidgets = new HashMap<>();
    private String logoutWidgetId;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    @Delegate private final TextElements textApis;
    @Delegate private final InputElements inputApis;
    @Delegate private final StatusElements statusApis;
    @Delegate private final LayoutElements layoutApis;
    @Delegate private final NavigationElements navigationApis;
    @Delegate private final ListElements listApis;
    @Delegate private final DataElements dataApis;
    @Delegate private final FormElements formApis;
    @Delegate private final CrudElements crudApis;
    @Delegate private final MapElements mapApis;
    @Delegate private final MediaElements mediaApis;
    @Delegate private final ChartElements chartApis;
    @Delegate private final AuthElements authApis;

    public UIContext(String sessionId, ISessionManager sessionManager) {
        this(sessionManager.getOrCreateSession(sessionId), ISessionManager.UI_DEFAULT_VIEW);
    }

    public UIContext(SessionState session, String viewId) {
        this(session, viewId, null);
    }

    public UIContext(SessionState session, String viewId, String logoutWidgetId) {
        this.session = session;
        this.view = session.view(viewId);
        this.logoutWidgetId = logoutWidgetId;
        textApis = new TextElements(this);
        inputApis = new InputElements(this);
        statusApis = new StatusElements(this);
        layoutApis = new LayoutElements(this);
        navigationApis = new NavigationElements(this);
        listApis = new ListElements(this);
        dataApis = new DataElements(this);
        formApis = new FormElements(this);
        crudApis = new CrudElements(this);
        mapApis = new MapElements(this);
        mediaApis = new MediaElements(this);
        chartApis = new ChartElements(this);
        authApis = new AuthElements(this);
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
        Object value = view.internalValues().get(widgetId);
        if (value == null && defaultValue != null) {
            view.internalValues().put(widgetId, defaultValue);
            return defaultValue;
        }
        return (T) value;
    }

    public Object getRawValue(String widgetId) {
        return view.internalValues().get(widgetId);
    }

    public void setValue(String widgetId, Object value) {
        if (value == null) removeValue(widgetId);
        else view.internalValues().put(widgetId, value);
    }

    public void removeValue(String widgetId) {
        view.internalValues().remove(widgetId);
    }

    public boolean consumeBoolean(String widgetId) {
        Object value = view.internalValues().remove(widgetId);
        return Boolean.TRUE.equals(value);
    }

    /** Register only inputs actually emitted by this render, never internal state keys. */
    public void registerWidget(String id, WidgetSpec spec) {
        if (id == null || id.isBlank() || id.startsWith("__jui_"))
            throw new IllegalArgumentException("Invalid widget id");
        renderedWidgets.put(id, java.util.Objects.requireNonNull(spec));
    }

    @SuppressWarnings("unchecked")
    public <T> T getWidgetValue(String id, T defaultValue) {
        Object value = view.widgetValues().get(id);
        if (value == null && defaultValue != null) {
            view.widgetValues().put(id, defaultValue);
            return defaultValue;
        }
        return (T) value;
    }

    public Object getRawWidgetValue(String id) { return view.widgetValues().get(id); }
    public void setWidgetValue(String id, Object value) {
        if (value == null) removeWidgetValue(id);
        else view.widgetValues().put(id, value);
    }
    public void removeWidgetValue(String id) { view.widgetValues().remove(id); }
    public boolean consumeWidgetBoolean(String id) {
        return Boolean.TRUE.equals(view.widgetValues().remove(id));
    }
    public Optional<AuthUser> authenticatedUser() { return session.user(); }
    public boolean consumeLogout(String id) {
        if (!id.equals(logoutWidgetId)) return false;
        logoutWidgetId = null;
        return true;
    }
    /** Publish the complete allowlist atomically, or revoke it on render failure. */
    public void completeRender(boolean successful) {
        view.rendered(successful ? renderedWidgets : Map.of());
    }
}
