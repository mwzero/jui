import os
import zipfile

files = {
    "README.md": r"""# Java Streamlit-like CLI (v2.1 - Sidebar)

Framework Java UI con **Hot Reloading**, **Dark Mode**, **Console** e **Multi-View Sidebar**.

## Istruzioni Rapide

1. **Compila:**
   ```bash
   cd streamlit-like-jetty
   mvn clean package
   ```

2. **Inizializza:**
   ```bash
   java -jar target/streamlit-like-jetty-1.0-SNAPSHOT.jar init MyApp.java
   ```

3. **Esegui:**
   ```bash
   java -jar target/streamlit-like-jetty-1.0-SNAPSHOT.jar watch MyApp.java
   ```
   
4. **Browser:** `http://localhost:8080/ui`
""",

    "streamlit-like-jetty/pom.xml": r"""<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>it.my.framework</groupId>
    <artifactId>streamlit-like-jetty</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <jetty.version>11.0.20</jetty.version>
        <gson.version>2.10.1</gson.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-server</artifactId>
            <version>${jetty.version}</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-servlet</artifactId>
            <version>${jetty.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>it.my.Main</mainClass>
                                </transformer>
                            </transformers>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
""",

    "streamlit-like-jetty/src/main/java/it/my/Main.java": r"""package it.my;

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
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/compiler/HotReloadService.java": r"""package it.my.framework.compiler;

import it.my.framework.UIApp;
import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class HotReloadService {

    private final File sourceFile;
    private UIApp currentAppInstance;
    private long lastModified = 0;

    public HotReloadService(File sourceFile) {
        this.sourceFile = sourceFile.getAbsoluteFile();
    }

    public synchronized UIApp getApp() {
        if (!sourceFile.exists()) {
             return ui -> ui.info("File sorgente non trovato: " + sourceFile.getAbsolutePath());
        }

        long currentModified = sourceFile.lastModified();
        
        if (currentModified > lastModified || currentAppInstance == null) {
            try {
                long start = System.currentTimeMillis();
                currentAppInstance = compileAndLoad();
                lastModified = currentModified;
                System.out.println("Ricompilato " + sourceFile.getName() + " in " + (System.currentTimeMillis() - start) + "ms");
            } catch (Exception e) {
                final String errorMsg = e.getMessage();
                return ui -> {
                    ui.title("Errore di Compilazione");
                    ui.info(errorMsg != null ? errorMsg.replace("\n", "<br>") : "Errore sconosciuto");
                };
            }
        }
        return currentAppInstance;
    }

    private UIApp compileAndLoad() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("Compilatore JDK non trovato. Assicurati di usare un JDK e non una JRE.");
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        File parentDir = sourceFile.getParentFile();
        
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));

        String currentClasspath = System.getProperty("java.class.path");
        List<String> options = new ArrayList<>();
        options.add("-classpath");
        options.add(currentClasspath + File.pathSeparator + parentDir.getAbsolutePath());

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);

        boolean success = task.call();
        fileManager.close();

        if (!success) throw new RuntimeException("Errore di sintassi nel codice.");

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{parentDir.toURI().toURL()})) {
            String className = sourceFile.getName().replace(".java", "");
            Class<?> loadedClass = classLoader.loadClass(className);
            return (UIApp) loadedClass.getDeclaredConstructor().newInstance();
        }
    }
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/server/UiServlet.java": r"""package it.my.framework.server;

