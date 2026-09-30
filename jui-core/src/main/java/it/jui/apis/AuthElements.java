package it.jui.apis;

import java.util.Optional;

import it.jui.auth.AuthUser;
import it.jui.UIContext;
import it.jui.input.WidgetSpec;

public class AuthElements extends BaseElements {

    public AuthElements(UIContext ctx) {
        super(ctx);
    }

    public Optional<AuthUser> authUser() {
        return ctx.authenticatedUser();
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
        ctx.registerWidget(id, WidgetSpec.logout());
        boolean clicked = ctx.consumeLogout(id);
        ctx.addHtml("<button type='button' onclick=\"sendUpdate('" + id + "', true)\" "
                + "class='px-3 py-2 rounded-md border border-gray-300 dark:border-gray-600 text-sm text-gray-700 dark:text-gray-200'>"
                + escapeHtml(label == null || label.isBlank() ? "Logout" : label) + "</button>");
        return clicked;
    }

}
