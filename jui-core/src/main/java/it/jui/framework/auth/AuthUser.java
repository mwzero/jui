package it.jui.framework.auth;

public record AuthUser(String id, String email, String name, String picture) {
    public static final String SESSION_KEY = "__jui_auth_user";
}
