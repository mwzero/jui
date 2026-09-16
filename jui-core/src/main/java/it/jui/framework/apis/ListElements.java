package it.jui.framework.apis;

import java.util.List;

import it.jui.framework.core.UIContext;

public class ListElements extends BaseElements {

    public ListElements(UIContext ctx) {
        super(ctx);
    }

    public void bullets(List<?> items) {
        list(null, items, false);
    }

    public void bullets(String label, List<?> items) {
        list(label, items, false);
    }

    public void numberedList(List<?> items) {
        list(null, items, true);
    }

    public String dropdownButton(String label, List<String> items) {
        if (items == null || items.isEmpty()) return "";
        String id = ctx.getNextWidgetId("dropdown:" + label);
        String selected = ctx.getValue(id, items.get(0));
        StringBuilder options = new StringBuilder();
        for (String item : items) {
            options.append("<button type='button' onclick=\"sendUpdate('").append(id).append("','")
                    .append(escapeJs(item)).append("')\" class='block w-full px-3 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-700'>")
                    .append(escapeHtml(item)).append("</button>");
        }
        ctx.addHtml("<details class='relative inline-block mb-4'><summary class='list-none cursor-pointer px-4 py-2 rounded-md bg-indigo-600 text-white'>"
                + escapeHtml(label) + ": " + escapeHtml(selected) + "</summary>"
                + "<div class='absolute z-30 mt-2 min-w-48 overflow-hidden rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-xl'>"
                + options + "</div></details>");
        return selected;
    }

    private void list(String label, List<?> items, boolean ordered) {
        String tag = ordered ? "ol" : "ul";
        String classes = ordered ? "list-decimal" : "list-disc";
        StringBuilder html = new StringBuilder("<div class='mb-4'>");
        if (label != null && !label.isBlank()) {
            html.append("<div class='mb-2 text-sm font-medium text-gray-700 dark:text-gray-300'>")
                    .append(escapeHtml(label)).append("</div>");
        }
        html.append("<").append(tag).append(" class='").append(classes)
                .append(" pl-6 space-y-1 text-gray-700 dark:text-gray-300'>");
        if (items != null) {
            for (Object item : items) html.append("<li>").append(escapeHtml(String.valueOf(item))).append("</li>");
        }
        html.append("</").append(tag).append("></div>");
        ctx.addHtml(html.toString());
    }
}
