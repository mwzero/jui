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
            "        // Gestione Navigazione Multi-Page\n" +
            "        String currentPage = ui.getState(\"page\", \"Home\");\n\n" +
            "        // --- SIDEBAR ---\n" +
            "        ui.sidebar(() -> {\n" +
            "            ui.title(\"Menu\");\n" +
            "            if (ui.button(\"🏠 Home\")) ui.updateState(\"page\", \"Home\");\n" +
            "            if (ui.button(\"📊 Analisi\")) ui.updateState(\"page\", \"Analisi\");\n" +
            "            if (ui.button(\"⚙️ Settings\")) ui.updateState(\"page\", \"Settings\");\n" +
            "        });\n\n" +
            "        // --- MAIN CONTENT ---\n" +
            "        if (\"Home\".equals(currentPage)) {\n" +
            "            ui.title(\"Benvenuto nella Home\");\n" +
            "            ui.text(\"Questa è la pagina principale.\");\n" +
            "            ui.info(\"Usa la sidebar a sinistra per navigare.\");\n" +
            "        } else if (\"Analisi\".equals(currentPage)) {\n" +
            "            ui.title(\"Pagina di Analisi\");\n" +
            "            int val = ui.slider(\"Filtro Dati\", 0, 100, 50);\n" +
            "            ui.text(\"Valore filtro: \" + val);\n" +
            "        } else {\n" +
            "            ui.title(\"Impostazioni\");\n" +
            "            boolean dark = ui.checkbox(\"Modalità Esperto\", false);\n" +
            "            ui.text(\"Modalità esperto: \" + dark);\n" +
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