package it.jui.apps.auth;

import it.jui.UIContext;
import it.jui.auth.AuthUser;
import it.jui.server.InMemorySessionManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthAppTest {
    @Test void anonymousViewDoesNotExposeThePrivateForm() {
        try (var sessions = new InMemorySessionManager()) {
            var ui = new UIContext(sessions.createSession(), "tab-a");
            new AuthApp().run(ui);
            assertTrue(ui.getHtml().contains("Accedi con Google"));
            assertFalse(ui.getHtml().contains("data-jui='form'"));
            assertFalse(ui.getHtml().contains("Ultima nota"));
        }
    }

    @Test void notesAreServerStatePrivateToTheTabAndDiscardedOnLogout() {
        try (var sessions = new InMemorySessionManager()) {
            var session = sessions.createSession();
            session.authenticate(new AuthUser("google-sub", "ada@example.com", "Ada", null));
            var ui = new UIContext(session, "tab-a");
            new AuthApp().run(ui);
            assertTrue(ui.getHtml().contains("ada@example.com"));
            String key = "form:" + AuthApp.PrivateNote.class.getName();
            ui.setWidgetValue(ui.getNextWidgetId(key + ":message"), "<private note>");
            ui.setWidgetValue(ui.getNextWidgetId(key + ":submit"), true);
            var saved = new UIContext(session, "tab-a");
            new AuthApp().run(saved);
            assertEquals("<private note>", saved.getRawValue("auth-demo:last-note"));
            assertTrue(saved.getHtml().contains("&lt;private note&gt;"));
            var otherTab = new UIContext(session, "tab-b");
            new AuthApp().run(otherTab);
            assertEquals("", otherTab.getRawValue("auth-demo:last-note"));
            assertFalse(otherTab.getHtml().contains("private note"));
            var anonymous = sessions.rotateSession(session, null);
            var loggedOut = new UIContext(anonymous, "tab-a");
            new AuthApp().run(loggedOut);
            assertFalse(loggedOut.authenticated());
            assertNull(loggedOut.getRawValue("auth-demo:last-note"));
            anonymous.authenticate(new AuthUser("different-user", null, null, null));
            assertNull(new UIContext(anonymous, "tab-a").getRawValue("auth-demo:last-note"));
        }
    }
}
