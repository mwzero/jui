package it.jui.server;

import it.jui.input.WidgetSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** HTTP access is serialized under the owning session's monitor. */
public final class ViewState {
    private final Map<String, Object> internalValues = new ConcurrentHashMap<>();
    private final Map<String, Object> widgetValues = new ConcurrentHashMap<>();
    private Map<String, WidgetSpec> widgets = Map.of();
    private long revision;

    public Map<String, Object> internalValues() { return internalValues; }
    public Map<String, Object> widgetValues() { return widgetValues; }
    public Map<String, WidgetSpec> widgets() { return widgets; }
    public long revision() { return revision; }

    public void rendered(Map<String, WidgetSpec> next) {
        widgets = Map.copyOf(next);
        revision++;
    }

    void clear() {
        internalValues.clear();
        widgetValues.clear();
        widgets = Map.of();
        revision++;
    }
}
