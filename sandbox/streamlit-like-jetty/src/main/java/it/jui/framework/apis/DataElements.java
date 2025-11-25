package it.jui.framework.apis;

import java.util.List;

import it.jui.framework.core.UIContext;

public class DataElements extends BaseElements {

    public DataElements(UIContext ctx) {
        super(ctx);
    }

    public void table(String title, List<String> headers, List<List<String>> rows) {
        StringBuilder head = new StringBuilder();
        for (String h : headers) {
            head.append(String.format(
                "<th scope='col' class='px-6 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-300'>%s</th>",
                h));
        }

        StringBuilder body = new StringBuilder();
        for (List<String> row : rows) {
            body.append("<tr class='border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800/60'>");
            for (String cell : row) {
                body.append(String.format(
                    "<td class='px-6 py-4 text-sm text-gray-700 dark:text-gray-200'>%s</td>",
                    cell));
            }
            body.append("</tr>");
        }

        ctx.addHtml(String.format(
            "<div class='mb-6 overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm'>" +
            "<div class='px-4 py-3 bg-gray-50 dark:bg-gray-900/40 border-b border-gray-200 dark:border-gray-700'>" +
            "<h3 class='text-sm font-semibold text-gray-800 dark:text-gray-100'>%s</h3>" +
            "</div>" +
            "<div class='overflow-x-auto'>" +
            "<table class='min-w-full divide-y divide-gray-200 dark:divide-gray-700'>" +
            "<thead class='bg-gray-50 dark:bg-gray-900/60'><tr>%s</tr></thead>" +
            "<tbody class='bg-white dark:bg-gray-800'>%s</tbody>" +
            "</table>" +
            "</div>" +
            "</div>",
            title, head.toString(), body.toString()));
    }

    public void badge(String label, String tone) {
        String toneKey = tone != null ? tone.toLowerCase() : "";
        String toneClasses = switch (toneKey) {
            case "success" -> "bg-green-100 text-green-800 dark:bg-green-900/60 dark:text-green-200";
            case "warning" -> "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/60 dark:text-yellow-200";
            case "danger" -> "bg-red-100 text-red-800 dark:bg-red-900/60 dark:text-red-200";
            case "info" -> "bg-blue-100 text-blue-800 dark:bg-blue-900/60 dark:text-blue-200";
            default -> "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200";
        };

        ctx.addHtml(String.format(
            "<span class='inline-flex items-center px-3 py-1 text-xs font-medium rounded-full %s'>%s</span>",
            toneClasses, label));
    }
}
