package it.jui.framework.core;

import java.util.concurrent.atomic.AtomicInteger;

import it.jui.framework.apis.StatusElements;
import it.jui.framework.apis.TextElements;
import lombok.experimental.Delegate;

public class UIContext {

    private final StringBuilder htmlOutput = new StringBuilder();
    
    private final String sessionId;
    private final SessionManager sessionManager;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    @Delegate
    private final TextElements textApis;

    @Delegate
    private final StatusElements statusApis;

    public UIContext(String sessionId, SessionManager sessionManager) {

        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        
        textApis = new TextElements(this);
        statusApis = new StatusElements(this);
    }

    public String getHtml() { return htmlOutput.toString(); }
    public String getNextWidgetId() { return "widget-" + widgetCounter.getAndIncrement(); }
    public String getNextWidgetId(String label) {
        if (label == null || label.isEmpty()) {
            return "def000"; // Fallback per stringhe vuote
        }

        int hash = label.hashCode();

        // 2. Rendi il numero positivo usando una maschera bitwise.
        // 0x7FFFFFFF rimuove il bit del segno, garantendo un numero tra 0 e 2147483647.
        int positiveHash = hash & 0x7FFFFFFF;

        // 3. Converti in Base36 (numeri 0-9 e lettere a-z)
        String shortId = Integer.toString(positiveHash, 36);

        return "widget-" + shortId; 
    }
    
    public void addHtml(String html) {
        htmlOutput.append(html).append("\n"); 
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String widgetId, T defaultValue) {
        Object value = sessionManager.getState(sessionId).get(widgetId);
        if (value == null) { sessionManager.updateState(sessionId, widgetId, defaultValue); return defaultValue; }
        return (T) value;
    }

}