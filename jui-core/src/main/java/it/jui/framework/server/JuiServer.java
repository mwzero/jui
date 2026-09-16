package it.jui.framework.server;

import com.sun.net.httpserver.HttpServer;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.auth.GoogleOAuthConfig;
import it.jui.framework.auth.GoogleOAuthSupport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JuiServer {

    private static final int DEFAULT_PORT = 8080;

    private final HttpServer server;
    private final ISessionManager sessionManager;
    private final ExecutorService executor;
    private final CountDownLatch stopped = new CountDownLatch(1);

    public JuiServer(JuiProvider appProvider) throws IOException {
        this(resolvePort(), appProvider);
    }

    public JuiServer(int port, JuiProvider appProvider) throws IOException {
        sessionManager = new InMemorySessionManager();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        executor = Executors.newVirtualThreadPerTaskExecutor();
        server.setExecutor(executor);

        server.createContext("/ui", new UiHandler(sessionManager, appProvider));
        server.createContext("/", new StaticHandler());
    }

    /** Registers Google OAuth endpoints before the server is started. */
    public JuiServer googleOAuth(GoogleOAuthConfig config) {
        GoogleOAuthSupport.install(server, sessionManager, config);
        return this;
    }

    /** Starts the server and blocks until {@link #stop()} is called. */
    public void start() throws InterruptedException {
        server.start();
        System.out.println("JUI server listening on port " + server.getAddress().getPort());
        try {
            stopped.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stop();
            throw e;
        }
    }

    public void stop() {
        server.stop(0);
        executor.shutdown();
        stopped.countDown();
    }

    private static int resolvePort() {
        String configured = System.getenv("PORT");
        if (configured == null || configured.isBlank()) return DEFAULT_PORT;
        try {
            int port = Integer.parseInt(configured);
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("PORT must be between 1 and 65535: " + configured);
            }
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("PORT must be a valid integer: " + configured, e);
        }
    }
}
