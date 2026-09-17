package it.jui.apis;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.jui.UIContext;

public class TextElements extends BaseElements {

    private static final Pattern MARKDOWN_LINK = Pattern.compile("\\[([^\\]]+)]\\((https?://[^\\s)]+)\\)");

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
        ctx.addHtml("<h3 class='text-xl font-semibold mb-3 mt-5 text-gray-800 dark:text-gray-200'>"
                + escapeHtml(text) + "</h3>");
    }

    public void text(String text) {
        ctx.addHtml("<p class='mb-2 text-gray-600 dark:text-gray-300'>"
                + escapeHtml(text) + "</p>");
    }

    public void caption(String text) {
        ctx.addHtml("<p class='mb-2 text-sm text-gray-500 dark:text-gray-400'>"
                + escapeHtml(text) + "</p>");
    }

    public void divider() {
        ctx.addHtml("<hr class='my-6 border-gray-200 dark:border-gray-700' />");
    }

    public void code(String code) {
        code(code, null);
    }

    public void code(String code, String language) {
        String label = language == null || language.isBlank()
                ? ""
                : "<div class='px-3 py-1 text-xs text-gray-400 border-b border-gray-700'>" + escapeHtml(language) + "</div>";
        ctx.addHtml("<div class='my-4 overflow-hidden rounded-lg bg-gray-950 text-gray-100'>" + label
                + "<pre class='overflow-x-auto p-4 text-sm'><code>" + escapeHtml(code) + "</code></pre></div>");
    }

    /**
     * Renders a deliberately small, safe Markdown subset without adding a server
     * dependency. Supported constructs are headings, bullet lists, emphasis,
     * inline code and http(s) links. Raw HTML is always escaped.
     */
    public void markdown(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return;
        }

        StringBuilder out = new StringBuilder("<div class='jui-markdown space-y-2 text-gray-700 dark:text-gray-300'>");
        boolean inList = false;
        for (String line : markdown.split("\\R", -1)) {
            String trimmed = line.trim();
            if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                if (!inList) {
                    out.append("<ul class='list-disc pl-6 space-y-1'>");
                    inList = true;
                }
                out.append("<li>").append(inlineMarkdown(trimmed.substring(2))).append("</li>");
                continue;
            }
            if (inList) {
                out.append("</ul>");
                inList = false;
            }
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.startsWith("### ")) {
                out.append("<h3 class='text-lg font-semibold'>").append(inlineMarkdown(trimmed.substring(4))).append("</h3>");
            } else if (trimmed.startsWith("## ")) {
                out.append("<h2 class='text-xl font-semibold'>").append(inlineMarkdown(trimmed.substring(3))).append("</h2>");
            } else if (trimmed.startsWith("# ")) {
                out.append("<h1 class='text-2xl font-bold'>").append(inlineMarkdown(trimmed.substring(2))).append("</h1>");
            } else {
                out.append("<p>").append(inlineMarkdown(trimmed)).append("</p>");
            }
        }
        if (inList) out.append("</ul>");
        out.append("</div>");
        ctx.addHtml(out.toString());
    }

    private String inlineMarkdown(String text) {
        String safe = escapeHtml(text);
        safe = safe.replaceAll("`([^`]+)`", "<code class='px-1 py-0.5 rounded bg-gray-100 dark:bg-gray-700'>$1</code>");
        safe = safe.replaceAll("\\*\\*([^*]+)\\*\\*", "<strong>$1</strong>");
        safe = safe.replaceAll("__([^_]+)__", "<strong>$1</strong>");
        safe = safe.replaceAll("(?<!\\*)\\*([^*]+)\\*(?!\\*)", "<em>$1</em>");

        Matcher matcher = MARKDOWN_LINK.matcher(safe);
        StringBuffer linked = new StringBuffer();
        while (matcher.find()) {
            String replacement = "<a class='text-indigo-600 dark:text-indigo-400 underline' target='_blank' rel='noopener noreferrer' href='"
                    + escapeHtml(matcher.group(2)) + "'>" + matcher.group(1) + "</a>";
            matcher.appendReplacement(linked, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(linked);
        return linked.toString();
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
        return textarea(label, def, 4, false);
    }

    public String codeEditor(String label, String def) {
        return textarea(label, def, 24, true);
    }

    private String textarea(String label, String def, int rows, boolean code) {
        String id = ctx.getNextWidgetId(label);
        String val = ctx.getValue(id, def);
        ctx.addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<textarea onchange=\"sendUpdate('%s', this.value)\" rows='%d' %s" +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white %s'>%s</textarea></div>",
            escapeHtml(label), id, rows, code ? "spellcheck='false' " : "", code ? "font-mono text-sm" : "", escapeHtml(val)));
        return val;
    }

    /** @deprecated use {@code ui.fileUploader(...)} which returns the uploaded file. */
    @Deprecated
    public String fileUpload(String label) {
        String id = ctx.getNextWidgetId("legacy-file:" + label);
        Object raw = ctx.getRawValue(id);
        String val = raw == null ? "Nessun file" : String.valueOf(raw);
        ctx.addHtml("<p class='text-sm text-gray-500'>Use fileUploader(\"" + escapeHtml(label) + "\") for real uploads.</p>");
        return val;
    }
}
