package it.jui.framework.apis;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.jui.framework.core.UIContext;

public class DataElements extends BaseElements {

    public DataElements(UIContext ctx) {
        super(ctx);
    }

    /**
     * Renders a table by inferring columns from the Java values.
     * <p>
     * Records use their declared record-component order, POJOs use public bean-style
     * getters sorted by property name, maps use sorted keys, and scalar values are
     * rendered in a single {@code Value} column.
     */
    public <T> void table(List<T> items) {
        table(null, items);
    }

    /**
     * Same as {@link #table(List)} with an optional visible title.
     */
    public <T> void table(String title, List<T> items) {
        if (items == null || items.isEmpty()) {
            renderEmptyTable(title);
            return;
        }

        Object sample = items.stream().filter(item -> item != null).findFirst().orElse(null);
        if (sample == null) {
            renderScalarTable(title, items);
            return;
        }

        if (sample instanceof Map<?, ?>) {
            renderMapTable(title, items);
            return;
        }

        Class<?> type = sample.getClass();
        if (isScalarType(type)) {
            renderScalarTable(title, items);
            return;
        }

        if (type.isRecord()) {
            renderRecordTable(title, items, type);
            return;
        }

        List<PojoColumn> pojoColumns = pojoColumns(type);
        if (!pojoColumns.isEmpty()) {
            renderPojoTable(title, items, pojoColumns);
            return;
        }

        renderScalarTable(title, items);
    }

    /**
     * Low-level table API retained for explicit headers and rows.
     */
    public void table(String title, List<String> headers, List<List<String>> rows) {
        renderTable(title, headers, rows);
    }

    private <T> void renderRecordTable(String title, List<T> items, Class<?> type) {
        RecordComponent[] components = type.getRecordComponents();
        List<String> headers = new ArrayList<>(components.length);
        List<List<String>> rows = new ArrayList<>(items.size());

        for (RecordComponent component : components) {
            headers.add(humanize(component.getName()));
        }

        for (T item : items) {
            List<String> row = new ArrayList<>(components.length);
            for (RecordComponent component : components) {
                if (item == null) {
                    row.add("");
                    continue;
                }
                try {
                    row.add(stringValue(component.getAccessor().invoke(item)));
                } catch (ReflectiveOperationException e) {
                    throw new IllegalArgumentException(
                            "Cannot read table property '" + component.getName() + "'", e);
                }
            }
            rows.add(row);
        }

        renderTable(title, headers, rows);
    }

    private <T> void renderPojoTable(String title, List<T> items, List<PojoColumn> columns) {
        List<String> headers = columns.stream().map(column -> humanize(column.name())).toList();
        List<List<String>> rows = new ArrayList<>(items.size());

        for (T item : items) {
            List<String> row = new ArrayList<>(columns.size());
            for (PojoColumn column : columns) {
                if (item == null) {
                    row.add("");
                    continue;
                }
                try {
                    row.add(stringValue(column.getter().invoke(item)));
                } catch (ReflectiveOperationException e) {
                    throw new IllegalArgumentException(
                            "Cannot read table property '" + column.name() + "'", e);
                }
            }
            rows.add(row);
        }

        renderTable(title, headers, rows);
    }

    private <T> void renderMapTable(String title, List<T> items) {
        TreeMap<String, Object> keys = new TreeMap<>();
        for (T item : items) {
            if (item instanceof Map<?, ?> map) {
                for (Object key : map.keySet()) {
                    keys.putIfAbsent(String.valueOf(key), key);
                }
            }
        }

        if (keys.isEmpty()) {
            renderEmptyTable(title);
            return;
        }

        List<String> headers = keys.keySet().stream().map(this::humanize).toList();
        List<List<String>> rows = new ArrayList<>(items.size());

        for (T item : items) {
            List<String> row = new ArrayList<>(keys.size());
            for (Object key : keys.values()) {
                Object value = item instanceof Map<?, ?> map ? map.get(key) : null;
                row.add(stringValue(value));
            }
            rows.add(row);
        }

        renderTable(title, headers, rows);
    }

    private <T> void renderScalarTable(String title, List<T> items) {
        List<List<String>> rows = new ArrayList<>(items.size());
        for (T item : items) {
            rows.add(List.of(stringValue(item)));
        }
        renderTable(title, List.of("Value"), rows);
    }

