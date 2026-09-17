package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import it.jui.auth.AuthUser;
import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class AuthElementsTest {

    @Test
    void authenticationIsAbsentByDefaultAndCanBeReadFromStructuredState() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext empty = new UIContext("s1", sessions);
        assertFalse(empty.authenticated());
        assertTrue(empty.authUser().isEmpty());

        sessions.updateState("s1", AuthUser.SESSION_KEY, Map.of(
                "id", "42",
                "email", "ada@example.com",
                "name", "Ada",
                "picture", "https://example.com/ada.png"));

        UIContext authenticated = new UIContext("s1", sessions);
        assertTrue(authenticated.authenticated());
        assertEquals("Ada", authenticated.authUser().orElseThrow().name());
    }

    @Test
    void googleLoginButtonEscapesCustomLabel() {
        UIContext ui = new UIContext("s1", new InMemorySessionManager());
        ui.googleLoginButton("<Google>");
        assertTrue(ui.getHtml().contains("&lt;Google&gt;"));
        assertTrue(ui.getHtml().contains("/auth/google/login"));
    }

    @Test
    void logoutConsumesClickAndRemovesAuthenticatedUser() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        sessions.updateState("s1", AuthUser.SESSION_KEY,
                new AuthUser("1", "a@example.com", "Ada", null));
        UIContext probe = new UIContext("s1", sessions);
        String id = probe.getNextWidgetId("auth:logout:Logout");
        sessions.updateState("s1", id, true);

        UIContext ui = new UIContext("s1", sessions);
        assertTrue(ui.logoutButton("Logout"));
        assertFalse(ui.authenticated());
        assertFalse(new UIContext("s1", sessions).logoutButton("Logout"));
    }
}
