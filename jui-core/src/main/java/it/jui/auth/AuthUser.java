package it.jui.auth;

public record AuthUser(String id, String email, String name, String picture) {
    /** @deprecated Authentication is stored separately from UI state. */
    @Deprecated
    public static final String SESSION_KEY = "__jui_auth_user";
}