    /**
     * Value-like JDK types must be classified before bean reflection. Otherwise
     * methods such as String.isEmpty() or LocalDate.isLeapYear() look like bean
     * getters and accidentally become table columns.
     */
    private boolean isScalarType(Class<?> type) {
        return CharSequence.class.isAssignableFrom(type)
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class
                || type.isEnum()
                || TemporalAccessor.class.isAssignableFrom(type);
    }

    private List<PojoColumn> pojoColumns(Class<?> type) {
        List<PojoColumn> columns = new ArrayList<>();
        for (Method method : type.getMethods()) {
            if (!Modifier.isPublic(method.getModifiers())
                    || Modifier.isStatic(method.getModifiers())
                    || method.getParameterCount() != 0
                    || method.getReturnType() == Void.TYPE
                    || method.getDeclaringClass() == Object.class) {
                continue;
            }

            String property = propertyName(method);
            if (property != null) {
                columns.add(new PojoColumn(property, method));
            }
        }
        columns.sort(Comparator.comparing(PojoColumn::name));
        return columns;
    }

    private String propertyName(Method method) {
        String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {
            return decapitalize(name.substring(3));
        }
        if (name.startsWith("is") && name.length() > 2
                && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
            return decapitalize(name.substring(2));
        }
        return null;
    }

    private String decapitalize(String value) {
        if (value.isEmpty()) return value;
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    private String humanize(String value) {
        if (value == null || value.isBlank()) return "";
        String spaced = value.replace('_', ' ').replaceAll("(?<=[a-z0-9])(?=[A-Z])", " ");
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void renderEmptyTable(String title) {
        String titleHtml = title == null || title.isBlank()
                ? ""
                : "<div class='px-4 py-3 bg-gray-50 dark:bg-gray-900/40 border-b border-gray-200 dark:border-gray-700'>"
                  + "<h3 class='text-sm font-semibold text-gray-800 dark:text-gray-100'>"
                  + escapeHtml(title) + "</h3></div>";

        ctx.addHtml("<div class='mb-6 overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm' data-jui='table'>"
                + titleHtml
                + "<div class='px-6 py-8 text-sm text-center text-gray-500 dark:text-gray-400' data-jui='table-empty'>No data</div>"
                + "</div>");
    }

    private void renderTable(String title, List<String> headers, List<List<String>> rows) {
        StringBuilder head = new StringBuilder();
        for (String h : headers) {
            head.append(String.format(
                "<th scope='col' class='px-6 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500 dark:text-gray-300'>%s</th>",
                escapeHtml(h)));
        }

        StringBuilder body = new StringBuilder();
        for (List<String> row : rows) {
            body.append("<tr class='border-b border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800/60'>");
            for (String cell : row) {
                body.append(String.format(
                    "<td class='px-6 py-4 text-sm text-gray-700 dark:text-gray-200'>%s</td>",
                    escapeHtml(cell)));
            }
            body.append("</tr>");
        }

        String titleHtml = title == null || title.isBlank()
                ? ""
                : "<div class='px-4 py-3 bg-gray-50 dark:bg-gray-900/40 border-b border-gray-200 dark:border-gray-700'>"
                  + "<h3 class='text-sm font-semibold text-gray-800 dark:text-gray-100'>"
                  + escapeHtml(title) + "</h3></div>";

        ctx.addHtml(String.format(
            "<div class='mb-6 overflow-hidden rounded-xl border border-gray-200 dark:border-gray-700 shadow-sm' data-jui='table'>" +
            "%s" +
            "<div class='overflow-x-auto'>" +
            "<table class='min-w-full divide-y divide-gray-200 dark:divide-gray-700'>" +
            "<thead class='bg-gray-50 dark:bg-gray-900/60'><tr>%s</tr></thead>" +
            "<tbody class='bg-white dark:bg-gray-800'>%s</tbody>" +
            "</table>" +
            "</div>" +
            "</div>",
            titleHtml, head.toString(), body.toString()));
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
            toneClasses, escapeHtml(label)));
    }

    private record PojoColumn(String name, Method getter) {}
}
