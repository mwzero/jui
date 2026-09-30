package it.jui.auth;

import it.jui.server.HttpTestRuntime;
import it.jui.server.MutableClock;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class GoogleOAuthSupportTest {
    private static final AuthUser ADA = new AuthUser("google-sub-42", "ada@example.com", "Ada", null);

    private HttpTestRuntime runtime(MutableClock clock, GoogleOAuthClient client) throws Exception {
        var runtime = new HttpTestRuntime(ui -> {
            ui.text(ui.authUser().map(AuthUser::id).orElse("anonymous"));
            ui.googleLoginButton("Login");
            ui.logoutButton("Logout");
        }, clock);
        GoogleOAuthSupport.install(runtime.server, runtime.sessions,
                new GoogleOAuthConfig("test-client", "test-secret", runtime.origin + "/auth/google/callback"),
                runtime.cookies, client, clock);
        runtime.start();
        return runtime;
    }

    private String login(HttpTestRuntime.Browser browser) throws Exception {
        var response = browser.get("/auth/google/login");
        assertEquals(302, response.statusCode());
        String url = response.headers().firstValue("Location").orElseThrow();
        assertFalse(url.contains("sessionId"));
        for (String pair : URI.create(url).getRawQuery().split("&")) {
            if (pair.startsWith("state=")) return URLDecoder.decode(pair.substring(6), StandardCharsets.UTF_8);
        }
        throw new AssertionError("Missing state");
    }
    private String callback(String state) { return "/auth/google/callback?code=code&state=" + state; }

    @Test void successfulCallbackRotatesSessionAndStateCannotBeReused() throws Exception {
        var calls = new AtomicInteger();
        try (var runtime = runtime(new MutableClock(), (code, config) -> { calls.incrementAndGet(); return ADA; })) {
            var browser = runtime.browser();
            browser.getUi();
            var old = browser.session();
            old.view("tab-a").internalValues().put("note", "previous identity");
            String state = login(browser);
            assertFalse(state.contains(old.id()));
            assertEquals(302, browser.get(callback(state)).statusCode());
            assertEquals(ADA, browser.session().user().orElseThrow());
            assertTrue(runtime.sessions.findSession(old.id()).isEmpty());
            assertTrue(browser.session().findView("tab-a").isEmpty());
            browser.getUi();
            assertTrue(browser.html().contains(ADA.id()));
            assertEquals(403, browser.get(callback(state)).statusCode());
            assertEquals(1, calls.get());
        }
    }

    @Test void callbackRequiresTheOriginalCookieAndMatchingState() throws Exception {
        var calls = new AtomicInteger();
        try (var runtime = runtime(new MutableClock(), (code, config) -> { calls.incrementAndGet(); return ADA; })) {
            var browser = runtime.browser();
            assertEquals(401, browser.get("/auth/google/login?sessionId=chosen").statusCode());
            browser.getUi();
            String state = login(browser);
            var stranger = runtime.browser();
            assertEquals(403, stranger.get(callback(state)).statusCode());
            stranger.getUi();
            assertEquals(403, stranger.get(callback(state)).statusCode());
            assertEquals(403, browser.get(callback(state + "x")).statusCode());
            assertEquals(400, browser.get("/auth/google/callback?code=code").statusCode());
            assertEquals(0, calls.get());
            assertTrue(stranger.session().user().isEmpty());
            assertEquals(302, browser.get(callback(state)).statusCode());
            assertEquals(1, calls.get());
        }
    }

    @Test void expiredStateNeverCallsGoogle() throws Exception {
        var clock = new MutableClock();
        try (var runtime = runtime(clock, (code, config) -> { fail("Google must not be called"); return ADA; })) {
            var browser = runtime.browser();
            browser.getUi();
            String state = login(browser);
            clock.advance(Duration.ofMinutes(10));
            assertEquals(403, browser.get(callback(state)).statusCode());
            assertTrue(browser.session().user().isEmpty());
        }
    }

    @Test void consentDenialConsumesStateAndShowsASafeNotice() throws Exception {
        try (var runtime = runtime(new MutableClock(), (code, config) -> { fail("Google must not be called"); return ADA; })) {
            var browser = runtime.browser();
            browser.getUi();
            String state = login(browser);
            assertEquals(302, browser.get("/auth/google/callback?error=access_denied&state=" + state).statusCode());
            browser.getUi();
            assertTrue(browser.html().contains("cancelled or denied"));
            assertTrue(browser.session().user().isEmpty());
            assertEquals(403, browser.get(callback(state)).statusCode());
        }
    }

    @Test void providerFailuresAndMissingSubjectDoNotAuthenticateOrExposeDiagnostics() throws Exception {
        for (GoogleOAuthClient client : java.util.List.<GoogleOAuthClient>of(
                (code, config) -> { throw new IOException("secret-token-diagnostic"); },
                (code, config) -> { throw new java.net.http.HttpTimeoutException("secret-token-diagnostic"); },
                (code, config) -> new AuthUser("", "ada@example.com", "Ada", null))) {
            try (var runtime = runtime(new MutableClock(), client)) {
                var browser = runtime.browser();
                browser.getUi();
                String state = login(browser);
                assertEquals(302, browser.get(callback(state)).statusCode());
                assertTrue(browser.session().user().isEmpty());
                browser.getUi();
                assertTrue(browser.html().contains("could not be completed"));
                assertFalse(browser.html().contains("secret-token-diagnostic"));
                assertEquals(403, browser.get(callback(state)).statusCode());
            }
        }
    }

    @Test void anInFlightCallbackCannotRestoreAnInvalidatedSession() throws Exception {
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        try (var runtime = runtime(new MutableClock(), (code, config) -> {
            entered.countDown();
            if (!release.await(5, TimeUnit.SECONDS)) throw new IOException("Test timeout");
            return ADA;
        }); var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var browser = runtime.browser();
            browser.getUi();
            String state = login(browser);
            String sessionId = browser.session().id();
            var callback = executor.submit(() -> browser.get(callback(state)));
            try {
                assertTrue(entered.await(3, TimeUnit.SECONDS));
                runtime.sessions.removeSession(sessionId);
            } finally { release.countDown(); }
            assertEquals(403, callback.get().statusCode());
            assertTrue(runtime.sessions.findSession(sessionId).isEmpty());
        }
    }

    @Test void secureCookiesComeFromConfiguredHttpsNotProxyHeaders() throws Exception {
        try (var runtime = new HttpTestRuntime(ui -> ui.text("hello"))) {
            GoogleOAuthSupport.install(runtime.server, runtime.sessions,
                    new GoogleOAuthConfig("client", "secret", "https://example.com/auth/google/callback"),
                    runtime.cookies, (code, config) -> ADA, new MutableClock());
            runtime.start();
            assertTrue(runtime.browser().getUi().headers().firstValue("Set-Cookie").orElseThrow().contains("; Secure"));
        }
    }

    @Test void callbackConfigurationRejectsRemotePlainHttpAndReservedPaths() {
        for (String callback : java.util.List.of("http://example.com/callback", "http://127.999.0.1/callback", "https://example.com/", "https://example.com/ui",
                "https://user:password@example.com/callback", "https://example.com/callback#fragment", "/callback")) {
            assertThrows(IllegalArgumentException.class, () -> new GoogleOAuthConfig("client", "secret", callback), callback);
        }
        assertDoesNotThrow(() -> new GoogleOAuthConfig("client", "secret", "http://localhost:8080/auth/google/callback"));
        assertDoesNotThrow(() -> new GoogleOAuthConfig("client", "secret", "http://[::1]:8080/auth/google/callback"));
    }
}
