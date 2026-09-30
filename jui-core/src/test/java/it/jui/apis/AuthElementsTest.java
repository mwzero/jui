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
    void authenticationIgnoresLegacyMapsAndUsesTrustedIdentity() {
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
        assertFalse(authenticated.authenticated());
        sessions.updateState("s1", AuthUser.SESSION_KEY, new AuthUser("forged", null, null, null));
        assertFalse(authenticated.authenticated());
        sessions.getOrCreateSession("s1").authenticate(new AuthUser("42", "ada@example.com", "Ada", null));
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
    void logoutReportsTheServerProcessedEventOnce() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        var oldSession = sessions.createSession();
        oldSession.authenticate(new AuthUser("1", "a@example.com", "Ada", null));
        String id = new UIContext(oldSession, "tab").getNextWidgetId("auth:logout:Logout");
        var anonymous = sessions.rotateSession(oldSession, null);
        UIContext ui = new UIContext(anonymous, "tab", id);
        assertTrue(ui.logoutButton("Logout"));
        assertFalse(ui.logoutButton("Logout"));
        assertFalse(ui.authenticated());
        assertTrue(sessions.findSession(oldSession.id()).isEmpty());
    }
}
