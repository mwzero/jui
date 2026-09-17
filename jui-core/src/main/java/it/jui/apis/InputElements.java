package it.jui.apis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.jui.UIContext;
import it.jui.input.UploadedFile;

public class InputElements extends BaseElements {

    public InputElements(UIContext ctx) {
        super(ctx);
    }

    public String radio(String label, List<String> options, String defaultOption) {
        if (options == null || options.isEmpty()) return "";
        String id = ctx.getNextWidgetId("radio:" + label);
        String initial = defaultOption != null ? defaultOption : options.get(0);
        String selected = ctx.getValue(id, initial);

        StringBuilder html = new StringBuilder("<fieldset class='mb-4'><legend class='block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2'>")
                .append(escapeHtml(label)).append("</legend><div class='space-y-2'>");
        for (String option : options) {
            String safe = escapeHtml(option);
            html.append("<label class='flex items-center gap-2 text-sm text-gray-800 dark:text-gray-200'>")
                    .append("<input type='radio' name='").append(id).append("' value='").append(safe).append("' ")
                    .append(option.equals(selected) ? "checked " : "")
                    .append("onchange=\"sendUpdate('").append(id).append("', this.value)\" ")
                    .append("class='text-indigo-600 border-gray-300 dark:border-gray-600' />")
                    .append(safe).append("</label>");
        }
        html.append("</div></fieldset>");
        ctx.addHtml(html.toString());
        return selected;
    }

    public List<String> multiCheckbox(String label, List<String> options, List<String> defaults) {
        String id = ctx.getNextWidgetId("multi-checkbox:" + label);
        Object raw = ctx.getRawValue(id);
        List<String> selected = raw instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : defaults == null ? List.of() : List.copyOf(defaults);

        if (raw == null) ctx.setValue(id, new ArrayList<>(selected));

        StringBuilder html = new StringBuilder("<fieldset class='mb-4'><legend class='block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2'>")
                .append(escapeHtml(label)).append("</legend><div class='space-y-2'>");
        if (options != null) {
            for (String option : options) {
                String safe = escapeHtml(option);
                html.append("<label class='flex items-center gap-2 text-sm text-gray-800 dark:text-gray-200'>")
                        .append("<input type='checkbox' data-jui-multi='").append(id).append("' value='").append(safe).append("' ")
                        .append(selected.contains(option) ? "checked " : "")
                        .append("onchange=\"juiMultiCheck('").append(id).append("')\" ")
                        .append("class='h-4 w-4 text-indigo-600 border-gray-300 dark:border-gray-600 rounded' />")
                        .append(safe).append("</label>");
            }
        }
        html.append("</div></fieldset>");
        ctx.addHtml(html.toString());
        return selected;
    }

    public String select(String label, List<String> options, String defaultOption) {
        if (options == null || options.isEmpty()) return "";
        String id = ctx.getNextWidgetId("select:" + label);
        String selected = ctx.getValue(id, defaultOption != null ? defaultOption : options.get(0));
        StringBuilder opts = new StringBuilder();
        for (String option : options) {
            String safe = escapeHtml(option);
            opts.append("<option value='").append(safe).append("' ")
                    .append(option.equals(selected) ? "selected" : "")
                    .append(">").append(safe).append("</option>");
        }
        ctx.addHtml("<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>"
                + escapeHtml(label) + "</label><select onchange=\"sendUpdate('" + id + "', this.value)\" "
                + "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>"
                + opts + "</select></div>");
        return selected;
    }

    public String colorPicker(String label, String defaultColor) {
        String id = ctx.getNextWidgetId("color:" + label);
        String selected = ctx.getValue(id, defaultColor == null ? "#4f46e5" : defaultColor);
        ctx.addHtml("<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1'>"
                + escapeHtml(label) + "</label><div class='flex items-center gap-3'>"
                + "<input type='color' value='" + escapeHtml(selected) + "' onchange=\"sendUpdate('" + id + "', this.value)\" "
                + "class='h-10 w-16 rounded border border-gray-300 dark:border-gray-600 bg-transparent'/>"
                + "<span class='text-sm text-gray-500 dark:text-gray-400'>" + escapeHtml(selected) + "</span></div></div>");
        return selected;
    }

    public String dateInput(String label, String defaultValue) {
        String id = ctx.getNextWidgetId("date:" + label);
        String value = ctx.getValue(id, defaultValue == null ? "" : defaultValue);
        ctx.addHtml("<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>"
                + escapeHtml(label) + "</label><input type='date' value='" + escapeHtml(value)
                + "' onchange=\"sendUpdate('" + id + "', this.value)\" class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'/></div>");
        return value;
    }

    public Optional<UploadedFile> fileUploader(String label) {
        String id = ctx.getNextWidgetId("file:" + label);
        Object raw = ctx.getRawValue(id);
        UploadedFile file = toUploadedFile(raw);

        ctx.addHtml("<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1'>"
                + escapeHtml(label) + "</label><input type='file' onchange=\"juiUploadFile('" + id + "', this)\" "
                + "class='block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 dark:file:bg-indigo-900 dark:file:text-indigo-300 hover:file:bg-indigo-100'/>"
                + (file == null ? "" : "<p class='mt-1 text-xs text-gray-500 dark:text-gray-400'>Uploaded: "
                        + escapeHtml(file.name()) + " (" + file.size() + " bytes)</p>") + "</div>");
        return Optional.ofNullable(file);
    }

    private UploadedFile toUploadedFile(Object raw) {
        if (raw instanceof UploadedFile file) return file;
        if (!(raw instanceof Map<?, ?> map)) return null;
        Map<String, Object> values = new LinkedHashMap<>();
        map.forEach((key, value) -> values.put(String.valueOf(key), value));
        String name = String.valueOf(values.getOrDefault("name", ""));
        String type = String.valueOf(values.getOrDefault("contentType", values.getOrDefault("type", "application/octet-stream")));
        Object sizeValue = values.get("size");
        long size = sizeValue instanceof Number n ? n.longValue() : 0L;
        String base64 = String.valueOf(values.getOrDefault("base64", ""));
        return new UploadedFile(name, type, size, base64);
    }
}
