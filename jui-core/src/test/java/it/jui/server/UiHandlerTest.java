package it.jui.server;

import org.junit.jupiter.api.Test;
import it.jui.auth.AuthUser;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static it.jui.server.HttpTestRuntime.id;
import static org.junit.jupiter.api.Assertions.*;

class UiHandlerTest {
    @Test void forgedIdentityAndInternalStateNeverReachTheApplication() throws Exception {
        var renders = new AtomicInteger();
        try (var runtime = new HttpTestRuntime(ui -> {
            renders.incrementAndGet();
            ui.text(ui.authenticated() ? "PRIVATE" : "PUBLIC");
            ui.setValue(id("Name"), "server-owned");
            ui.textInput("Name", "Guest");
        })) {
            runtime.start();
            var browser = runtime.browser();
            assertEquals(200, browser.getUi().statusCode());
            assertEquals(403, browser.update("__jui_auth_user", Map.of("id", "admin")).statusCode());
            assertEquals(403, browser.update("__jui_csrf", "replacement").statusCode());
            assertEquals(403, browser.update(id("crud:mode"), "edit").statusCode());
            assertEquals(1, renders.get());
            assertTrue(browser.session().user().isEmpty());
            assertEquals(200, browser.update(id("Name"), "Ada").statusCode());
            assertTrue(browser.html().contains("Ada"));
            assertEquals("server-owned", browser.session().view(browser.viewId).internalValues().get(id("Name")));
        }
    }

    @Test void validatesProtocolBeforeMutatingStateOrRendering() throws Exception {
        var renders = new AtomicInteger();
        try (var runtime = new HttpTestRuntime(ui -> { renders.incrementAndGet(); ui.textInput("Name", "Guest"); })) {
            runtime.start();
            var browser = runtime.browser();
            var bootstrap = browser.getUi();
            String cookie = bootstrap.headers().firstValue("Set-Cookie").orElseThrow();
            assertTrue(cookie.contains("HttpOnly"));
            assertTrue(cookie.contains("SameSite=Lax"));
            assertTrue(cookie.contains("Path=/"));
            assertFalse(cookie.contains("Domain="));
            assertFalse(bootstrap.body().contains(browser.cookie().split("=", 2)[1]));
            String body = "{\"id\":\"" + id("Name") + "\",\"value\":\"Ada\",\"revision\":1}";
            assertEquals(403, browser.postRaw(body, null, "application/json").statusCode());
            assertEquals(403, browser.postRaw(body, "wrong", "application/json").statusCode());
            assertEquals(400, browser.postRaw(body, browser.csrf, "text/plain").statusCode());
            for (String invalid : List.of("{", "[]", "null", "{}", "{\"id\":3,\"value\":true,\"revision\":1}",
                    body.replace("\"revision\":1", "\"revision\":1.5"))) {
                assertEquals(400, browser.postRaw(invalid, browser.csrf, "application/json").statusCode(), invalid);
            }
            assertEquals(401, runtime.browser().postRaw(body, browser.csrf, "application/json").statusCode());
            assertEquals(400, browser.update(id("Name"), List.of("invalid")).statusCode());
            assertEquals(400, browser.postRaw(body.replace("\"Ada\"", "null"), browser.csrf, "application/json").statusCode());
            assertEquals(400, browser.get("/ui?sessionId=chosen&viewId=tab-a").statusCode());
            assertEquals(1, renders.get());
        }
    }

