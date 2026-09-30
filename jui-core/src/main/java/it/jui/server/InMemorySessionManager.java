package it.jui.server;

import it.jui.auth.AuthUser;
import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InMemorySessionManager implements ISessionManager {
    private static final Duration IDLE = Duration.ofMinutes(30);
    private static final Duration MAXIMUM = Duration.ofHours(8);
    private final ConcurrentHashMap<String, SessionState> sessions = new ConcurrentHashMap<>();
    private final Clock clock;
    private ScheduledExecutorService cleanup;
    private volatile boolean closed;

    public InMemorySessionManager() { this(Clock.systemUTC()); }
    public InMemorySessionManager(Clock clock) { this.clock = clock; }

    @Override public SessionState createSession() {
        if (closed) throw new IllegalStateException("Session manager is closed");
        SessionState session;
        do {
            session = new SessionState(SessionState.randomToken(), clock.instant());
        } while (sessions.putIfAbsent(session.id(), session) != null);
        if (closed) {
            removeSession(session.id());
            throw new IllegalStateException("Session manager is closed");
        }
        return session;
    }

    @Override public Optional<SessionState> findSession(String sessionId) {
        if (closed || sessionId == null) return Optional.empty();
        SessionState session = sessions.get(sessionId);
        if (session == null) return Optional.empty();
        synchronized (session) {
            if (!session.valid(clock.instant(), IDLE, MAXIMUM)) {
                sessions.remove(sessionId, session);
                session.invalidate();
                return Optional.empty();
            }
            session.touch(clock.instant());
            return Optional.of(session);
        }
    }

    @Override public SessionState getOrCreateSession(String sessionId) {
        if (closed) throw new IllegalStateException("Session manager is closed");
        return findSession(sessionId).orElseGet(() -> sessions.computeIfAbsent(sessionId,
                id -> new SessionState(id, clock.instant())));
    }

    @Override public SessionState rotateSession(SessionState previous, AuthUser user) {
        if (user != null && (user.id() == null || user.id().isBlank()))
            throw new IllegalArgumentException("Authenticated user requires an id");
        synchronized (previous) {
            if (findSession(previous.id()).isEmpty()) throw new IllegalStateException("Session expired");
            removeSession(previous.id());
            SessionState next = createSession();
            if (user != null) next.authenticate(user);
            return next;
        }
    }

    @Override public void removeSession(String sessionId) {
        SessionState session = sessions.get(sessionId);
        if (session != null) synchronized (session) {
            sessions.remove(sessionId, session);
            session.invalidate();
        }
    }

    public void cleanupExpired() {
        sessions.forEach((id, session) -> {
            synchronized (session) {
                if (!session.valid(clock.instant(), IDLE, MAXIMUM)) removeSession(id);
            }
        });
    }

    @Override public synchronized void startCleanup() {
        if (closed) throw new IllegalStateException("Session manager is closed");
        if (cleanup != null) return;
        cleanup = Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform()
                .daemon().name("jui-session-cleanup").factory());
        cleanup.scheduleAtFixedRate(this::cleanupExpired, 1, 1, TimeUnit.MINUTES);
    }

    @Override public synchronized void close() {
        closed = true;
        if (cleanup != null) cleanup.shutdownNow();
        sessions.keySet().forEach(this::removeSession);
    }
}
