package it.jui.apis;

import java.util.List;
import java.util.function.Consumer;

import it.jui.UIContext;

public class NavigationElements extends BaseElements {

    public NavigationElements(UIContext ctx) {
        super(ctx);
    }

    public String tabs(String label, List<String> options, String defaultOption) {
        if (options == null || options.isEmpty()) return "";

        String id = ctx.getNextWidgetId("tabs:" + label);
        String initial = defaultOption != null ? defaultOption : options.get(0);
        String active = ctx.getValue(id, initial);

        StringBuilder sb = new StringBuilder();
        for (String opt : options) {
            boolean isActive = opt.equals(active);
            String classes = isActive
                    ? "bg-indigo-600 text-white border-indigo-600 shadow-md"
                    : "bg-white text-gray-700 border-gray-300 hover:bg-gray-50 dark:bg-gray-800 dark:text-gray-200 dark:border-gray-600";
            sb.append(String.format(
                "<button onclick=\"sendUpdate('%s', '%s')\" class='px-4 py-2 text-sm font-medium border rounded-md transition %s'>%s</button>",
                id, escapeJs(opt), classes, escapeHtml(opt)));
        }

        ctx.addHtml(String.format(
            "<div class='mb-6'><div class='flex items-center justify-between mb-2'>" +
            "<span class='text-sm font-medium text-gray-700 dark:text-gray-300'>%s</span>" +
            "<span class='text-xs text-gray-500 dark:text-gray-400'>Selected: %s</span></div>" +
            "<div class='flex flex-wrap gap-2'>%s</div></div>",
            escapeHtml(label), escapeHtml(active), sb));

        return active;
    }

    public String sidebar(String title, List<String> items, String defaultItem) {
        return sidebar(title, items, defaultItem, null);
    }

    /**
     * Renders a two-column application shell and invokes content with the selected
     * item. This keeps navigation and page rendering in a single compact Java call.
     */
    public String sidebar(String title, List<String> items, String defaultItem, Consumer<String> content) {
        if (items == null || items.isEmpty()) return "";
        String id = ctx.getNextWidgetId("sidebar:" + title);
        String initial = defaultItem != null ? defaultItem : items.get(0);
        String active = ctx.getValue(id, initial);

        StringBuilder nav = new StringBuilder();
        for (String item : items) {
            boolean selected = item.equals(active);
            String classes = selected
                    ? "bg-indigo-600 text-white"
                    : "text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700";
            nav.append("<button type='button' onclick=\"sendUpdate('").append(id).append("', '")
                    .append(escapeJs(item)).append("')\" class='w-full text-left px-3 py-2 rounded-md text-sm transition ")
                    .append(classes).append("'>").append(escapeHtml(item)).append("</button>");
        }

        String body = content == null ? "" : ctx.capture(() -> content.accept(active));
        ctx.addHtml("<div class='grid grid-cols-1 md:grid-cols-[14rem_minmax(0,1fr)] gap-5 mb-6'>"
                + "<aside class='rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900/40 p-3'>"
                + "<div class='px-2 pb-3 text-sm font-semibold text-gray-900 dark:text-gray-100'>" + escapeHtml(title) + "</div>"
                + "<nav class='space-y-1'>" + nav + "</nav></aside>"
                + "<main class='min-w-0'>" + body + "</main></div>");
        return active;
    }
}
