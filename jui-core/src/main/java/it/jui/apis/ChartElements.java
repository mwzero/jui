package it.jui.apis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import it.jui.UIContext;

public class ChartElements extends BaseElements {

    private final Gson gson = new Gson();

    public ChartElements(UIContext ctx) {
        super(ctx);
    }

    public void lineChart(String title, List<? extends Number> values) {
        chart("line", title, numericLabels(values == null ? 0 : values.size()), Map.of("Value", values == null ? List.of() : values));
    }

    public void barChart(String title, List<? extends Number> values) {
        chart("bar", title, numericLabels(values == null ? 0 : values.size()), Map.of("Value", values == null ? List.of() : values));
    }

    public void lineChart(String title, List<String> labels, Map<String, ? extends List<?>> series) {
        chart("line", title, labels, series);
    }

    public void barChart(String title, List<String> labels, Map<String, ? extends List<?>> series) {
        chart("bar", title, labels, series);
    }

    /**
     * Record/POJO friendly chart API. Example:
     * ui.lineChart("Revenue", months, "month", "revenue", "cost");
     */
    public <T> void lineChart(String title, List<T> items, String xField, String... yFields) {
        chartFromObjects("line", title, items, xField, yFields);
    }

    public <T> void barChart(String title, List<T> items, String xField, String... yFields) {
        chartFromObjects("bar", title, items, xField, yFields);
    }

    private <T> void chartFromObjects(String type, String title, List<T> items, String xField, String... yFields) {
        List<String> labels = new ArrayList<>();
        Map<String, List<Object>> series = new LinkedHashMap<>();
        if (yFields != null) {
            for (String field : yFields) series.put(field, new ArrayList<>());
        }

        if (items != null) {
            for (T item : items) {
                labels.add(String.valueOf(readProperty(item, xField)));
                if (yFields != null) {
                    for (String field : yFields) series.get(field).add(readProperty(item, field));
                }
            }
        }
        chart(type, title, labels, series);
    }

    private void chart(String type, String title, List<String> labels, Map<String, ? extends List<?>> values) {
        ctx.addHtmlDependency("apexcharts", "<script src=\"https://cdn.jsdelivr.net/npm/apexcharts@3.54.1/dist/apexcharts.min.js\"></script>");

        String id = ctx.getNextWidgetId("chart:" + type + ":" + title);
        List<Map<String, Object>> series = new ArrayList<>();
        if (values != null) {
            values.forEach((name, data) -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", name);
                item.put("data", data == null ? List.of() : data);
                series.add(item);
            });
        }

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("chart", Map.of("type", type, "height", 320, "toolbar", Map.of("show", true)));
        options.put("series", series);
        options.put("xaxis", Map.of("categories", labels == null ? List.of() : labels));
        options.put("dataLabels", Map.of("enabled", false));
        options.put("theme", Map.of("mode", "light"));
        if ("line".equals(type)) options.put("stroke", Map.of("curve", "smooth", "width", 3));

        ctx.addHtml("<section class='mb-6 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-sm overflow-hidden'>"
                + "<h3 class='px-4 pt-4 text-sm font-semibold text-gray-800 dark:text-gray-100'>" + escapeHtml(title) + "</h3>"
                + "<div id='" + id + "' class='px-2 pb-2'></div>"
                + "<script>(function(){var el=document.getElementById('" + id + "');"
                + "if(el && window.ApexCharts){new ApexCharts(el," + gson.toJson(options) + ").render();}})();</script></section>");
    }

    private List<String> numericLabels(int size) {
        List<String> labels = new ArrayList<>(size);
        for (int i = 0; i < size; i++) labels.add(String.valueOf(i + 1));
        return labels;
    }

    private Object readProperty(Object target, String name) {
        if (target == null) return null;
        if (target instanceof Map<?, ?> map) return map.get(name);
        Class<?> type = target.getClass();
        try {
            if (type.isRecord()) {
                for (RecordComponent component : type.getRecordComponents()) {
                    if (component.getName().equals(name)) return component.getAccessor().invoke(target);
                }
            }

            String suffix = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            for (String candidate : List.of(name, "get" + suffix, "is" + suffix)) {
                try {
                    Method method = type.getMethod(candidate);
                    if (method.getParameterCount() == 0) return method.invoke(target);
                } catch (NoSuchMethodException ignored) {
                }
            }

            Field field = type.getDeclaredField(name);
            if (!field.canAccess(target)) field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new IllegalArgumentException("Cannot read chart property '" + name + "' from " + type.getName(), e);
        }
    }
}
