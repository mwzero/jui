package it.my;

import it.my.framework.SessionManager;
import it.my.framework.InMemorySessionManager;
import it.my.framework.compiler.HotReloadService;
import it.my.framework.server.UiServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

public class Main {
    
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
        String template = 
            "import it.my.framework.*;\n" +
            "import java.util.*;\n\n" +
            "public class " + className + " implements UIApp {\n" +
            "    @Override\n" +
            "    public void run(UIContext ui) {\n" +
            "        ui.title(\"Ciao da " + className + "\");\n" +
            "        ui.text(\"Questa app supporta Hot Reload con stato persistente.\");\n\n" +
            "        String nome = ui.textInput(\"Come ti chiami?\", \"Ospite\");\n" +
            "        int age = ui.slider(\"La tua età\", 0, 100, 25);\n\n" +
            "        if (ui.button(\"Conferma Dati\")) {\n" +
            "             ui.info(\"Dati salvati: \" + nome + \", anni: \" + age);\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(template);
        }
        System.out.println("File creato: " + filename);
        System.out.println("Avvia con: java -jar framework.jar watch " + filename);
    }

    private static void startServer(String sourceFilePath) throws Exception {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            System.err.println("File sorgente non trovato: " + sourceFile.getAbsolutePath());
            return;
        }

        System.out.println("=== Avvio Server Watch Mode ===");
        System.out.println("Sorgente: " + sourceFile.getName());

        HotReloadService hotReloadService = new HotReloadService(sourceFile);
        SessionManager sessionManager = new InMemorySessionManager();

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        URL staticResources = Main.class.getResource("/static");
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