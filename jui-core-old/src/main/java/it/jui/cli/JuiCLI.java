package it.jui.cli;

import it.jui.framework.core.AppProvider;
import it.jui.framework.core.HotReloadAppProvider;
import it.jui.framework.server.InMemorySessionManager;
import it.jui.framework.server.JuiServer;
import it.jui.framework.server.SessionManager;
import it.jui.framework.server.UiServlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            AppProvider appProvider = new HotReloadAppProvider(new File(filename));
            JuiServer server = new JuiServer(8080, appProvider);
            server.start();
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

    public static void initProject(String filename) throws Exception {
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

    
}