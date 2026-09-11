package it.jui.framework.apis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.jui.framework.core.UIContext;

/**
 * High-semantic-density form API. JUI infers fields from Java types so callers
 * can describe common forms with a single method call.
 */
public class FormElements extends BaseElements {

    public FormElements(UIContext ctx) {
        super(ctx);
    }

    /**
     * Renders a create form. Records are the preferred model; bean-style POJOs
     * with a no-arg constructor plus public getters/setters are also supported.
     *
     * @return a value only on the render caused by a successful Save click
     */
    public <T> Optional<T> form(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("form type must not be null");
        return renderForm(type, null);
    }

    /**
     * Renders an edit form initialized from an existing record or bean-style POJO.
     * Records produce a new instance on Save. POJOs are updated through setters.
     *
     * @return the updated value only on the render caused by a successful Save click
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> form(T value) {
        if (value == null) throw new IllegalArgumentException("form value must not be null");
        return renderForm((Class<T>) value.getClass(), value);
    }

    private <T> Optional<T> renderForm(Class<T> type, T initialValue) {
        String formKey = "form:" + type.getName();
        List<FieldSpec> fields = fieldsFor(type, initialValue);

        ctx.addHtml("<div class='mb-6 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 shadow-sm p-4' data-jui='form'>");
        for (FieldSpec field : fields) {
            renderField(formKey, field);
        }

        String submitId = ctx.getNextWidgetId(formKey + ":submit");
        boolean submitted = ctx.consumeBoolean(submitId);
        ctx.addHtml(String.format(
                "<button type='button' onclick=\"sendUpdate('%s', true)\" " +
                "class='mt-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white rounded-md transition' data-jui='form-submit'>Save</button>",
                submitId));
        ctx.addHtml("</div>");

        if (!submitted) return Optional.empty();

        try {
            T result = type.isRecord()
                    ? buildRecord(type, fields, formKey)
                    : buildPojo(type, initialValue, fields, formKey);
            return Optional.of(result);
        } catch (ReflectiveOperationException | RuntimeException e) {
            ctx.error("Cannot create " + type.getSimpleName() + ": " + rootMessage(e));
            return Optional.empty();
        }
    }

    private <T> List<FieldSpec> fieldsFor(Class<T> type, T initialValue) {
        if (type.isRecord()) {
            List<FieldSpec> result = new ArrayList<>();
            for (RecordComponent component : type.getRecordComponents()) {
                ensureSupported(component.getType(), component.getName());
                Object defaultValue = defaultValue(component.getType());
                if (initialValue != null) {
                    try {
                        defaultValue = component.getAccessor().invoke(initialValue);
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalArgumentException("Cannot read form field '" + component.getName() + "'", e);
                    }
                }
                result.add(new FieldSpec(component.getName(), component.getType(), defaultValue, null));
            }
            return result;
        }

        Map<String, Method> getters = new LinkedHashMap<>();
        Map<String, Method> setters = new LinkedHashMap<>();

        for (Method method : type.getMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) continue;

            String getterProperty = getterProperty(method);
            if (getterProperty != null) getters.put(getterProperty, method);

            String setterProperty = setterProperty(method);
            if (setterProperty != null) setters.put(setterProperty, method);
        }

        List<String> names = getters.keySet().stream()
                .filter(setters::containsKey)
                .sorted()
                .toList();

        if (names.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unsupported form type " + type.getName() + ": use a record or a bean with public getters/setters");
        }

        ensureNoArgConstructor(type);

        List<FieldSpec> result = new ArrayList<>();
        for (String name : names) {
            Method getter = getters.get(name);
            Method setter = setters.get(name);
            Class<?> fieldType = setter.getParameterTypes()[0];
            if (!wrap(fieldType).isAssignableFrom(wrap(getter.getReturnType()))) {
                throw new IllegalArgumentException("Getter/setter type mismatch for form field '" + name + "'");
            }
            ensureSupported(fieldType, name);

            Object defaultValue = defaultValue(fieldType);
            if (initialValue != null) {
                try {
                    defaultValue = getter.invoke(initialValue);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalArgumentException("Cannot read form field '" + name + "'", e);
                }
            }
            result.add(new FieldSpec(name, fieldType, defaultValue, setter));
        }
        return result;
    }

    private void renderField(String formKey, FieldSpec field) {
        String id = ctx.getNextWidgetId(formKey + ":" + field.name());
        Object value = ctx.getValue(id, normalizeDefault(field.type(), field.defaultValue()));
        String label = humanize(field.name());
        Class<?> type = wrap(field.type());

        if (type == Boolean.class) {
            boolean checked = asBoolean(value);
            ctx.addHtml(String.format(
                    "<div class='mb-4 flex items-center'>" +
                    "<input id='%s' type='checkbox' %s onchange=\"sendUpdate('%s', this.checked)\" " +
                    "class='h-4 w-4 text-indigo-600 border-gray-300 dark:border-gray-600 dark:bg-gray-700 rounded'>" +
                    "<label for='%s' class='ml-2 text-sm text-gray-900 dark:text-gray-300'>%s</label></div>",
                    id, checked ? "checked" : "", id, id, escapeHtml(label)));
            return;
        }

        if (type == LocalDate.class) {
            ctx.addHtml(inputHtml(id, label, "date", stringValue(value), "this.value"));
            return;
        }

        if (Number.class.isAssignableFrom(type)) {
            ctx.addHtml(inputHtml(id, label, "number", stringValue(value), "this.value"));
            return;
        }

        if (type.isEnum()) {
            String current = stringValue(value);
            StringBuilder options = new StringBuilder();
            for (Object constant : type.getEnumConstants()) {
                String enumValue = String.valueOf(constant);
                options.append(String.format("<option value='%s' %s>%s</option>",
                        escapeHtml(enumValue), enumValue.equals(current) ? "selected" : "", escapeHtml(enumValue)));
            }
            ctx.addHtml(String.format(
                    "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
                    "<select onchange=\"sendUpdate('%s', this.value)\" " +
                    "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</select></div>",
                    escapeHtml(label), id, options));
            return;
        }

        ctx.addHtml(inputHtml(id, label, "text", stringValue(value), "this.value"));
    }

    private String inputHtml(String id, String label, String inputType, String value, String jsValue) {
        return String.format(
                "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
                "<input type='%s' value='%s' onchange=\"sendUpdate('%s', %s)\" " +
                "class='mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500' /></div>",
                escapeHtml(label), inputType, escapeHtml(value), id, jsValue);
    }

    private <T> T buildRecord(Class<T> type, List<FieldSpec> fields, String formKey)
            throws ReflectiveOperationException {
        RecordComponent[] components = type.getRecordComponents();
        Class<?>[] parameterTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new);
        Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
        if (!constructor.canAccess(null)) constructor.setAccessible(true);

        Object[] values = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            FieldSpec field = fields.get(i);
            values[i] = convertedState(formKey, field);
        }
        return constructor.newInstance(values);
    }

    private <T> T buildPojo(Class<T> type, T initialValue, List<FieldSpec> fields, String formKey)
            throws ReflectiveOperationException {
        T target = initialValue;
        if (target == null) {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) constructor.setAccessible(true);
            target = constructor.newInstance();
        }

        for (FieldSpec field : fields) {
            field.setter().invoke(target, convertedState(formKey, field));
        }
        return target;
    }

    private Object convertedState(String formKey, FieldSpec field) {
        String id = ctx.getNextWidgetId(formKey + ":" + field.name());
        Object raw = ctx.getValue(id, normalizeDefault(field.type(), field.defaultValue()));
        return convert(raw, field.type(), field.name());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object convert(Object raw, Class<?> targetType, String fieldName) {
        Class<?> type = wrap(targetType);
        try {
            if (type == String.class) return raw == null ? "" : String.valueOf(raw);
            if (type == Boolean.class) return asBoolean(raw);
            if (type == Integer.class) return raw instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(raw));
            if (type == Long.class) return raw instanceof Number n ? n.longValue() : Long.parseLong(String.valueOf(raw));
            if (type == Double.class) return raw instanceof Number n ? n.doubleValue() : Double.parseDouble(String.valueOf(raw));
            if (type == Float.class) return raw instanceof Number n ? n.floatValue() : Float.parseFloat(String.valueOf(raw));
            if (type == Short.class) return raw instanceof Number n ? n.shortValue() : Short.parseShort(String.valueOf(raw));
            if (type == Byte.class) return raw instanceof Number n ? n.byteValue() : Byte.parseByte(String.valueOf(raw));
            if (type == LocalDate.class) return LocalDate.parse(String.valueOf(raw));
            if (type.isEnum()) return Enum.valueOf((Class<? extends Enum>) type, String.valueOf(raw));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid value for field '" + fieldName + "'", e);
        }
        throw new IllegalArgumentException("Unsupported form field type " + targetType.getName());
    }

    private Object normalizeDefault(Class<?> fieldType, Object value) {
        if (value != null) {
            if (wrap(fieldType).isEnum()) return String.valueOf(value);
            if (wrap(fieldType) == LocalDate.class) return String.valueOf(value);
            return value;
        }
        return defaultValue(fieldType);
    }

    private Object defaultValue(Class<?> type) {
        Class<?> wrapped = wrap(type);
        if (wrapped == String.class) return "";
        if (wrapped == Boolean.class) return false;
        if (wrapped == Integer.class || wrapped == Short.class || wrapped == Byte.class) return 0;
        if (wrapped == Long.class) return 0L;
        if (wrapped == Double.class) return 0.0d;
        if (wrapped == Float.class) return 0.0f;
        if (wrapped == LocalDate.class) return LocalDate.now().toString();
        if (wrapped.isEnum()) {
            Object[] values = wrapped.getEnumConstants();
            return values.length == 0 ? "" : String.valueOf(values[0]);
        }
        return "";
    }

    private void ensureSupported(Class<?> type, String fieldName) {
        Class<?> wrapped = wrap(type);
        boolean supported = wrapped == String.class
                || wrapped == Boolean.class
                || Number.class.isAssignableFrom(wrapped)
                || wrapped == LocalDate.class
                || wrapped.isEnum();
        if (!supported) {
            throw new IllegalArgumentException(
                    "Unsupported form field '" + fieldName + "' of type " + type.getName());
        }
    }

    private <T> void ensureNoArgConstructor(Class<T> type) {
        try {
            type.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "POJO form type " + type.getName() + " requires a no-arg constructor", e);
        }
    }

    private String getterProperty(Method method) {
        if (method.getParameterCount() != 0 || method.getReturnType() == Void.TYPE || method.getDeclaringClass() == Object.class) {
            return null;
        }
        String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) return decapitalize(name.substring(3));
        if (name.startsWith("is") && name.length() > 2
                && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
            return decapitalize(name.substring(2));
        }
        return null;
    }

    private String setterProperty(Method method) {
        String name = method.getName();
        if (name.startsWith("set") && name.length() > 3 && method.getParameterCount() == 1
                && method.getReturnType() == Void.TYPE) {
            return decapitalize(name.substring(3));
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

    private boolean asBoolean(Object value) {
        return value instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(value));
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String rootMessage(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null) current = current.getCause();
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == boolean.class) return Boolean.class;
        if (type == char.class) return Character.class;
        return type;
    }

    private record FieldSpec(String name, Class<?> type, Object defaultValue, Method setter) {}
}
