package it.my.data;
import it.my.data.server.DataAppServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import java.net.URL;

public class DataAppMain {
    public static void main(String[] args) throws Exception {
        DuckDataFrame df = new DuckDataFrame();
        Server server = new Server(8081);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        // FIX: Caricamento corretto da JAR
        URL res = DataAppMain.class.getResource("/static");
        if(res == null) throw new RuntimeException("Static missing");
        String resBase = res.toExternalForm();
        if(!resBase.endsWith("/")) resBase += "/";
        ctx.setResourceBase(resBase);

        server.setHandler(ctx);

        // Serving statico
        ServletHolder def = new ServletHolder("default", DefaultServlet.class);
        def.setInitParameter("dirAllowed","true");
        ctx.addServlet(def, "/");

        // API
        ctx.addServlet(new ServletHolder(new DataAppServlet(df)), "/data/*");

        System.out.println("Data App: http://localhost:8081/data_index.html");
        server.start();
        server.join();
    }
}