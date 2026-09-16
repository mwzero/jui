package it.jui.framework.apis;

import it.jui.framework.core.UIContext;

public class StatusElements extends BaseElements {

    public StatusElements(UIContext ctx) {
        super(ctx);
    }

    public void success(String message) { alert(message, "green", successIcon()); }
    public void info(String message) { alert(message, "blue", infoIcon()); }
    public void warning(String message) { alert(message, "yellow", warningIcon()); }
    public void error(String message) { alert(message, "red", errorIcon()); }

    private void alert(String message, String color, String icon) {
        ctx.addHtml("<div class='p-4 rounded-lg bg-" + color + "-50 dark:bg-" + color + "-900/30 border border-" + color + "-400 dark:border-" + color + "-700 text-" + color + "-800 dark:text-" + color + "-300 text-sm mb-4 flex items-start'>"
                + icon + "<div>" + escapeHtml(message) + "</div></div>");
    }

    public void spinner(String label) {
        ctx.addHtml("<div class='flex flex-col items-center justify-center mb-4'><div class='inline-flex items-center'>"
                + "<div class='animate-spin inline-block w-6 h-6 border-4 border-gray-300 dark:border-gray-600 border-t-indigo-600 dark:border-t-indigo-400 rounded-full'></div>"
                + "<span class='ml-3 text-gray-600 dark:text-gray-400 text-sm'>" + escapeHtml(label) + "</span></div></div>");
    }

    public int progressBar(String label, int percentage) {
        return progress(label, percentage, false);
    }

    public int progressBarAnimated(String label, int percentage) {
        return progress(label, percentage, true);
    }

    private int progress(String label, int percentage, boolean animated) {
        int value = Math.max(0, Math.min(100, percentage));
        String animation = animated ? " animate-pulse bg-gradient-to-r from-indigo-400 via-indigo-600 to-indigo-400" : " bg-indigo-600 dark:bg-indigo-400";
        ctx.addHtml("<div class='mb-4'><div class='flex justify-between items-center mb-1'>"
                + "<span class='text-sm font-medium text-gray-700 dark:text-gray-300'>" + escapeHtml(label) + "</span>"
                + "<span class='text-sm font-semibold text-gray-600 dark:text-gray-400'>" + value + "%</span></div>"
                + "<div class='w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2 overflow-hidden'>"
                + "<div class='h-2 rounded-full transition-all duration-300" + animation + "' style='width:" + value + "%'></div></div></div>");
        return value;
    }

    private String successIcon() {
        return "<span class='material-symbols-outlined text-lg mr-2'>check_circle</span>";
    }
    private String infoIcon() {
        return "<span class='material-symbols-outlined text-lg mr-2'>info</span>";
    }
    private String warningIcon() {
        return "<span class='material-symbols-outlined text-lg mr-2'>warning</span>";
    }
    private String errorIcon() {
        return "<span class='material-symbols-outlined text-lg mr-2'>error</span>";
    }
}
