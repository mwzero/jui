package it.my.framework;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UIContext {
    private final StringBuilder htmlOutput = new StringBuilder();
    private final String sessionId;
    private final SessionManager sessionManager;
    private final Map<String, Object> state;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    public UIContext(String sessionId, SessionManager sessionManager) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        this.state = sessionManager.getState(sessionId);
    }
    private String getNextWidgetId() { return "widget-" + widgetCounter.getAndIncrement(); }
    
    private int getInt(String widgetId, int defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        if (value instanceof Number) return ((Number) value).intValue();
        return defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getValue(String widgetId, T defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        return (T) value;
    }

    public void addHtml(String html) { htmlOutput.append(html).append("\n"); }
    public String getHtml() { return htmlOutput.toString(); }

    public void title(String t) { addHtml("<h1 class='text-3xl font-bold mb-4 text-indigo-700'>" + t + "</h1>"); }
    public void header(String t) { addHtml("<h2 class='text-2xl font-semibold mb-3 mt-6 text-gray-800'>" + t + "</h2>"); }
    public void text(String t) { addHtml("<p class='mb-2 text-gray-600'>" + t + "</p>"); }
    public void info(String m) { addHtml("<div class='p-3 rounded-md bg-blue-50 border border-blue-400 text-blue-800 text-sm mb-4'>" + m + "</div>"); }

    public String textInput(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><input type='text' value='%s' onchange=\"sendUpdate('%s', this.value)\" class='mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2' /></div>", label, val, id));
        return val;
    }
    public int slider(String label, int min, int max, int def) {
        String id = getNextWidgetId();
        int val = getInt(id, def); 
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s: <span id='%s-val'>%d</span></label><input type='range' min='%d' max='%d' value='%d' oninput=\"document.getElementById('%s-val').innerText=this.value\" onchange=\"sendUpdate('%s', parseInt(this.value))\" class='mt-1 block w-full' /></div>", label, id, val, min, max, val, id, id));
        return val;
    }
    public boolean button(String label) {
        String id = getNextWidgetId();
        boolean clicked = getValue(id, false);
        if (clicked) { sessionManager.updateState(sessionId, id, false); return true; }
        addHtml(String.format("<div class='mb-4'><button onclick=\"sendUpdate('%s', true)\" class='w-full px-4 py-2 bg-indigo-600 text-white rounded-md'>%s</button></div>", id, label));
        return false;
    }
    public boolean checkbox(String label, boolean def) {
        String id = getNextWidgetId();
        boolean val = getValue(id, def);
        addHtml(String.format("<div class='mb-4 flex items-center'><input type='checkbox' %s onclick=\"sendUpdate('%s', this.checked)\" class='h-4 w-4 text-indigo-600'><label class='ml-2 text-sm'>%s</label></div>", val ? "checked" : "", id, label));
        return val;
    }
    public String selectBox(String label, List<String> opts, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        StringBuilder sb = new StringBuilder();
        for(String o : opts) sb.append(String.format("<option value='%s' %s>%s</option>", o, o.equals(val)?"selected":"", o));
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><select onchange=\"sendUpdate('%s', this.value)\" class='mt-1 block w-full border p-2 rounded-md bg-white'>%s</select></div>", label, id, sb.toString()));
        return val;
    }
    public String datePicker(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><input type='date' value='%s' onchange=\"sendUpdate('%s', this.value)\" class='mt-1 block w-full border p-2 rounded-md' /></div>", label, val, id));
        return val;
    }
    public String textarea(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><textarea onchange=\"sendUpdate('%s', this.value)\" rows='4' class='mt-1 block w-full border p-2 rounded-md'>%s</textarea></div>", label, id, val));
        return val;
    }
    public String fileUpload(String label) {
        String id = getNextWidgetId();
        String val = getValue(id, "Nessun file");
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><input type='file' disabled class='mt-1 block w-full text-sm text-gray-500' /><p class='text-xs text-gray-500'>Stato: %s</p></div>", label, val));
        return val;
    }
}