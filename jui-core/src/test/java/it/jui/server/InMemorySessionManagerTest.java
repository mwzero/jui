package it.jui.server;

import org.junit.jupiter.api.Test;
import it.jui.auth.AuthUser;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

class InMemorySessionManagerTest {
    @Test void idleAndAbsoluteTimeoutsAreEnforcedAndCleanupInvalidatesReferences() {
        var clock = new MutableClock();
        try (var sessions = new InMemorySessionManager(clock)) {
            var idle = sessions.createSession();
            idle.authenticate(new AuthUser("user", null, null, null));
            clock.advance(Duration.ofMinutes(30));
            sessions.cleanupExpired();
            assertTrue(sessions.findSession(idle.id()).isEmpty());
            assertTrue(idle.user().isEmpty());

            var active = sessions.createSession();
            for (int i = 0; i < 47; i++) {
                clock.advance(Duration.ofMinutes(10));
                assertTrue(sessions.findSession(active.id()).isPresent());
            }
            clock.advance(Duration.ofMinutes(10));
            assertTrue(sessions.findSession(active.id()).isEmpty());
        }
    }

    @Test void rotationClearsIdentityValuesAndOAuthInOldSession() {
        try (var sessions = new InMemorySessionManager()) {
            var old = sessions.createSession();
            old.view("tab").internalValues().put("secret", "note");
            old.view("tab").widgetValues().put("field", "private");
            String state = old.beginOAuth(java.time.Instant.now());
            var fresh = sessions.rotateSession(old, new AuthUser("person", null, null, null));
            assertNotEquals(old.id(), fresh.id());
            assertNotEquals(old.csrfToken(), fresh.csrfToken());
            assertTrue(sessions.findSession(old.id()).isEmpty());
            assertTrue(old.findView("tab").isEmpty());
            assertFalse(old.consumeOAuth(state, java.time.Instant.now()));
            assertEquals("person", fresh.user().orElseThrow().id());
            assertTrue(fresh.findView("tab").isEmpty());
        }
    }

    @Test void closingTheManagerInvalidatesSessionsAndStopsCleanup() {
        var sessions = new InMemorySessionManager();
        var session = sessions.createSession();
        sessions.startCleanup();
        sessions.startCleanup();
        sessions.close();
        assertTrue(sessions.findSession(session.id()).isEmpty());
        assertThrows(IllegalStateException.class, sessions::createSession);
        assertThrows(IllegalStateException.class, sessions::startCleanup);
    }
}
