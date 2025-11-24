package it.my.framework;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AGGIORNATO: Supporto Sidebar Multi-View e layout Split.
 */
public class UIContext {
    private final StringBuilder mainBuffer = new StringBuilder();
    private final StringBuilder sidebarBuffer = new StringBuilder();
    private boolean isSidebarMode = false;
    
    private final String sessionId;
    private final SessionManager sessionManager;
    private final Map<String, Object> state;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    public UIContext(String sessionId, SessionManager sessionManager) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        this.state = sessionManager.getState(sessionId);
    }
    
    /**
     * Esegue il blocco di codice in modalità Sidebar.
     * I widget definiti all'interno verranno renderizzati nella colonna sinistra.
     */
    public void sidebar(Runnable block) {
        isSidebarMode = true;
        block.run();
        isSidebarMode = false;
    }
    
    // Helper per recuperare/aggiornare stato manualmente (utile per navigazione)
    @SuppressWarnings("unchecked")
    public <T> T getState(String key, T defaultValue) {
        Object val = state.get(key);
        if (val == null) return defaultValue;
        return (T) val;
    }
    
    public void updateState(String key, Object value) {
        sessionManager.updateState(sessionId, key, value);
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

    public void addHtml(String html) { 
        if (isSidebarMode) {
            sidebarBuffer.append(html).append("\n");
        } else {
            mainBuffer.append(html).append("\n");
        }
    }
    
    // Genera il layout finale: Sidebar (se esiste) + Main Content
    public String getHtml() { 
        String sidebarContent = sidebarBuffer.toString();
        String mainContent = mainBuffer.toString();
        
        if (sidebarContent.trim().isEmpty()) {
            return "<div class='p-6 fade-in'>" + mainContent + "</div>";
        } else {
            return String.format(
                "<div class='flex flex-col md:flex-row min-h-[80vh]'>" +
                "  <aside class='w-full md:w-64 bg-gray-50 dark:bg-gray-800/50 border-r border-gray-200 dark:border-gray-700 p-4 flex-shrink-0'>" +
                "    %s" +
                "  </aside>" +
                "  <main class='flex-1 p-6 fade-in'>" +
                "    %s" +
                "  </main>" +
                "</div>", 
                sidebarContent, mainContent
            );
        }
    }

    public void title(String t) { 
        String cls = isSidebarMode ? "text-xl font-bold mb-4 text-indigo-700 dark:text-indigo-400" : "text-3xl font-bold mb-4 text-indigo-700 dark:text-indigo-400";
        addHtml("<h1 class='" + cls + "'>" + t + "</h1>"); 
    }
    public void header(String t) { 
        String cls = isSidebarMode ? "text-lg font-semibold mb-2 mt-4 text-gray-800 dark:text-gray-200" : "text-2xl font-semibold mb-3 mt-6 text-gray-800 dark:text-gray-200";
        addHtml("<h2 class='" + cls + "'>" + t + "</h2>"); 
    }
    public void text(String t) { 
        addHtml("<p class='mb-2 text-gray-600 dark:text-gray-300 " + (isSidebarMode ? "text-sm" : "") + "'>" + t + "</p>"); 
    }
    public void info(String m) { 
        addHtml("<div class='p-3 rounded-md bg-blue-50 dark:bg-blue-900/30 border border-blue-400 dark:border-blue-700 text-blue-800 dark:text-blue-300 text-sm mb-4'>" + m + "</div>"); 
    }

    public String textInput(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='text' value='%s' onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500' /></div>", 
            label, val, id));
        return val;
    }
    
    public int slider(String label, int min, int max, int def) {
        String id = getNextWidgetId();
        int val = getInt(id, def); 
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s: <span id='%s-val'>%d</span></label>" +
            "<input type='range' min='%d' max='%d' value='%d' oninput=\"document.getElementById('%s-val').innerText=this.value\" onchange=\"sendUpdate('%s', parseInt(this.value))\" " +
            "class='mt-1 block w-full h-2 bg-gray-200 dark:bg-gray-600 rounded-lg appearance-none cursor-pointer' /></div>", 
            label, id, val, min, max, val, id, id));
        return val;
    }
    
    public boolean button(String label) {
        String id = getNextWidgetId();
        boolean clicked = getValue(id, false);
        if (clicked) { sessionManager.updateState(sessionId, id, false); return true; }
        addHtml(String.format(
            "<div class='mb-4'><button onclick=\"sendUpdate('%s', true)\" " +
            "class='w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white rounded-md transition text-sm font-medium shadow-sm'>%s</button></div>", 
            id, label));
        return false;
    }
    
    public boolean checkbox(String label, boolean def) {
        String id = getNextWidgetId();
        boolean val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4 flex items-center'><input type='checkbox' %s onclick=\"sendUpdate('%s', this.checked)\" " +
            "class='h-4 w-4 text-indigo-600 border-gray-300 dark:border-gray-600 dark:bg-gray-700 rounded'><label class='ml-2 text-sm text-gray-900 dark:text-gray-300'>%s</label></div>", 
            val ? "checked" : "", id, label));
        return val;
    }
    
    public String selectBox(String label, List<String> opts, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        StringBuilder sb = new StringBuilder();
        for(String o : opts) sb.append(String.format("<option value='%s' %s>%s</option>", o, o.equals(val)?"selected":"", o));
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<select onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</select></div>", 
            label, id, sb.toString()));
        return val;
    }
    
    public String datePicker(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='date' value='%s' onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white' /></div>", 
            label, val, id));
        return val;
    }
    
    public String textarea(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<textarea onchange=\"sendUpdate('%s', this.value)\" rows='4' " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</textarea></div>", 
            label, id, val));
        return val;
    }
    
    public String fileUpload(String label) {
        String id = getNextWidgetId();
        String val = getValue(id, "Nessun file");
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='file' disabled class='mt-1 block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 dark:file:bg-indigo-900 dark:file:text-indigo-300 hover:file:bg-indigo-100' />" +
            "<p class='text-xs text-gray-500 dark:text-gray-400'>Stato: %s</p></div>", 
            label, val));
        return val;
    }
}