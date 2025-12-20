package it.jui.framework.apis;

import it.jui.framework.core.UIContext;

public class LayoutElements extends BaseElements {

    public LayoutElements(UIContext ctx) {
        super(ctx);
    }

    public void card(String title, String body) {
        ctx.addHtml(String.format(
            "<div class='mb-4 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-sm overflow-hidden'>" +
            "<div class='px-4 py-3 border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900/40'>" +
            "<h3 class='text-lg font-semibold text-gray-800 dark:text-gray-100'>%s</h3>" +
            "</div>" +
            "<div class='px-4 py-3 text-gray-600 dark:text-gray-300'>%s</div>" +
            "</div>",
            title, body));
    }

    public void metricCard(String label, String value, String trend, boolean trendIsPositive) {
        String trendClasses = trendIsPositive
                ? "text-green-600 dark:text-green-400"
                : "text-red-600 dark:text-red-400";
        String trendIcon = trendIsPositive
                ? "<span class='text-xs mr-1'>▲</span>"
                : "<span class='text-xs mr-1'>▼</span>";

        ctx.addHtml(String.format(
            "<div class='p-4 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-sm'>" +
            "<dl>" +
            "<dt class='text-sm font-medium text-gray-500 dark:text-gray-400'>%s</dt>" +
            "<dd class='mt-2 text-3xl font-bold text-gray-900 dark:text-white'>%s</dd>" +
            "</dl>" +
            "<p class='mt-2 inline-flex items-center text-sm %s'>%s%s</p>" +
            "</div>",
            label, value, trendClasses, trendIcon, trend));
    }
}
