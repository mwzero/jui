package it.jui.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DataFrame {

    private final List<String> headers;
    private final List<List<Object>> rows;

    public DataFrame(List<String> headers, List<? extends List<?>> rows) {
        this.headers = List.copyOf(headers == null ? List.of() : headers);
        List<List<Object>> copy = new ArrayList<>();
        if (rows != null) {
            for (List<?> row : rows) copy.add(Collections.unmodifiableList(new ArrayList<>(row)));
        }
        this.rows = Collections.unmodifiableList(copy);
    }

    public List<String> headers() {
        return headers;
    }

    public List<List<Object>> rows() {
        return rows;
    }

    public int rowCount() {
        return rows.size();
    }

    public int columnCount() {
        return headers.size();
    }

    public Object get(int row, int column) {
        return rows.get(row).get(column);
    }

    public DataFrame limit(int limit) {
        int end = Math.max(0, Math.min(limit, rows.size()));
        return new DataFrame(headers, rows.subList(0, end));
    }

    public DataFrame select(String... columns) {
        return select(List.of(columns));
    }

    public DataFrame select(List<String> columns) {
        List<Integer> indexes = new ArrayList<>();
        for (String column : columns) {
            int index = headers.indexOf(column);
            if (index < 0) throw new IllegalArgumentException("Unknown column: " + column);
            indexes.add(index);
        }

        List<List<Object>> selectedRows = new ArrayList<>();
        for (List<Object> row : rows) {
            List<Object> selected = new ArrayList<>(indexes.size());
            for (int index : indexes) selected.add(row.get(index));
            selectedRows.add(selected);
        }
        return new DataFrame(columns, selectedRows);
    }

    /** Convenient representation for ui.table(...). */
    public List<Map<String, Object>> toMaps() {
        List<Map<String, Object>> result = new ArrayList<>(rows.size());
        for (List<Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                item.put(headers.get(i), i < row.size() ? row.get(i) : null);
            }
            result.add(item);
        }
        return result;
    }
}
