
package it.example.streamlitlike.ws;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.HashMap;
import java.util.Map;

public class WsMain {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        // Static files: serve index.html
        ResourceHandler resource = new ResourceHandler();
        resource.setDirectoriesListed(false);
        resource.setResourceBase("static"); // directory con index.html

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        Map<String, Map<String, String>> sessions = new HashMap<>();

        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, container) -> {
            container.addMapping("/ws", (req, resp) -> new ProductSocket(sessions));
        });

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resource);
        handlers.addHandler(context);

        server.setHandler(handlers);
        server.start();
        server.join();
    }
}
