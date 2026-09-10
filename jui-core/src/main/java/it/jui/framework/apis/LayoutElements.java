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
            escapeHtml(title), escapeHtml(body)));
    }

    /**
     * Renders a metric using a compact LLM-friendly API.
     */
    public void metric(String label, Object value) {
        renderMetric(label, value, null, null);
    }

    /**
     * Renders a metric and infers trend direction from a leading + or - sign.
     * Trends without a sign are rendered neutrally.
     */
    public void metric(String label, Object value, String trend) {
        renderMetric(label, value, trend, inferTrendDirection(trend));
    }

    /**
     * Legacy explicit API retained for compatibility. New code should prefer
     * {@link #metric(String, Object)} or {@link #metric(String, Object, String)}.
     */
    public void metricCard(String label, String value, String trend, boolean trendIsPositive) {
        renderMetric(label, value, trend, trendIsPositive ? TrendDirection.POSITIVE : TrendDirection.NEGATIVE);
    }

    private void renderMetric(String label, Object value, String trend, TrendDirection direction) {
        String trendHtml = "";
        if (trend != null && !trend.isBlank()) {
            String classes = switch (direction == null ? TrendDirection.NEUTRAL : direction) {
                case POSITIVE -> "text-green-600 dark:text-green-400";
                case NEGATIVE -> "text-red-600 dark:text-red-400";
                case NEUTRAL -> "text-gray-500 dark:text-gray-400";
            };
            String icon = switch (direction == null ? TrendDirection.NEUTRAL : direction) {
                case POSITIVE -> "<span class='text-xs mr-1' aria-hidden='true'>▲</span>";
                case NEGATIVE -> "<span class='text-xs mr-1' aria-hidden='true'>▼</span>";
                case NEUTRAL -> "";
            };
            trendHtml = String.format(
                    "<p class='mt-2 inline-flex items-center text-sm %s' data-jui='metric-trend'>%s%s</p>",
                    classes, icon, escapeHtml(trend));
        }

        ctx.addHtml(String.format(
            "<div class='p-4 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-sm' data-jui='metric'>" +
            "<dl>" +
            "<dt class='text-sm font-medium text-gray-500 dark:text-gray-400'>%s</dt>" +
            "<dd class='mt-2 text-3xl font-bold text-gray-900 dark:text-white'>%s</dd>" +
            "</dl>" +
            "%s" +
            "</div>",
            escapeHtml(label), escapeHtml(String.valueOf(value == null ? "" : value)), trendHtml));
    }

    private TrendDirection inferTrendDirection(String trend) {
        if (trend == null) return TrendDirection.NEUTRAL;
        String normalized = trend.trim();
        if (normalized.startsWith("+")) return TrendDirection.POSITIVE;
        if (normalized.startsWith("-")) return TrendDirection.NEGATIVE;
        return TrendDirection.NEUTRAL;
    }

    private enum TrendDirection {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }
}
