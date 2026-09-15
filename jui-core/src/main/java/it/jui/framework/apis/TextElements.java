package it.jui.framework.apis;

import java.util.List;
import it.jui.framework.core.UIContext;

public class TextElements extends BaseElements {

    public TextElements(UIContext ctx) {
        super(ctx);
    }

    public void title(String text) {
        title(text, null);
    }

    public void title(String text, String iconName) {
        String safe = escapeHtml(text);
        String icon = (iconName == null || iconName.isBlank())
            ? ""
            : """
            <span class="material-symbols-outlined align-middle text-[32px]
                        text-indigo-700 dark:text-indigo-400"
                    aria-hidden="true">%s</span>
            """.formatted(escapeHtml(iconName));

        ctx.addHtml("""
            <h1 class="mb-4 inline-flex items-center gap-2 text-3xl font-bold
                    text-gray-900 dark:text-gray-100"
                data-jui="title">
            %s
            <span>%s</span>
            </h1>
            """.formatted(icon, safe));
    }

    public void header(String text) {
        ctx.addHtml("<h2 class='text-2xl font-semibold mb-3 mt-6 text-gray-800 dark:text-gray-200'>"
                + escapeHtml(text) + "</h2>");
    }

    public void subheader(String text) {
        ctx.addHtml("<h3 class='text-2xl font-semibold mb-3 mt-6 text-gray-800 dark:text-gray-200'>"
                + escapeHtml(text) + "</h3>");
    }

    public void text(String text) {
        ctx.addHtml("<p class='mb-2 text-gray-600 dark:text-gray-300'>"
                + escapeHtml(text) + "</p>");
    }

    /**
     * Adds explicitly trusted raw HTML to the page. Normal text APIs escape their
     * content and should be preferred for user-provided values.
     */
    public void html(String trustedHtml) {
        ctx.addHtml(trustedHtml == null ? "" : trustedHtml);
    }

    public String textInput(String label, String def) {
        String id = ctx.getNextWidgetId(label);
        String val = ctx.getValue(id, def);
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='text' value='%s' onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500' /></div>",
            escapeHtml(label), escapeHtml(val), id));
        return val;
    }

    public int slider(String label, long min, long max, long def) {
        String id = ctx.getNextWidgetId(label);
        long val = ctx.getValue(id, def);
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s: <span id='%s-val'>%d</span></label>" +
            "<input type='range' min='%d' max='%d' value='%d' oninput=\"document.getElementById('%s-val').innerText=this.value\" onchange=\"sendUpdate('%s', parseInt(this.value))\" " +
            "class='mt-1 block w-full h-2 bg-gray-200 dark:bg-gray-600 rounded-lg appearance-none cursor-pointer' /></div>",
            escapeHtml(label), id, val, min, max, val, id, id));
        return (int) val;
    }

    /**
     * Returns true only for the render triggered by the click. The event is
     * consumed immediately and therefore does not remain true in session state.
     */
    public boolean button(String label) {
        String id = ctx.getNextWidgetId(label);
        boolean clicked = ctx.consumeBoolean(id);
        ctx.addHtml(String.format(
            "<div class='mb-4'><button onclick=\"sendUpdate('%s', true)\" " +
            "class='w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white rounded-md transition'>%s</button></div>",
            id, escapeHtml(label)));
        return clicked;
    }

    public boolean checkbox(String label, boolean def) {
        String id = ctx.getNextWidgetId(label);
        boolean val = ctx.getValue(id, def);
        ctx.addHtml(String.format(
            "<div class='mb-4 flex items-center'><input type='checkbox' %s onclick=\"sendUpdate('%s', this.checked)\" " +
            "class='h-4 w-4 text-indigo-600 border-gray-300 dark:border-gray-600 dark:bg-gray-700 rounded'><label class='ml-2 text-sm text-gray-900 dark:text-gray-300'>%s</label></div>",
            val ? "checked" : "", id, escapeHtml(label)));
        return val;
    }

    public String selectBox(String label, List<String> opts, String def) {
        String id = ctx.getNextWidgetId(label);
        String val = ctx.getValue(id, def);
        StringBuilder sb = new StringBuilder();
        for (String option : opts) {
            String safeOption = escapeHtml(option);
            sb.append(String.format("<option value='%s' %s>%s</option>",
                    safeOption, option.equals(val) ? "selected" : "", safeOption));
        }
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<select onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</select></div>",
            escapeHtml(label), id, sb.toString()));
        return val;
    }

    public String datePicker(String label, String def) {
        String id = ctx.getNextWidgetId(label);
        String val = ctx.getValue(id, def);
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='date' value='%s' onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white' /></div>",
            escapeHtml(label), escapeHtml(val), id));
        return val;
    }

    public String textarea(String label, String def) {
        String id = ctx.getNextWidgetId(label);
        String val = ctx.getValue(id, def);
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<textarea onchange=\"sendUpdate('%s', this.value)\" rows='4' " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</textarea></div>",
            escapeHtml(label), id, escapeHtml(val)));
        return val;
    }

    public String fileUpload(String label) {
        String id = ctx.getNextWidgetId(label);
        String val = ctx.getValue(id, "Nessun file");
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='file' disabled class='mt-1 block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 dark:file:bg-indigo-900 dark:file:text-indigo-300 hover:file:bg-indigo-100' />" +
            "<p class='text-xs text-gray-500 dark:text-gray-400'>Stato: %s</p></div>",
            escapeHtml(label), escapeHtml(val)));
        return val;
    }
}