    @Test void rejectsOldRevisionsAndControlsNoLongerRendered() throws Exception {
        try (var runtime = new HttpTestRuntime(ui -> {
            if (ui.checkbox("Show", true)) ui.textInput("Hidden", "");
        })) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            var stale = browser.snapshot();
            assertEquals(200, browser.update(id("Show"), false).statusCode());
            assertEquals(409, stale.update(id("Hidden"), "old action").statusCode());
            assertEquals(403, browser.update(id("Hidden"), "new action").statusCode());
            assertEquals(409, browser.tab("unknown").postRaw(
                    "{\"id\":\"anything\",\"value\":true,\"revision\":0}", browser.csrf, "application/json").statusCode());
        }
    }

    @Test void renderFailuresDiscardMarkupAndRevokeTheAllowlist() throws Exception {
        try (var runtime = new HttpTestRuntime(ui -> {
            if (ui.textInput("Name", "").equals("explode")) throw new IllegalStateException("private diagnostic");
        })) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            assertEquals(200, browser.update(id("Name"), "explode").statusCode());
            assertTrue(browser.html().contains("Runtime Error"));
            assertFalse(browser.html().contains("private diagnostic"));
            assertFalse(browser.html().contains("<input"));
            assertEquals(403, browser.update(id("Name"), "recover").statusCode());
        }
    }

    @Test void logoutRotatesBeforeRenderingAndClearsAllTabs() throws Exception {
        var logoutObserved = new AtomicBoolean();
        try (var runtime = new HttpTestRuntime(ui -> {
            if (ui.logoutButton("Logout")) logoutObserved.set(true);
            if (!ui.authenticated()) { ui.text("PUBLIC"); return; }
            ui.text("PRIVATE");
            ui.setValue("note", "secret");
            ui.textInput("Name", "Ada");
        })) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            browser.session().authenticate(new AuthUser("42", "ada@example.com", "Ada", null));
            browser.getUi();
            var otherTab = browser.tab("tab-b");
            otherTab.getUi();
            SessionState old = browser.session();
            var oldCookie = browser.snapshot();
            assertEquals(200, browser.update(id("auth:logout:Logout"), true).statusCode());
            assertTrue(logoutObserved.get());
            assertTrue(browser.html().contains("PUBLIC"));
            assertFalse(browser.html().contains("PRIVATE"));
            assertNotEquals(oldCookie.cookie(), browser.cookie());
            assertTrue(runtime.sessions.findSession(old.id()).isEmpty());
            assertTrue(old.findView("tab-b").isEmpty());
            assertTrue(browser.session().view(browser.viewId).internalValues().isEmpty());
            assertEquals(401, oldCookie.update(id("Name"), "attack").statusCode());
            assertEquals(403, otherTab.update(id("Name"), "stale").statusCode());
        }
    }

    @Test void tabsShareIdentityButNotWidgetOrInternalValues() throws Exception {
        try (var runtime = new HttpTestRuntime(ui -> {
            ui.text(ui.authUser().map(AuthUser::id).orElse("anonymous"));
            ui.textInput("Name", "Guest");
        })) {
            runtime.start();
            var first = runtime.browser();
            first.getUi();
            first.session().authenticate(new AuthUser("person-1", null, null, null));
            first.update(id("Name"), "Alice");
            var second = first.tab("tab-b");
            second.getUi();
            assertTrue(second.html().contains("person-1"));
            assertFalse(second.html().contains("Alice"));
            second.update(id("Name"), "Bob");
            first.getUi();
            assertTrue(first.html().contains("Alice"));
            assertFalse(first.html().contains("Bob"));
            var stranger = runtime.browser();
            stranger.getUi();
            assertTrue(stranger.html().contains("anonymous"));
            assertNotEquals(first.cookie(), stranger.cookie());
        }
    }

    @Test void concurrentActionsWithTheSameRevisionExecuteOnlyOnce() throws Exception {
        var clicks = new AtomicInteger();
        try (var runtime = new HttpTestRuntime(ui -> { if (ui.button("Save")) clicks.incrementAndGet(); })) {
            runtime.start();
            var first = runtime.browser();
            first.getUi();
            var second = first.snapshot();
            try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
                var a = executor.submit(() -> first.update(id("Save"), true).statusCode());
                var b = executor.submit(() -> second.update(id("Save"), true).statusCode());
                assertEquals(java.util.Set.of(200, 409), java.util.Set.of(a.get(), b.get()));
            }
            first.getUi();
            assertEquals(1, clicks.get());
        }
    }

    @Test void expiredCookiesCannotUpdateAndBootstrapCreatesAFreshSession() throws Exception {
        var clock = new MutableClock();
        try (var runtime = new HttpTestRuntime(ui -> ui.textInput("Name", "Guest"), clock)) {
            runtime.start();
            var browser = runtime.browser();
            browser.getUi();
            String cookie = browser.cookie();
            clock.advance(Duration.ofMinutes(30));
            assertEquals(401, browser.update(id("Name"), "expired").statusCode());
            assertEquals(200, browser.getUi().statusCode());
            assertNotEquals(cookie, browser.cookie());
        }
    }
}
