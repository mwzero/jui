package it.jui.framework.apis;

import it.jui.framework.core.UIContext;

public class LayoutElements extends BaseElements {

    public LayoutElements(UIContext ctx) {
        super(ctx);
    }

    public void container(Runnable content) {
        String body = ctx.capture(content);
        ctx.addHtml("<div class='mb-4'>" + body + "</div>");
    }

    /**
     * Renders equally sized responsive columns. Each lambda renders with the same
     * UIContext, so widget state and deterministic ids keep normal rerun semantics.
     */
    public void columns(Runnable... columns) {
        if (columns == null || columns.length == 0) return;
        int count = Math.min(columns.length, 6);
        String responsive = switch (count) {
            case 1 -> "md:grid-cols-1";
            case 2 -> "md:grid-cols-2";
            case 3 -> "md:grid-cols-3";
            case 4 -> "md:grid-cols-4";
            case 5 -> "md:grid-cols-5";
            default -> "md:grid-cols-6";
        };
        StringBuilder html = new StringBuilder("<div class='grid grid-cols-1 gap-4 ").append(responsive).append(" mb-5'>");
        for (Runnable renderer : columns) {
            html.append("<div class='min-w-0'>").append(ctx.capture(renderer)).append("</div>");
        }
        html.append("</div>");
        ctx.addHtml(html.toString());
    }

    public void expander(String title, Runnable content) {
        String body = ctx.capture(content);
        ctx.addHtml("<details class='mb-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800'>"
                + "<summary class='cursor-pointer select-none px-4 py-3 font-medium text-gray-800 dark:text-gray-100'>"
                + escapeHtml(title) + "</summary><div class='px-4 pb-4'>" + body + "</div></details>");
    }

    public void popover(String label, Runnable content) {
        String body = ctx.capture(content);
        ctx.addHtml("<details class='relative inline-block mb-4'><summary class='list-none cursor-pointer px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-sm text-gray-700 dark:text-gray-200'>"
                + escapeHtml(label) + "</summary><div class='absolute z-30 mt-2 w-72 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 p-4 shadow-xl'>"
                + body + "</div></details>");
    }

    public void dialog(String title, Runnable content) {
        String id = ctx.getNextWidgetId("dialog:" + title);
        String body = ctx.capture(content);
        ctx.addHtml("<div class='mb-4'><button type='button' onclick=\"document.getElementById('" + id + "').showModal()\" "
                + "class='px-4 py-2 rounded-md bg-indigo-600 hover:bg-indigo-700 text-white'>" + escapeHtml(title) + "</button>"
                + "<dialog id='" + id + "' class='w-[min(42rem,90vw)] rounded-xl bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 p-0 shadow-2xl backdrop:bg-black/50'>"
                + "<div class='flex items-center justify-between border-b border-gray-200 dark:border-gray-700 px-5 py-4'><h3 class='text-lg font-semibold'>"
                + escapeHtml(title) + "</h3><button type='button' onclick=\"document.getElementById('" + id + "').close()\" class='text-2xl leading-none text-gray-500'>&times;</button></div>"
                + "<div class='p-5'>" + body + "</div></dialog></div>");
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

    public void metric(String label, Object value) {
        renderMetric(label, value, null, null);
    }

    public void metric(String label, Object value, String trend) {
        renderMetric(label, value, trend, inferTrendDirection(trend));
    }

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
            "<dl><dt class='text-sm font-medium text-gray-500 dark:text-gray-400'>%s</dt>" +
            "<dd class='mt-2 text-3xl font-bold text-gray-900 dark:text-white'>%s</dd></dl>%s</div>",
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
        POSITIVE, NEGATIVE, NEUTRAL
    }
}
