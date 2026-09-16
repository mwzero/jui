package it.jui.data;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class DataFrames {

    private DataFrames() {}

    public static DataFrame readCsv(String path) throws IOException {
        try (Reader reader = Files.newBufferedReader(Path.of(path))) {
            return readCsv(reader);
        }
    }

    public static DataFrame readCsvString(String csv) throws IOException {
        return readCsv(new StringReader(csv));
    }

    public static DataFrame readCsv(Reader reader) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        try (CSVParser parser = format.parse(reader)) {
            List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
            List<List<Object>> rows = new ArrayList<>();
            for (CSVRecord record : parser) {
                List<Object> row = new ArrayList<>(headers.size());
                for (String header : headers) row.add(record.get(header));
                rows.add(row);
            }
            return new DataFrame(headers, rows);
        }
    }

    public static DataFrame readJson(String json) {
        JsonElement root = JsonParser.parseString(json);
        if (!root.isJsonArray()) throw new IllegalArgumentException("JSON DataFrame input must be an array");
        JsonArray array = root.getAsJsonArray();
        if (array.isEmpty()) return new DataFrame(List.of(), List.of());

        if (array.get(0).isJsonObject()) return objectArray(array);
        if (array.get(0).isJsonArray()) return arrayArray(array);
        return new DataFrame(List.of("Value"), primitiveRows(array));
    }

    public static DataFrame readSql(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            List<String> headers = new ArrayList<>(meta.getColumnCount());
            for (int i = 1; i <= meta.getColumnCount(); i++) headers.add(meta.getColumnLabel(i));

            List<List<Object>> rows = new ArrayList<>();
            while (rs.next()) {
                List<Object> row = new ArrayList<>(headers.size());
                for (int i = 1; i <= headers.size(); i++) row.add(rs.getObject(i));
                rows.add(row);
            }
            return new DataFrame(headers, rows);
        }
    }

    private static DataFrame objectArray(JsonArray array) {
        Set<String> headerSet = new LinkedHashSet<>();
        for (JsonElement element : array) {
            if (element.isJsonObject()) headerSet.addAll(element.getAsJsonObject().keySet());
        }
        List<String> headers = new ArrayList<>(headerSet);
        List<List<Object>> rows = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            List<Object> row = new ArrayList<>(headers.size());
            for (String header : headers) row.add(toJava(object.get(header)));
            rows.add(row);
        }
        return new DataFrame(headers, rows);
    }

    private static DataFrame arrayArray(JsonArray array) {
        int width = array.get(0).getAsJsonArray().size();
        List<String> headers = new ArrayList<>(width);
        for (int i = 0; i < width; i++) headers.add("Column" + (i + 1));
        List<List<Object>> rows = new ArrayList<>();
        for (JsonElement element : array) {
            List<Object> row = new ArrayList<>();
            for (JsonElement cell : element.getAsJsonArray()) row.add(toJava(cell));
            rows.add(row);
        }
        return new DataFrame(headers, rows);
    }

    private static List<List<Object>> primitiveRows(JsonArray array) {
        List<List<Object>> rows = new ArrayList<>();
        for (JsonElement element : array) rows.add(List.of(toJava(element)));
        return rows;
    }

    private static Object toJava(JsonElement value) {
        if (value == null || value.isJsonNull()) return null;
        if (value.isJsonPrimitive()) {
            var primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            if (primitive.isNumber()) return primitive.getAsNumber();
            return primitive.getAsString();
        }
        return value.toString();
    }
}
