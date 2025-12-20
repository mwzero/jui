package it.jui.framework.apis;

import java.util.List;

import it.jui.framework.core.UIContext;

public class NavigationElements extends BaseElements {

    public NavigationElements(UIContext ctx) {
        super(ctx);
    }

    public String tabs(String label, List<String> options, String defaultOption) {
        if (options == null || options.isEmpty()) {
            return "";
        }

        String id = ctx.getNextWidgetId(label);
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
                id, opt, classes, opt));
        }

        ctx.addHtml(String.format(
            "<div class='mb-6'>" +
            "<div class='flex items-center justify-between mb-2'>" +
            "<span class='text-sm font-medium text-gray-700 dark:text-gray-300'>%s</span>" +
            "<span class='text-xs text-gray-500 dark:text-gray-400'>Selezionato: %s</span>" +
            "</div>" +
            "<div class='flex flex-wrap gap-2'>%s</div>" +
            "</div>",
            label, active, sb.toString()));

        return active;
    }
}
