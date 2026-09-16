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
