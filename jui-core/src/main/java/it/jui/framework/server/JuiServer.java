package it.jui.framework.server;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import it.jui.cli.JuiCLI;
import it.jui.framework.app.JuiProvider;

import java.net.URL;

//jetty
import org.eclipse.jetty.server.Server;

public class JuiServer {

    private static final int DEFAULT_PORT = 8080;

    Server server;

    /**
     * Starts a JUI server on the deployment-provided PORT when available, falling
     * back to 8080 for local development.
     */
    public JuiServer(JuiProvider appProvider) throws Exception {
        this(resolvePort(), appProvider);
    }

    public JuiServer(int port, JuiProvider appProvider) throws Exception {

        ISessionManager sessionManager = new InMemorySessionManager();

        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        URL staticResources = JuiCLI.class.getResource("/static");
        if (staticResources != null) {
            String resourceBase = staticResources.toExternalForm();
            if (!resourceBase.endsWith("/")) {
                resourceBase += "/";
            }
            context.setResourceBase(resourceBase);
        }
        server.setHandler(context);

        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("dirAllowed", "true");
        context.addServlet(staticHolder, "/");

        ServletHolder uiServletHolder = new ServletHolder(new UiServlet(sessionManager, appProvider));
        context.addServlet(uiServletHolder, "/ui");

        System.out.println("JUI server listening on port " + port);
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }

    private static int resolvePort() {
        String configured = System.getenv("PORT");
        if (configured == null || configured.isBlank()) {
            return DEFAULT_PORT;
        }

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
