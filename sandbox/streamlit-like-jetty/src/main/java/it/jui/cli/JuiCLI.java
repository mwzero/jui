package it.jui.cli;

import it.jui.framework.session.SessionManager;
import it.jui.framework.server.UiServlet;
import it.jui.framework.session.InMemorySessionManager;

//jetty
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class JuiCLI {
    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String command = args[0];
        String filename = args[1];

        if ("init".equalsIgnoreCase(command)) {
            initProject(filename);
        } else if ("watch".equalsIgnoreCase(command) || "run".equalsIgnoreCase(command)) {
            startServer(filename);
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Streamlit-like Java CLI");
        System.out.println("-----------------------");
        System.out.println("Usage:");
        System.out.println("  java -jar framework.jar init <AppName.java>");
        System.out.println("  java -jar framework.jar watch <AppName.java>");
    }

    private static void initProject(String filename) throws Exception {
        File file = new File(filename);
        if (file.exists()) {
            System.out.println("Errore: Il file " + filename + " esiste già.");
            return;
        }
        
        String className = filename.replace(".java", "");
        
        // 1. Carica il template dalle risorse (dentro il JAR o classpath)
        String rawTemplate = loadTemplateResource("/AppTemplate.txt");
        
        if (rawTemplate == null) {
            System.out.println("Errore critico: Impossibile trovare il template /AppTemplate.txt");
            return;
        }

        // 2. Sostituisce il placeholder con il nome reale della classe
        String finalContent = rawTemplate.replace("{{CLASS_NAME}}", className);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(finalContent);
        }
        
        System.out.println("File creato: " + filename);
        System.out.println("Avvia con: java -jar framework.jar watch " + filename);
    }

    // Metodo helper per leggere il file dalle risorse come Stringa
    private static String loadTemplateResource(String path) {
        try (InputStream is = JuiCLI.class.getResourceAsStream(path)) {
            if (is == null) return null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void startServer(String sourceFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        
        System.out.println("=== Avvio Server Watch Mode ===");
        System.out.println("Sorgente: " + sourceFile.getName());

        HotReloadService hotReloadService = new HotReloadService(sourceFile);
        SessionManager sessionManager = new InMemorySessionManager();

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        URL staticResources = JuiCLI.class.getResource("/static");
        if (staticResources != null) {
            String resourceBase = staticResources.toExternalForm();
            if (!resourceBase.endsWith("/")) resourceBase += "/";
            context.setResourceBase(resourceBase);
        }
        
        server.setHandler(context);

        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("dirAllowed", "true");
        context.addServlet(staticHolder, "/");

        ServletHolder uiServletHolder = new ServletHolder("uiServlet", new UiServlet(sessionManager, hotReloadService));
        context.addServlet(uiServletHolder, "/ui");

        System.out.println("Server attivo: http://localhost:8080/ui");
        server.start();
        server.join();
    }
}