package it.jui.input;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** Server-owned validation rules for an input rendered in the current view. */
public final class WidgetSpec {
    public enum Kind { VALUE, ACTION, LOGOUT }
    private final Kind kind;
    private final Function<Object, Object> validator;

    private WidgetSpec(Kind kind, Function<Object, Object> validator) {
        this.kind = kind;
        this.validator = validator;
    }

    public Kind kind() { return kind; }
    public Object validate(Object value) {
        try {
            if (value == null) throw new IllegalArgumentException();
            return validator.apply(value);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid widget value", e);
        }
    }

    public static WidgetSpec text() { return value(WidgetSpec::string); }
    public static WidgetSpec bool() {
        return value(raw -> {
            if (!(raw instanceof Boolean)) throw new IllegalArgumentException();
            return raw;
        });
    }
    public static WidgetSpec action() { return action(Kind.ACTION); }
    public static WidgetSpec logout() { return action(Kind.LOGOUT); }
    private static WidgetSpec action(Kind kind) {
        return new WidgetSpec(kind, raw -> {
            if (!Boolean.TRUE.equals(raw)) throw new IllegalArgumentException();
            return true;
        });
    }
    public static WidgetSpec choice(Collection<String> options) {
        Set<String> allowed = Set.copyOf(options);
        return value(raw -> {
            String selected = string(raw);
            if (!allowed.contains(selected)) throw new IllegalArgumentException();
            return selected;
        });
    }
    public static WidgetSpec multiple(Collection<String> options) {
        Set<String> allowed = Set.copyOf(options);
        return value(raw -> {
            if (!(raw instanceof List<?> list)) throw new IllegalArgumentException();
            if (list.stream().anyMatch(item -> !(item instanceof String) || !allowed.contains(item)))
                throw new IllegalArgumentException();
            return List.copyOf(list);
        });
    }
    public static WidgetSpec integer(long min, long max) {
        return value(raw -> {
            long number = integerValue(raw);
            if (number < min || number > max) throw new IllegalArgumentException();
            return number;
        });
    }
    public static WidgetSpec date() {
        return value(raw -> {
            String date = string(raw);
            if (!date.isEmpty()) LocalDate.parse(date);
            return date;
        });
    }
    public static WidgetSpec color() {
        return value(raw -> {
            String color = string(raw);
            if (!color.matches("#[0-9a-fA-F]{6}")) throw new IllegalArgumentException();
            return color;
        });
    }
    public static WidgetSpec map() {
        return value(raw -> {
            if (!(raw instanceof Map<?, ?> map)) throw new IllegalArgumentException();
            double latitude = number(map.get("latitude"));
            double longitude = number(map.get("longitude"));
            long zoom = integerValue(map.get("zoom"));
            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180 || zoom < 0 || zoom > 19)
                throw new IllegalArgumentException();
            return Map.of("latitude", latitude, "longitude", longitude, "zoom", zoom);
        });
    }
    public static WidgetSpec upload() {
        return value(raw -> {
            if (!(raw instanceof Map<?, ?> map)) throw new IllegalArgumentException();
            String name = string(map.get("name"));
            String contentType = string(map.get("contentType"));
            String base64 = string(map.get("base64"));
            long size = integerValue(map.get("size"));
            int maximum = 5 * 1024 * 1024;
            if (size < 0 || size > maximum || base64.length() > 4 * ((maximum + 2) / 3))
                throw new IllegalArgumentException();
            byte[] decoded = Base64.getDecoder().decode(base64);
            if (decoded.length != size) throw new IllegalArgumentException();
            return Map.of("name", name, "contentType", contentType, "base64", base64, "size", size);
        });
    }
    private static WidgetSpec value(Function<Object, Object> validator) {
        return new WidgetSpec(Kind.VALUE, validator);
    }
    private static String string(Object raw) {
        if (!(raw instanceof String text)) throw new IllegalArgumentException();
        return text;
    }
    private static double number(Object raw) {
        if (!(raw instanceof Number number) || !Double.isFinite(number.doubleValue()))
            throw new IllegalArgumentException();
        return number.doubleValue();
    }
    private static long integerValue(Object raw) {
        if (!(raw instanceof Number number)) throw new IllegalArgumentException();
        return new java.math.BigDecimal(number.toString()).longValueExact();
    }
}
