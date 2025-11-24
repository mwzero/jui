package it.jui.framework.apis;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.jui.framework.session.SessionManager;

public class TextElements extends BaseElements {

    public TextElements(String sessionId, SessionManager sessionManager, StringBuilder htmlOutput,
            AtomicInteger widgetCounter) {
        super(sessionId, sessionManager, htmlOutput, widgetCounter);
    }

    public void addHtml(String html) { htmlOutput.append(html).append("\n"); }
    public String getHtml() { return htmlOutput.toString(); }

    public void title(String t) { 
        addHtml("<h1 class='text-3xl font-bold mb-4 text-indigo-700 dark:text-indigo-400'>" + t + "</h1>"); 
    }
    public void header(String t) { 
        addHtml("<h2 class='text-2xl font-semibold mb-3 mt-6 text-gray-800 dark:text-gray-200'>" + t + "</h2>"); 
    }

    public void subheader(String t) {
        addHtml("<h3 class='text-2xl font-semibold mb-3 mt-6 text-gray-800 dark:text-gray-200'>" + t + "</h3>"); 
	}

    public void text(String t) { 
        addHtml("<p class='mb-2 text-gray-600 dark:text-gray-300'>" + t + "</p>"); 
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
            "class='w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white rounded-md transition'>%s</button></div>", 
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