import it.my.framework.*;
import it.my.framework.compiler.HotReloadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class UiServlet extends HttpServlet {
    private final SessionManager sm;
    private final HotReloadService hotReloadService;
    private final Gson gson = new Gson();

    public UiServlet(SessionManager sm, HotReloadService hotReloadService) {
        this.sm = sm;
        this.hotReloadService = hotReloadService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("sessionId");
        if (sid == null) sid = req.getSession(true).getId();
        render(sid, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("sessionId");
        if (sid == null) { resp.setStatus(400); return; }

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            Map<String, Object> data = gson.fromJson(body, new TypeToken<Map<String, Object>>(){}.getType());
            String widgetId = (String) data.get("id");
            Object value = data.get("value");
            
            if (widgetId != null && value != null) {
                sm.updateState(sid, widgetId, value);
            }
            render(sid, resp);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }

    private void render(String sid, HttpServletResponse resp) throws IOException {
        UIApp app = hotReloadService.getApp();
        UIContext ui = new UIContext(sid, sm);
        try { app.run(ui); } 
        catch (Exception e) { ui.title("Runtime Error"); ui.info(e.getMessage()); }
        resp.setContentType("text/html");
        resp.getWriter().write(ui.getHtml());
    }
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/UIApp.java": r"""package it.my.framework;
public interface UIApp {
    void run(UIContext ui);
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/SessionManager.java": r"""package it.my.framework;
import java.util.Map;
public interface SessionManager {
    Map<String, Object> getState(String sessionId);
    void updateState(String sessionId, String widgetId, Object value);
    void removeSession(String sessionId);
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/InMemorySessionManager.java": r"""package it.my.framework;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class InMemorySessionManager implements SessionManager {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> sessions = new ConcurrentHashMap<>();
    @Override
    public Map<String, Object> getState(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
    }
    @Override
    public void updateState(String sessionId, String widgetId, Object value) {
        getState(sessionId).put(widgetId, value);
    }
    @Override
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/UIContext.java": r"""package it.my.framework;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AGGIORNATO: Supporto Sidebar Multi-View e layout Split.
 */
public class UIContext {
    private final StringBuilder mainBuffer = new StringBuilder();
    private final StringBuilder sidebarBuffer = new StringBuilder();
    private boolean isSidebarMode = false;
    
    private final String sessionId;
    private final SessionManager sessionManager;
    private final Map<String, Object> state;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    public UIContext(String sessionId, SessionManager sessionManager) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        this.state = sessionManager.getState(sessionId);
    }
    
    /**
     * Esegue il blocco di codice in modalità Sidebar.
     * I widget definiti all'interno verranno renderizzati nella colonna sinistra.
     */
    public void sidebar(Runnable block) {
        isSidebarMode = true;
        block.run();
        isSidebarMode = false;
    }
    
    // Helper per recuperare/aggiornare stato manualmente (utile per navigazione)
    @SuppressWarnings("unchecked")
    public <T> T getState(String key, T defaultValue) {
        Object val = state.get(key);
        if (val == null) return defaultValue;
        return (T) val;
    }
    
    public void updateState(String key, Object value) {
        sessionManager.updateState(sessionId, key, value);
    }

    private String getNextWidgetId() { return "widget-" + widgetCounter.getAndIncrement(); }
    
    private int getInt(String widgetId, int defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        if (value instanceof Number) return ((Number) value).intValue();
        return defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getValue(String widgetId, T defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        return (T) value;
    }

    public void addHtml(String html) { 
        if (isSidebarMode) {
            sidebarBuffer.append(html).append("\n");
        } else {
            mainBuffer.append(html).append("\n");
        }
    }
    
    // Genera il layout finale: Sidebar (se esiste) + Main Content
    public String getHtml() { 
        String sidebarContent = sidebarBuffer.toString();
        String mainContent = mainBuffer.toString();
        
        if (sidebarContent.trim().isEmpty()) {
            return "<div class='p-6 fade-in'>" + mainContent + "</div>";
        } else {
            return String.format(
                "<div class='flex flex-col md:flex-row min-h-[80vh]'>" +
                "  <aside class='w-full md:w-64 bg-gray-50 dark:bg-gray-800/50 border-r border-gray-200 dark:border-gray-700 p-4 flex-shrink-0'>" +
                "    %s" +
                "  </aside>" +
                "  <main class='flex-1 p-6 fade-in'>" +
                "    %s" +
                "  </main>" +
                "</div>", 
                sidebarContent, mainContent
            );
        }
    }

    public void title(String t) { 
        String cls = isSidebarMode ? "text-xl font-bold mb-4 text-indigo-700 dark:text-indigo-400" : "text-3xl font-bold mb-4 text-indigo-700 dark:text-indigo-400";
        addHtml("<h1 class='" + cls + "'>" + t + "</h1>"); 
    }
    public void header(String t) { 
        String cls = isSidebarMode ? "text-lg font-semibold mb-2 mt-4 text-gray-800 dark:text-gray-200" : "text-2xl font-semibold mb-3 mt-6 text-gray-800 dark:text-gray-200";
        addHtml("<h2 class='" + cls + "'>" + t + "</h2>"); 
    }
    public void text(String t) { 
        addHtml("<p class='mb-2 text-gray-600 dark:text-gray-300 " + (isSidebarMode ? "text-sm" : "") + "'>" + t + "</p>"); 
    }
    public void info(String m) { 
        addHtml("<div class='p-3 rounded-md bg-blue-50 dark:bg-blue-900/30 border border-blue-400 dark:border-blue-700 text-blue-800 dark:text-blue-300 text-sm mb-4'>" + m + "</div>"); 
    }

    public String textInput(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='text' value='%s' onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 dark:text-white shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500' /></div>", 
            label, val, id));
        return val;
    }
    
    public int slider(String label, int min, int max, int def) {
        String id = getNextWidgetId();
        int val = getInt(id, def); 
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s: <span id='%s-val'>%d</span></label>" +
            "<input type='range' min='%d' max='%d' value='%d' oninput=\"document.getElementById('%s-val').innerText=this.value\" onchange=\"sendUpdate('%s', parseInt(this.value))\" " +
            "class='mt-1 block w-full h-2 bg-gray-200 dark:bg-gray-600 rounded-lg appearance-none cursor-pointer' /></div>", 
            label, id, val, min, max, val, id, id));
        return val;
    }
    
    public boolean button(String label) {
        String id = getNextWidgetId();
        boolean clicked = getValue(id, false);
        if (clicked) { sessionManager.updateState(sessionId, id, false); return true; }
        addHtml(String.format(
            "<div class='mb-4'><button onclick=\"sendUpdate('%s', true)\" " +
            "class='w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-700 dark:bg-indigo-500 dark:hover:bg-indigo-600 text-white rounded-md transition text-sm font-medium shadow-sm'>%s</button></div>", 
            id, label));
        return false;
    }
    
    public boolean checkbox(String label, boolean def) {
        String id = getNextWidgetId();
        boolean val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4 flex items-center'><input type='checkbox' %s onclick=\"sendUpdate('%s', this.checked)\" " +
            "class='h-4 w-4 text-indigo-600 border-gray-300 dark:border-gray-600 dark:bg-gray-700 rounded'><label class='ml-2 text-sm text-gray-900 dark:text-gray-300'>%s</label></div>", 
            val ? "checked" : "", id, label));
        return val;
    }
    
    public String selectBox(String label, List<String> opts, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        StringBuilder sb = new StringBuilder();
        for(String o : opts) sb.append(String.format("<option value='%s' %s>%s</option>", o, o.equals(val)?"selected":"", o));
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<select onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</select></div>", 
            label, id, sb.toString()));
        return val;
    }
    
    public String datePicker(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='date' value='%s' onchange=\"sendUpdate('%s', this.value)\" " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white' /></div>", 
            label, val, id));
        return val;
    }
    
    public String textarea(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<textarea onchange=\"sendUpdate('%s', this.value)\" rows='4' " +
            "class='mt-1 block w-full border border-gray-300 dark:border-gray-600 p-2 rounded-md bg-white dark:bg-gray-700 dark:text-white'>%s</textarea></div>", 
            label, id, val));
        return val;
    }
    
    public String fileUpload(String label) {
        String id = getNextWidgetId();
        String val = getValue(id, "Nessun file");
        addHtml(String.format(
            "<div class='mb-4'><label class='block text-sm font-medium text-gray-700 dark:text-gray-300'>%s</label>" +
            "<input type='file' disabled class='mt-1 block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 dark:file:bg-indigo-900 dark:file:text-indigo-300 hover:file:bg-indigo-100' />" +
            "<p class='text-xs text-gray-500 dark:text-gray-400'>Stato: %s</p></div>", 
            label, val));
        return val;
    }
}
""",

    "streamlit-like-jetty/src/main/resources/static/index.html": r"""<!DOCTYPE html>
