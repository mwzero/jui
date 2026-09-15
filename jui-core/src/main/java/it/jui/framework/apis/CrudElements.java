package it.jui.framework.apis;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.jui.framework.core.UIContext;
import it.jui.framework.data.CrudRepository;

/**
 * High-semantic-density CRUD composition.
 * <p>
 * Mutable lists are supported as a zero-configuration convenience. Persistent
 * stores should implement {@link CrudRepository} so identity and storage remain
 * outside the UI layer.
 */
public class CrudElements extends BaseElements {

    public CrudElements(UIContext ctx) {
        super(ctx);
    }

    /**
     * Convenience CRUD for a mutable in-memory list. List position is used as
     * temporary identity and is intentionally limited to non-persistent scenarios.
     */
    public <T> void crud(Class<T> type, List<T> items) {
        crud(humanize(type.getSimpleName()), type, items);
    }

    public <T> void crud(String title, Class<T> type, List<T> items) {
        if (type == null) throw new IllegalArgumentException("crud type must not be null");
        if (items == null) throw new IllegalArgumentException("crud items must not be null");

        String crudKey = "crud:" + type.getName();
        String modeId = ctx.getNextWidgetId(crudKey + ":mode");
        String indexId = ctx.getNextWidgetId(crudKey + ":index");
        String createFormKey = crudKey + ":create";

        String mode = ctx.getValue(modeId, "list");

        if (consume(crudKey + ":new")) {
            ctx.clearForm(createFormKey, type);
            ctx.setValue(modeId, "create");
            ctx.removeValue(indexId);
            mode = "create";
        }

        for (int i = 0; i < items.size(); i++) {
            if (consume(crudKey + ":edit:" + i)) {
                String editFormKey = crudKey + ":edit:" + i;
                ctx.clearForm(editFormKey, type);
                ctx.setValue(modeId, "edit");
                ctx.setValue(indexId, i);
                mode = "edit";
            }
            if (consume(crudKey + ":delete:" + i)) {
                items.remove(i);
                ctx.setValue(modeId, "list");
                ctx.removeValue(indexId);
                mode = "list";
                break;
            }
        }

        if (consume(crudKey + ":cancel")) {
            if ("create".equals(mode)) {
                ctx.clearForm(createFormKey, type);
            } else if ("edit".equals(mode)) {
                Integer index = numericIndex(ctx.getValue(indexId, null));
                if (index != null) ctx.clearForm(crudKey + ":edit:" + index, type);
            }
            ctx.setValue(modeId, "list");
            ctx.removeValue(indexId);
            mode = "list";
        }

        ctx.header(title);

        if ("create".equals(mode)) {
            renderModeTitle("New " + humanize(type.getSimpleName()));
            Optional<T> created = ctx.form(createFormKey, type);
            renderCancel(crudKey);
            if (created.isPresent()) {
                items.add(created.get());
                ctx.clearForm(createFormKey, type);
                ctx.setValue(modeId, "list");
                ctx.success(humanize(type.getSimpleName()) + " saved");
            }
            return;
        }

        if ("edit".equals(mode)) {
            Integer index = numericIndex(ctx.getValue(indexId, null));
            if (index == null || index < 0 || index >= items.size()) {
                ctx.setValue(modeId, "list");
                ctx.removeValue(indexId);
            } else {
                String editFormKey = crudKey + ":edit:" + index;
                renderModeTitle("Edit " + humanize(type.getSimpleName()));
                Optional<T> updated = ctx.form(editFormKey, items.get(index));
                renderCancel(crudKey);
                if (updated.isPresent()) {
                    items.set(index, updated.get());
                    ctx.clearForm(editFormKey, type);
                    ctx.setValue(modeId, "list");
                    ctx.removeValue(indexId);
                    ctx.success(humanize(type.getSimpleName()) + " updated");
                }
                return;
            }
        }

        renderNew(crudKey, type);
        ctx.table(title, items);
        renderRowActions(crudKey, items.size());
    }

