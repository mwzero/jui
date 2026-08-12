package it.jui.framework.server;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import it.jui.cli.JuiCLI;
import it.jui.framework.core.AppProvider;

import java.net.URL;

//jetty
import org.eclipse.jetty.server.Server;

public class JuiServer {

    Server server;

    public JuiServer(int port, AppProvider appProvider) throws Exception {

        ISessionManager sessionManager = new InMemorySessionManager();

        Server server = new Server(port);

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

        System.out.println("Server attivo: http://localhost:" + port + "/ui");
    }

    public void start() throws Exception {
        
        server.start();
        server.join();
    }

}
