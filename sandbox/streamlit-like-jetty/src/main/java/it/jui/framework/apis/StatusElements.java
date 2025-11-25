package it.jui.framework.apis;

import it.jui.framework.core.UIContext;

public class StatusElements extends BaseElements{

    public StatusElements(UIContext ctx) {
        super(ctx);
    }

    public void success(String message) {
        ctx.addHtml("<div class='p-4 rounded-lg bg-green-50 dark:bg-green-900/30 border border-green-400 dark:border-green-700 text-green-800 dark:text-green-300 text-sm mb-4 flex items-start'>" +
                "<svg class='w-5 h-5 mr-3 flex-shrink-0 mt-0.5' fill='currentColor' viewBox='0 0 20 20'><path fill-rule='evenodd' d='M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z' clip-rule='evenodd'/></path></svg>" +
                "<div>" + message + "</div></div>");
    }

    public void info(String message) {
        ctx.addHtml("<div class='p-4 rounded-lg bg-blue-50 dark:bg-blue-900/30 border border-blue-400 dark:border-blue-700 text-blue-800 dark:text-blue-300 text-sm mb-4 flex items-start'>" +
                "<svg class='w-5 h-5 mr-3 flex-shrink-0 mt-0.5' fill='currentColor' viewBox='0 0 20 20'><path fill-rule='evenodd' d='M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z' clip-rule='evenodd'/></path></svg>" +
                "<div>" + message + "</div></div>");
    }

    public void warning(String message) {
        ctx.addHtml("<div class='p-4 rounded-lg bg-yellow-50 dark:bg-yellow-900/30 border border-yellow-400 dark:border-yellow-700 text-yellow-800 dark:text-yellow-300 text-sm mb-4 flex items-start'>" +
                "<svg class='w-5 h-5 mr-3 flex-shrink-0 mt-0.5' fill='currentColor' viewBox='0 0 20 20'><path fill-rule='evenodd' d='M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z' clip-rule='evenodd'/></path></svg>" +
                "<div>" + message + "</div></div>");
    }

    public void error(String message) {
        ctx.addHtml("<div class='p-4 rounded-lg bg-red-50 dark:bg-red-900/30 border border-red-400 dark:border-red-700 text-red-800 dark:text-red-300 text-sm mb-4 flex items-start'>" +
                "<svg class='w-5 h-5 mr-3 flex-shrink-0 mt-0.5' fill='currentColor' viewBox='0 0 20 20'><path fill-rule='evenodd' d='M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z' clip-rule='evenodd'/></path></svg>" +
                "<div>" + message + "</div></div>");
    }

    public void spinner(String label) {

        ctx.addHtml("<div class='flex flex-col items-center justify-center mb-4'>" +
                "<div class='inline-flex items-center'>" +
                "<div class='animate-spin inline-block w-6 h-6 border-4 border-gray-300 dark:border-gray-600 border-t-indigo-600 dark:border-t-indigo-400 rounded-full'></div>" +
                "<span class='ml-3 text-gray-600 dark:text-gray-400 text-sm'>" + label + "</span>" +
                "</div></div>");
    }

    public int progressBar(String label, int percentage) {
        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;

        ctx.getNextWidgetId();
        int value = ctx.getValue(label, percentage);
        ctx.addHtml("<div class='mb-4'>" +
                "<div class='flex justify-between items-center mb-1'>" +
                "<span class='text-sm font-medium text-gray-700 dark:text-gray-300'>" + label + "</span>" +
                "<span class='text-sm font-semibold text-gray-600 dark:text-gray-400'>" + percentage + "%</span>" +
                "</div>" +
                "<div class='w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2'>" +
                "<div class='bg-indigo-600 dark:bg-indigo-400 h-2 rounded-full transition-all duration-300' style='width: " + percentage + "%'></div>" +
                "</div></div>");

        return value;
    }

    public int progressBarAnimated(String label, int percentage) {
        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;
        
        ctx.getNextWidgetId();
        int value = ctx.getValue(label, percentage);
        
        ctx.addHtml("<div class='mb-4'>" +
                "<div class='flex justify-between items-center mb-1'>" +
                "<span class='text-sm font-medium text-gray-700 dark:text-gray-300'>" + label + "</span>" +
                "<span class='text-sm font-semibold text-gray-600 dark:text-gray-400'>" + percentage + "%</span>" +
                "</div>" +
                "<div class='w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2 overflow-hidden'>" +
                "<div class='bg-gradient-to-r from-indigo-400 via-indigo-600 to-indigo-400 h-2 rounded-full transition-all duration-300 animate-pulse' style='width: " + percentage + "%'></div>" +
                "</div></div>");

        return value;
    }

}