    /**
     * Repository-backed CRUD with stable user-defined identity.
     */
    public <T, ID> void crud(Class<T> type, CrudRepository<T, ID> repository) {
        crud(humanize(type.getSimpleName()), type, repository);
    }

    public <T, ID> void crud(String title, Class<T> type, CrudRepository<T, ID> repository) {
        if (type == null) throw new IllegalArgumentException("crud type must not be null");
        if (repository == null) throw new IllegalArgumentException("crud repository must not be null");

        String crudKey = "crud:repo:" + type.getName();
        String modeId = ctx.getNextWidgetId(crudKey + ":mode");
        String selectedIdState = ctx.getNextWidgetId(crudKey + ":selected-id");
        String selectedTokenState = ctx.getNextWidgetId(crudKey + ":selected-token");
        String createFormKey = crudKey + ":create";

        String mode = ctx.getValue(modeId, "list");
        List<T> items = repository.findAll();

        if (consume(crudKey + ":new")) {
            ctx.clearForm(createFormKey, type);
            ctx.setValue(modeId, "create");
            ctx.removeValue(selectedIdState);
            ctx.removeValue(selectedTokenState);
            mode = "create";
        }

        for (T item : items) {
            ID id = repository.id(item);
            String token = identityToken(id);
            if (consume(crudKey + ":edit:" + token)) {
                String editFormKey = crudKey + ":edit:" + token;
                ctx.clearForm(editFormKey, type);
                ctx.setValue(modeId, "edit");
                ctx.setValue(selectedIdState, id);
                ctx.setValue(selectedTokenState, token);
                mode = "edit";
            }
            if (consume(crudKey + ":delete:" + token)) {
                repository.deleteById(id);
                ctx.setValue(modeId, "list");
                ctx.removeValue(selectedIdState);
                ctx.removeValue(selectedTokenState);
                mode = "list";
                items = repository.findAll();
                break;
            }
        }

        if (consume(crudKey + ":cancel")) {
            if ("create".equals(mode)) {
                ctx.clearForm(createFormKey, type);
            } else if ("edit".equals(mode)) {
                String token = ctx.getValue(selectedTokenState, null);
                if (token != null) ctx.clearForm(crudKey + ":edit:" + token, type);
            }
            ctx.setValue(modeId, "list");
            ctx.removeValue(selectedIdState);
            ctx.removeValue(selectedTokenState);
            mode = "list";
        }

        ctx.header(title);

        if ("create".equals(mode)) {
            renderModeTitle("New " + humanize(type.getSimpleName()));
            Optional<T> created = ctx.form(createFormKey, type);
            renderCancel(crudKey);
            if (created.isPresent()) {
                repository.create(created.get());
                ctx.clearForm(createFormKey, type);
                ctx.setValue(modeId, "list");
                ctx.success(humanize(type.getSimpleName()) + " saved");
            }
            return;
        }

        if ("edit".equals(mode)) {
            @SuppressWarnings("unchecked")
            ID selectedId = (ID) ctx.getValue(selectedIdState, null);
            String token = ctx.getValue(selectedTokenState, null);
            Optional<T> current = selectedId == null ? Optional.empty() : repository.findById(selectedId);

            if (selectedId == null || token == null || current.isEmpty()) {
                ctx.setValue(modeId, "list");
                ctx.removeValue(selectedIdState);
                ctx.removeValue(selectedTokenState);
            } else {
                String editFormKey = crudKey + ":edit:" + token;
                renderModeTitle("Edit " + humanize(type.getSimpleName()));
                Optional<T> updated = ctx.form(editFormKey, current.get());
                renderCancel(crudKey);
                if (updated.isPresent()) {
                    repository.update(selectedId, updated.get());
                    ctx.clearForm(editFormKey, type);
                    ctx.setValue(modeId, "list");
                    ctx.removeValue(selectedIdState);
                    ctx.removeValue(selectedTokenState);
                    ctx.success(humanize(type.getSimpleName()) + " updated");
                }
                return;
            }
        }

        renderNew(crudKey, type);
        ctx.table(title, repository.findAll());
        renderRepositoryActions(crudKey, repository, repository.findAll());
    }