<html class="h-full">
<head>
    <title>Java Streamlit (CLI)</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            darkMode: 'class',
        }
    </script>
    <style>
        .console-scroll::-webkit-scrollbar { width: 8px; }
        .console-scroll::-webkit-scrollbar-track { background: #1f2937; }
        .console-scroll::-webkit-scrollbar-thumb { background: #4b5563; border-radius: 4px; }
        .fade-in { animation: fadeIn 0.3s ease-in; }
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
    </style>
</head>
<body class="bg-gray-100 dark:bg-gray-900 h-full transition-colors duration-200">
    
    <div class="fixed top-4 left-4 z-50">
        <button id="menu-btn" onclick="toggleMenu()" class="p-2 bg-white dark:bg-gray-800 rounded-full shadow-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-gray-700 dark:text-gray-200">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
            </svg>
        </button>

        <div id="menu-dropdown" class="hidden absolute top-12 left-0 w-56 bg-white dark:bg-gray-800 rounded-lg shadow-xl border border-gray-200 dark:border-gray-700 overflow-hidden transform transition-all origin-top-left">
            <div class="p-1">
                <button onclick="toggleTheme()" class="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 rounded flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4 mr-2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M21.752 15.002A9.718 9.718 0 0118 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.753 9.753 0 003 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 009.002-5.998z" />
                    </svg>
                    Toggle Dark/Light
                </button>
                <button onclick="deploySim()" class="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 rounded flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4 mr-2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15.59 14.37a6 6 0 01-5.84 7.38v-4.8m5.84-2.58a14.98 14.98 0 006.16-12.12A14.98 14.98 0 009.631 8.41m5.96 5.96a14.926 14.926 0 01-5.841 2.58m-.119-8.54a6 6 0 00-7.381 5.84h4.8m2.581-5.84a14.927 14.927 0 00-2.58 5.84m2.699 2.7c-.103.021-.207.041-.311.06a15.09 15.09 0 01-2.448-2.448 14.9 14.9 0 01.06-.312m-2.24 2.39a4.493 4.493 0 00-1.757 4.306 4.493 4.493 0 004.306-1.758M16.5 9a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
                    </svg>
                    Deploy to Cloud
                </button>
                <button onclick="toggleConsole()" class="w-full text-left px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 rounded flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-4 h-4 mr-2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 7.5l3 2.25-3 2.25m4.5 0h3m-9 8.25h13.5A2.25 2.25 0 0021 18V6a2.25 2.25 0 00-2.25-2.25H5.25A2.25 2.25 0 003 6v12a2.25 2.25 0 002.25 2.25z" />
                    </svg>
                    Show Console
                </button>
            </div>
        </div>
    </div>

    <!-- AGGIORNATO: Rimosso max-w e aggiunto w-full per supportare Sidebar full-width -->
    <div id="app-container" class="w-full mx-auto bg-white dark:bg-gray-800 shadow-xl mt-12 mb-32 transition-colors duration-200 min-h-[50vh]">
        <div class="text-center text-gray-500 dark:text-gray-400 mt-10">
            <div class="animate-spin inline-block w-8 h-8 border-4 border-current border-t-transparent text-indigo-600 rounded-full" role="status" aria-label="loading"></div>
            <p class="mt-2">Connecting to server...</p>
        </div>
    </div>

    <div id="console-panel" class="fixed bottom-0 left-0 w-full h-48 bg-gray-900 text-green-400 font-mono text-xs p-4 hidden border-t border-gray-700 shadow-2xl z-40 flex flex-col">
        <div class="flex justify-between items-center mb-2 border-b border-gray-700 pb-1">
            <span class="font-bold text-gray-300">System Console</span>
            <button onclick="toggleConsole()" class="text-gray-400 hover:text-white">✕</button>
        </div>
        <div id="console-logs" class="flex-1 overflow-y-auto console-scroll space-y-1">
            <div class="opacity-50">[SYSTEM] Console ready.</div>
        </div>
    </div>

    <div id="toast" class="fixed bottom-4 right-4 bg-gray-800 text-white px-4 py-2 rounded shadow-lg transform translate-y-20 opacity-0 transition-all duration-300 z-50">
        Notification
    </div>

    <script>
        const container = document.getElementById('app-container');
        let sid = sessionStorage.getItem('sid');
        if (!sid) {
            sid = crypto.randomUUID();
            sessionStorage.setItem('sid', sid);
        }

        function initTheme() {
            if (localStorage.getItem('theme') === 'dark' || (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
                document.documentElement.classList.add('dark');
            } else {
                document.documentElement.classList.remove('dark');
            }
        }
        function toggleTheme() {
            if (document.documentElement.classList.contains('dark')) {
                document.documentElement.classList.remove('dark');
                localStorage.setItem('theme', 'light');
            } else {
                document.documentElement.classList.add('dark');
                localStorage.setItem('theme', 'dark');
            }
            toggleMenu(); 
        }

        function toggleMenu() {
            document.getElementById('menu-dropdown').classList.toggle('hidden');
        }
        function toggleConsole() {
            document.getElementById('console-panel').classList.toggle('hidden');
            logToConsole("[UI] Console visibility toggled.");
            toggleMenu(); 
        }
        function showToast(msg) {
            const t = document.getElementById('toast');
            t.innerText = msg;
            t.classList.remove('translate-y-20', 'opacity-0');
            setTimeout(() => t.classList.add('translate-y-20', 'opacity-0'), 3000);
        }
        function deploySim() {
            toggleMenu();
            showToast("Deploying to cloud...");
            logToConsole("[DEPLOY] Starting deployment sequence...");
            setTimeout(() => {
                logToConsole("[DEPLOY] Uploading assets...");
                setTimeout(() => {
                    logToConsole("[DEPLOY] Success! App available at https://cloud.myapp.com");
                    showToast("🚀 Deployed successfully!");
                }, 1500);
            }, 1000);
        }
        function logToConsole(msg) {
            const logs = document.getElementById('console-logs');
            const entry = document.createElement('div');
            entry.innerText = `> ${msg}`;
            logs.appendChild(entry);
            logs.scrollTop = logs.scrollHeight;
        }

        async function sendUpdate(id, val) {
            logToConsole(`[EVENT] Widget ${id} changed to: ${val}`);
            try {
                const r = await fetch('/ui?sessionId=' + sid, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ id: id, value: val })
                });
                if (r.ok) {
                    container.innerHTML = await r.text();
                    logToConsole("[SERVER] UI Updated received.");
                } else {
                    logToConsole("[ERROR] Server responded with " + r.status);
                }
            } catch (e) { 
                console.error(e); 
                logToConsole("[NETWORK ERROR] " + e.message);
            }
        }
        
        async function load() {
            initTheme();
            logToConsole("[INIT] Connecting to Session " + sid);
            try {
                const r = await fetch('/ui?sessionId=' + sid);
                if (r.ok) {
                    container.innerHTML = await r.text();
                    logToConsole("[INIT] App loaded successfully.");
                } else {
                    container.innerHTML = "<div class='text-center text-red-500'>Server error.</div>";
                }
            } catch (e) { 
                container.innerHTML = "<div class='text-center text-red-500'>Server unreachable.</div>";
            }
        }

        window.sendUpdate = sendUpdate;
        window.onload = load;
        
        document.addEventListener('click', function(event) {
            const menu = document.getElementById('menu-dropdown');
            const btn = document.getElementById('menu-btn');
            if (!menu.contains(event.target) && !btn.contains(event.target) && !menu.classList.contains('hidden')) {
                menu.classList.add('hidden');
            }
        });
    </script>
</body>
</html>
"""
}

def create_project_zip():
    print("Inizio creazione dei progetti...")
    for filepath, content in files.items():
        directory = os.path.dirname(filepath)
        if directory and not os.path.exists(directory): os.makedirs(directory)
        with open(filepath, 'w', encoding='utf-8') as f: f.write(content.strip())
        print(f"Creato: {filepath}")
    
    zip_filename = "projects_bundle.zip"
    print(f"\nCompressione in {zip_filename}...")
    with zipfile.ZipFile(zip_filename, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for filepath in files.keys(): zipf.write(filepath)
    print(f"\nSuccesso! Archivio creato: {os.path.abspath(zip_filename)}")

if __name__ == "__main__":
    create_project_zip()