package it.jui.framework.apis;

import java.util.Map;
import java.util.Optional;

import it.jui.framework.auth.AuthUser;
import it.jui.framework.core.UIContext;

public class AuthElements extends BaseElements {

    public AuthElements(UIContext ctx) {
        super(ctx);
    }

    public Optional<AuthUser> authUser() {
        Object raw = ctx.getRawValue(AuthUser.SESSION_KEY);
        if (raw instanceof AuthUser user) return Optional.of(user);
        if (raw instanceof Map<?, ?> map) {
            return Optional.of(new AuthUser(
                    value(map, "id"), value(map, "email"), value(map, "name"), value(map, "picture")));
        }
        return Optional.empty();
    }

    public boolean authenticated() {
        return authUser().isPresent();
    }

    public void googleLoginButton(String label) {
        String safe = escapeHtml(label == null || label.isBlank() ? "Login with Google" : label);
        ctx.addHtml("<div class='mb-4'><button type='button' onclick=\"juiGoogleLogin('/auth/google/login')\" "
                + "class='inline-flex items-center gap-2 px-4 py-2 rounded-md border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-800 dark:text-gray-100 hover:bg-gray-50 dark:hover:bg-gray-700'>"
                + safe + "</button></div>");
    }

    public boolean logoutButton(String label) {
        String id = ctx.getNextWidgetId("auth:logout:" + label);
        boolean clicked = ctx.consumeBoolean(id);
        if (clicked) ctx.removeValue(AuthUser.SESSION_KEY);
        ctx.addHtml("<button type='button' onclick=\"sendUpdate('" + id + "', true)\" "
                + "class='px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 text-sm text-gray-700 dark:text-gray-200'>"
                + escapeHtml(label == null || label.isBlank() ? "Logout" : label) + "</button>");
        return clicked;
    }

    private String value(Map<?, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
