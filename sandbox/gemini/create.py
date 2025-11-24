import os
import zipfile

files = {
    "README.md": r"""# Java Streamlit-like CLI

Un framework Java UI con **Hot Reloading** che preserva lo stato della sessione.

## Istruzioni

1. **Compila il Framework (una tantum):**
   ```bash
   cd streamlit-like-jetty
   mvn clean package
   ```
   Il file eseguibile sarà: `target/streamlit-like-jetty-1.0-SNAPSHOT.jar`

2. **Inizializza un progetto:**
   ```bash
   java -jar target/streamlit-like-jetty-1.0-SNAPSHOT.jar init MyApp.java
   ```

3. **Sviluppa in tempo reale:**
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
            <!-- Shade Plugin: Questo è CRUCIALE per creare il Fat JAR funzionante -->
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
        this.sourceFile = sourceFile;
    }

    public synchronized UIApp getApp() {
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
                    ui.info(errorMsg.replace("\n", "<br>"));
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

public class UIContext {
    private final StringBuilder htmlOutput = new StringBuilder();
    private final String sessionId;
    private final SessionManager sessionManager;
    private final Map<String, Object> state;
    private final AtomicInteger widgetCounter = new AtomicInteger(0);

    public UIContext(String sessionId, SessionManager sessionManager) {
        this.sessionId = sessionId;
        this.sessionManager = sessionManager;
        this.state = sessionManager.getState(sessionId);
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

    public void addHtml(String html) { htmlOutput.append(html).append("\n"); }
    public String getHtml() { return htmlOutput.toString(); }

    public void title(String t) { addHtml("<h1 class='text-3xl font-bold mb-4 text-indigo-700'>" + t + "</h1>"); }
    public void header(String t) { addHtml("<h2 class='text-2xl font-semibold mb-3 mt-6 text-gray-800'>" + t + "</h2>"); }
    public void text(String t) { addHtml("<p class='mb-2 text-gray-600'>" + t + "</p>"); }
    public void info(String m) { addHtml("<div class='p-3 rounded-md bg-blue-50 border border-blue-400 text-blue-800 text-sm mb-4'>" + m + "</div>"); }

    public String textInput(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><input type='text' value='%s' onchange=\"sendUpdate('%s', this.value)\" class='mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2' /></div>", label, val, id));
        return val;
    }
    public int slider(String label, int min, int max, int def) {
        String id = getNextWidgetId();
        int val = getInt(id, def); 
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s: <span id='%s-val'>%d</span></label><input type='range' min='%d' max='%d' value='%d' oninput=\"document.getElementById('%s-val').innerText=this.value\" onchange=\"sendUpdate('%s', parseInt(this.value))\" class='mt-1 block w-full' /></div>", label, id, val, min, max, val, id, id));
        return val;
    }
    public boolean button(String label) {
        String id = getNextWidgetId();
        boolean clicked = getValue(id, false);
        if (clicked) { sessionManager.updateState(sessionId, id, false); return true; }
        addHtml(String.format("<div class='mb-4'><button onclick=\"sendUpdate('%s', true)\" class='w-full px-4 py-2 bg-indigo-600 text-white rounded-md'>%s</button></div>", id, label));
        return false;
    }
    public boolean checkbox(String label, boolean def) {
        String id = getNextWidgetId();
        boolean val = getValue(id, def);
        addHtml(String.format("<div class='mb-4 flex items-center'><input type='checkbox' %s onclick=\"sendUpdate('%s', this.checked)\" class='h-4 w-4 text-indigo-600'><label class='ml-2 text-sm'>%s</label></div>", val ? "checked" : "", id, label));
        return val;
    }
    public String selectBox(String label, List<String> opts, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        StringBuilder sb = new StringBuilder();
        for(String o : opts) sb.append(String.format("<option value='%s' %s>%s</option>", o, o.equals(val)?"selected":"", o));
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><select onchange=\"sendUpdate('%s', this.value)\" class='mt-1 block w-full border p-2 rounded-md bg-white'>%s</select></div>", label, id, sb.toString()));
        return val;
    }
    public String datePicker(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><input type='date' value='%s' onchange=\"sendUpdate('%s', this.value)\" class='mt-1 block w-full border p-2 rounded-md' /></div>", label, val, id));
        return val;
    }
    public String textarea(String label, String def) {
        String id = getNextWidgetId();
        String val = getValue(id, def);
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><textarea onchange=\"sendUpdate('%s', this.value)\" rows='4' class='mt-1 block w-full border p-2 rounded-md'>%s</textarea></div>", label, id, val));
        return val;
    }
    public String fileUpload(String label) {
        String id = getNextWidgetId();
        String val = getValue(id, "Nessun file");
        addHtml(String.format("<div class='mb-4'><label class='block text-sm font-medium text-gray-700'>%s</label><input type='file' disabled class='mt-1 block w-full text-sm text-gray-500' /><p class='text-xs text-gray-500'>Stato: %s</p></div>", label, val));
        return val;
    }
}
""",

    "streamlit-like-jetty/src/main/resources/static/index.html": r"""<!DOCTYPE html>
<html>
<head>
    <title>Java Streamlit (CLI)</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-8">
    <div id="app-container" class="max-w-4xl mx-auto p-6 bg-white shadow-xl rounded-xl">
        <div class="text-center text-gray-500">In attesa del server...</div>
    </div>
    <script>
        const container = document.getElementById('app-container');
        let sid = sessionStorage.getItem('sid');
        if (!sid) {
            sid = crypto.randomUUID();
            sessionStorage.setItem('sid', sid);
        }

        async function sendUpdate(id, val) {
            try {
                const r = await fetch('/ui?sessionId=' + sid, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ id: id, value: val })
                });
                if (r.ok) container.innerHTML = await r.text();
            } catch (e) { console.error(e); }
        }
        
        async function load() {
            try {
                const r = await fetch('/ui?sessionId=' + sid);
                if (r.ok) container.innerHTML = await r.text();
            } catch (e) { container.innerHTML = "Server non raggiungibile."; }
        }
        window.sendUpdate = sendUpdate;
        window.onload = load;
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