    private <T, ID> void renderRepositoryActions(String crudKey, CrudRepository<T, ID> repository, List<T> items) {
        if (items.isEmpty()) return;
        StringBuilder html = new StringBuilder("<div class='mb-6 space-y-2' data-jui='crud-actions'>");
        int row = 1;
        for (T item : items) {
            ID id = repository.id(item);
            String token = identityToken(id);
            String editId = ctx.getNextWidgetId(crudKey + ":edit:" + token);
            String deleteId = ctx.getNextWidgetId(crudKey + ":delete:" + token);
            html.append(String.format(
                    "<div class='flex items-center gap-2 text-sm'><span class='text-gray-500 dark:text-gray-400'>Row %d</span>" +
                    "<button type='button' onclick=\"sendUpdate('%s', true)\" class='px-3 py-1 border rounded-md dark:border-gray-600' data-jui='crud-edit'>Edit</button>" +
                    "<button type='button' onclick=\"sendUpdate('%s', true)\" class='px-3 py-1 border border-red-300 text-red-600 rounded-md dark:border-red-700 dark:text-red-400' data-jui='crud-delete'>Delete</button></div>",
                    row++, editId, deleteId));
        }
        html.append("</div>");
        ctx.addHtml(html.toString());
    }

    private String identityToken(Object id) {
        if (id == null) throw new IllegalArgumentException("CrudRepository returned a null id");
        return Integer.toUnsignedString(Objects.hashCode(id), 36)
                + "-" + Integer.toUnsignedString(String.valueOf(id).hashCode(), 36);
    }

    private boolean consume(String key) {
        return ctx.consumeBoolean(ctx.getNextWidgetId(key));
    }

    private void renderNew(String crudKey, Class<?> type) {
        String id = ctx.getNextWidgetId(crudKey + ":new");
        ctx.addHtml(String.format(
                "<div class='mb-4'><button type='button' onclick=\"sendUpdate('%s', true)\" " +
                "class='px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-md transition' data-jui='crud-new'>+ New %s</button></div>",
                id, escapeHtml(humanize(type.getSimpleName()))));
    }

    private void renderRowActions(String crudKey, int size) {
        if (size == 0) return;
        StringBuilder html = new StringBuilder("<div class='mb-6 space-y-2' data-jui='crud-actions'>");
        for (int i = 0; i < size; i++) {
            String editId = ctx.getNextWidgetId(crudKey + ":edit:" + i);
            String deleteId = ctx.getNextWidgetId(crudKey + ":delete:" + i);
            html.append(String.format(
                    "<div class='flex items-center gap-2 text-sm'><span class='text-gray-500 dark:text-gray-400'>Row %d</span>" +
                    "<button type='button' onclick=\"sendUpdate('%s', true)\" class='px-3 py-1 border rounded-md dark:border-gray-600' data-jui='crud-edit'>Edit</button>" +
                    "<button type='button' onclick=\"sendUpdate('%s', true)\" class='px-3 py-1 border border-red-300 text-red-600 rounded-md dark:border-red-700 dark:text-red-400' data-jui='crud-delete'>Delete</button></div>",
                    i + 1, editId, deleteId));
        }
        html.append("</div>");
        ctx.addHtml(html.toString());
    }

    private void renderCancel(String crudKey) {
        String id = ctx.getNextWidgetId(crudKey + ":cancel");
        ctx.addHtml(String.format(
                "<div class='mb-4'><button type='button' onclick=\"sendUpdate('%s', true)\" class='px-4 py-2 border rounded-md dark:border-gray-600' data-jui='crud-cancel'>Cancel</button></div>",
                id));
    }

    private void renderModeTitle(String text) {
        ctx.subheader(text);
    }

    private Integer numericIndex(Object value) {
        if (value instanceof Number number) return number.intValue();
        if (value == null) return null;
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String humanize(String value) {
        if (value == null || value.isBlank()) return "";
        String spaced = value.replace('_', ' ').replaceAll("(?<=[a-z0-9])(?=[A-Z])", " ");
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
