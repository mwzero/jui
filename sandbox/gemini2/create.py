import os
import zipfile

# Definizione dei contenuti dei file
files = {
    "README.md": r"""# Java Streamlit-like Framework (HTTP/AJAX Version)

Questo archivio contiene due progetti Maven distinti.

## 1. Avvio del Framework Core (`streamlit-like-jetty`)

Simula Streamlit usando chiamate HTTP AJAX (No WebSocket).

1.  Compila: `mvn clean install`
2.  Avvia: `java -jar target/streamlit-like-jetty-1.0-SNAPSHOT.jar`
3.  **Browser:** `http://localhost:8080/`

## 2. Avvio del Modulo Data App (`duckdb-pandas-like`)

Data App con DuckDB.

1.  Compila: `mvn clean install`
2.  Avvia: `java -jar target/duckdb-pandas-like-1.0-SNAPSHOT.jar`
3.  **Browser:** `http://localhost:8081/data_index.html`

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
        <slf4j.version>2.0.7</slf4j.version>
    </properties>

    <dependencies>
        <!-- Jetty Server Core (No WebSocket) -->
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
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-http</artifactId>
            <version>${jetty.version}</version>
        </dependency>
        
        <!-- JSON Processing -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
        </dependency>
        <!-- Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        
        <!-- Session Managers Deps -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>5.0.0</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>
    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>it.my.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <overWriteReleases>false</overWriteReleases>
                            <overWriteSnapshots>false</overWriteSnapshots>
                            <overWriteIfNewer>true</overWriteIfNewer>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
""",

    "streamlit-like-jetty/src/main/java/it/my/Main.java": r"""package it.my;

import it.my.app.MyApp;
import it.my.framework.UIApp;
import it.my.framework.SessionManager;
import it.my.framework.InMemorySessionManager;
import it.my.framework.JdbcSessionManager;
import it.my.framework.RedisSessionManager;
import it.my.framework.server.UiServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

import java.net.URL;

public class Main {
    
    private static final String SESSION_TYPE = "IN_MEMORY"; 

    public static void main(String[] args) throws Exception {
        
        final SessionManager sessionManager;

        switch (SESSION_TYPE) {
            case "JDBC":
                System.out.println("Avvio con JDBC Session Manager (H2)...");
                sessionManager = new JdbcSessionManager("jdbc:h2:./framework_store", "sa", "");
                break;
            case "REDIS":
                System.out.println("Avvio con Redis Session Manager...");
                sessionManager = new RedisSessionManager("localhost", 6379);
                break;
            case "IN_MEMORY":
            default:
                System.out.println("Avvio con In-Memory Session Manager...");
                sessionManager = new InMemorySessionManager();
                break;
        }
        
        final UIApp application = new MyApp();

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // --- CONFIGURAZIONE RISORSE STATICHE ---
        URL staticResources = Main.class.getResource("/static");
        if (staticResources == null) {
            System.err.println("ERRORE: Cartella 'static' non trovata nel classpath!");
            return;
        }
        String resourceBase = staticResources.toExternalForm();
        if (!resourceBase.endsWith("/")) {
            resourceBase += "/";
        }
        
        context.setResourceBase(resourceBase);
        context.setWelcomeFiles(new String[]{"index.html"});

        server.setHandler(context);

        // 1. DefaultServlet per file statici (JS, CSS, HTML)
        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("dirAllowed", "true");
        context.addServlet(staticHolder, "/");

        // 2. UiServlet per gestire sia il rendering iniziale (GET) che gli aggiornamenti AJAX (POST)
        ServletHolder uiServletHolder = new ServletHolder("uiServlet", new UiServlet(sessionManager, application));
        context.addServlet(uiServletHolder, "/ui");

        System.out.println("---------------------------------------------------------");
        System.out.println(" Framework Streamlit-like (HTTP/AJAX) avviato su http://localhost:8080");
        System.out.println(" Modalità Sessione: " + SESSION_TYPE);
        System.out.println("---------------------------------------------------------");

        server.start();
        server.join();
    }
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

    "streamlit-like-jetty/src/main/java/it/my/framework/JdbcSessionManager.java": r"""package it.my.framework;
import com.google.gson.Gson;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcSessionManager implements SessionManager {
    private final String jdbcUrl;
    private final String user;
    private final String password;
    private final Gson gson = new Gson();

    public JdbcSessionManager(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        initDb();
    }
    private void initDb() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS SESSIONS (session_id VARCHAR(255), widget_id VARCHAR(255), value_json CLOB, value_type VARCHAR(255), PRIMARY KEY (session_id, widget_id))";
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public Map<String, Object> getState(String sessionId) {
        Map<String, Object> state = new ConcurrentHashMap<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement("SELECT widget_id, value_json, value_type FROM SESSIONS WHERE session_id = ?")) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        state.put(rs.getString("widget_id"), gson.fromJson(rs.getString("value_json"), Class.forName(rs.getString("value_type"))));
                    } catch (Exception e) {}
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return state;
    }
    @Override
    public void updateState(String sessionId, String widgetId, Object value) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement("MERGE INTO SESSIONS KEY (session_id, widget_id) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, sessionId);
            ps.setString(2, widgetId);
            ps.setString(3, gson.toJson(value));
            ps.setString(4, value.getClass().getName());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public void removeSession(String sessionId) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement("DELETE FROM SESSIONS WHERE session_id = ?")) {
            ps.setString(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/RedisSessionManager.java": r"""package it.my.framework;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisSessionManager implements SessionManager {
    private final JedisPool jedisPool;
    private final Gson gson = new Gson();
    public RedisSessionManager(String host, int port) { this.jedisPool = new JedisPool(host, port); }
    @Override
    public Map<String, Object> getState(String sessionId) {
        Map<String, Object> state = new ConcurrentHashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> rawData = jedis.hgetAll("session:" + sessionId);
            for (Map.Entry<String, String> entry : rawData.entrySet()) {
                String val = entry.getValue();
                int sep = val.indexOf('|');
                if (sep > 0) {
                    state.put(entry.getKey(), gson.fromJson(val.substring(sep + 1), Class.forName(val.substring(0, sep))));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return state;
    }
    @Override
    public void updateState(String sessionId, String widgetId, Object value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset("session:" + sessionId, widgetId, value.getClass().getName() + "|" + gson.toJson(value));
            jedis.expire("session:" + sessionId, 3600);
        } catch (Exception e) { e.printStackTrace(); }
    }
    @Override
    public void removeSession(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) { jedis.del("session:" + sessionId); }
    }
}
""",

    "streamlit-like-jetty/src/main/java/it/my/framework/UIApp.java": "package it.my.framework; public interface UIApp { void run(UIContext ui); }",

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
    
    // Metodo generico (con cast non controllato, usato solo dove il tipo è certo)
    @SuppressWarnings("unchecked")
    private <T> T getValue(String widgetId, T defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) { state.put(widgetId, defaultValue); return defaultValue; }
        return (T) value;
    }
    
    // FIX: Metodo specifico per Integer che gestisce la conversione Double -> int
    // Necessario perché Gson deserializza i numeri come Double
    private int getInt(String widgetId, int defaultValue) {
        Object value = state.get(widgetId);
        if (value == null) {
            state.put(widgetId, defaultValue);
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
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
        // USIAMO getInt() invece di getValue()
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

    "streamlit-like-jetty/src/main/java/it/my/framework/server/UiServlet.java": r"""package it.my.framework.server;

import it.my.framework.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class UiServlet extends HttpServlet {
    private final SessionManager sm;
    private final UIApp app;
    private final Gson gson = new Gson();

    public UiServlet(SessionManager sm, UIApp app) { this.sm = sm; this.app = app; }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("sessionId");
        if (sid == null) sid = req.getSession(true).getId();

        String html = renderApp(sid);
        
        resp.setContentType("text/html");
        resp.getWriter().write(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("sessionId");
        if (sid == null) {
            resp.setStatus(400);
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            Map<String, Object> data = gson.fromJson(body, new TypeToken<Map<String, Object>>(){}.getType());
            String widgetId = (String) data.get("id");
            Object value = data.get("value");

            if (widgetId != null && value != null) {
                sm.updateState(sid, widgetId, value);
            }

            String html = renderApp(sid);
            resp.setContentType("text/html");
            resp.getWriter().write(html);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Errore: " + e.getMessage());
        }
    }

    private String renderApp(String sessionId) {
        UIContext ui = new UIContext(sessionId, sm);
        app.run(ui);
        return ui.getHtml();
    }
}
""",

    "streamlit-like-jetty/src/main/java/it/my/app/MyApp.java": r"""package it.my.app;
import it.my.framework.*;
import java.util.Arrays;
public class MyApp implements UIApp {
    @Override
    public void run(UIContext ui) {
        ui.title("Java Streamlit (HTTP/AJAX)");
        String name = ui.textInput("Nome", "Ospite");
        if(ui.button("Saluta")) ui.info("Ciao " + name + "! (Via HTTP POST)");
        ui.slider("Valore", 0, 100, 50);
        ui.datePicker("Data", "2025-01-01");
        ui.selectBox("Scelta", Arrays.asList("A","B","C"), "A");
    }
}
""",

    "streamlit-like-jetty/src/main/resources/static/index.html": r"""<!DOCTYPE html>
<html>
<head>
    <title>Java Streamlit (AJAX)</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 p-8">
    <div id="app-container" class="max-w-4xl mx-auto p-6 bg-white shadow-xl rounded-xl">
        <div class="text-center text-gray-500">Caricamento App...</div>
    </div>
    <script>
        const container = document.getElementById('app-container');
        // Genera o recupera Session ID
        let sid = localStorage.getItem('sid');
        if (!sid) {
            sid = crypto.randomUUID();
            localStorage.setItem('sid', sid);
        }

        // Funzione di aggiornamento via Fetch API (AJAX)
        async function sendUpdate(id, val) {
            try {
                // Invia POST al servlet /ui
                const response = await fetch('/ui?sessionId=' + sid, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ id: id, value: val })
                });
                
                if (response.ok) {
                    const html = await response.text();
                    container.innerHTML = html; // Sostituisci il DOM
                } else {
                    console.error("Errore server:", response.status);
                }
            } catch (e) {
                console.error("Errore di rete:", e);
            }
        }

        // Caricamento iniziale
        async function loadInitial() {
            try {
                const response = await fetch('/ui?sessionId=' + sid);
                if (response.ok) {
                    container.innerHTML = await response.text();
                }
            } catch (e) {
                container.innerHTML = "Errore di connessione al server.";
            }
        }

        window.sendUpdate = sendUpdate;
        window.onload = loadInitial;
    </script>
</body>
</html>
""",

    # --- DUCKDB PROJECT FILES (UNCHANGED) ---

    "duckdb-pandas-like/pom.xml": r"""<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>it.my.data</groupId>
    <artifactId>duckdb-pandas-like</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    <dependencies>
        <dependency><groupId>org.duckdb</groupId><artifactId>duckdb_jdbc</artifactId><version>0.10.2</version></dependency>
        <dependency><groupId>commons-fileupload</groupId><artifactId>commons-fileupload</artifactId><version>1.5</version></dependency>
        <dependency><groupId>org.eclipse.jetty</groupId><artifactId>jetty-server</artifactId><version>11.0.20</version></dependency>
        <dependency><groupId>org.eclipse.jetty</groupId><artifactId>jetty-servlet</artifactId><version>11.0.20</version></dependency>
        <dependency><groupId>org.slf4j</groupId><artifactId>slf4j-simple</artifactId><version>2.0.7</version></dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin><artifactId>maven-compiler-plugin</artifactId><version>3.11.0</version></plugin>
            <plugin><artifactId>maven-jar-plugin</artifactId><version>3.3.0</version><configuration><archive><manifest><addClasspath>true</addClasspath><classpathPrefix>lib/</classpathPrefix><mainClass>it.my.data.DataAppMain</mainClass></manifest></archive></configuration></plugin>
            <plugin><artifactId>maven-dependency-plugin</artifactId><version>3.6.0</version><executions><execution><phase>package</phase><goals><goal>copy-dependencies</goal></goals><configuration><outputDirectory>${project.build.directory}/lib</outputDirectory></configuration></execution></executions></plugin>
        </plugins>
    </build>
</project>
""",

    "duckdb-pandas-like/src/main/java/it/my/data/DuckDataFrame.java": r"""package it.my.data;
import java.sql.*;
import java.util.*;
public class DuckDataFrame {
    private Connection c;
    public DuckDataFrame() {
        try { Class.forName("org.duckdb.DuckDBDriver"); c = DriverManager.getConnection("jdbc:duckdb::memory:"); c.createStatement().execute("INSTALL 'csv'; LOAD 'csv';"); }
        catch(Exception e) { e.printStackTrace(); }
    }
    public void registerCsv(String path, String tbl) throws SQLException {
        c.createStatement().execute(String.format("CREATE OR REPLACE TABLE %s AS SELECT * FROM read_csv_auto('%s')", tbl, path.replace("\\", "/")));
    }
    public String query(String sql) throws SQLException {
        try(ResultSet rs = c.createStatement().executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            StringBuilder html = new StringBuilder("<table class='min-w-full border'><thead><tr class='bg-gray-200'>");
            for(int i=1;i<=cols;i++) html.append("<th class='p-2'>").append(md.getColumnLabel(i)).append("</th>");
            html.append("</tr></thead><tbody>");
            while(rs.next()) {
                html.append("<tr>");
                for(int i=1;i<=cols;i++) html.append("<td class='p-2 border'>").append(rs.getString(i)).append("</td>");
                html.append("</tr>");
            }
            return html.append("</tbody></table>").toString();
        }
    }
    public void close() { try { if(c!=null) c.close(); } catch(Exception e){} }
}
""",

    "duckdb-pandas-like/src/main/java/it/my/data/server/DataAppServlet.java": r"""package it.my.data.server;
import it.my.data.DuckDataFrame;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataAppServlet extends HttpServlet {
    private final DuckDataFrame df;
    private final String uploadDir;
    public DataAppServlet(DuckDataFrame df) {
        this.df = df;
        this.uploadDir = System.getProperty("java.io.tmpdir") + "/data_uploads";
        new File(uploadDir).mkdirs();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getSession(true).getId();
        if (ServletFileUpload.isMultipartContent(req)) {
            try {
                for(FileItem item : new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req)) {
                    if(!item.isFormField() && "csvFile".equals(item.getFieldName())) {
                        File f = new File(uploadDir, sid + "_" + item.getName());
                        item.write(f);
                        df.registerCsv(f.getAbsolutePath(), "uploaded_data");
                        resp.getWriter().write("OK");
                        return;
                    }
                }
            } catch(Exception e) { resp.setStatus(500); resp.getWriter().write(e.getMessage()); }
        } else {
            try {
                resp.getWriter().write(df.query(req.getParameter("query")));
            } catch(Exception e) { resp.getWriter().write("Error: " + e.getMessage()); }
        }
    }
}
""",

    "duckdb-pandas-like/src/main/java/it/my/data/DataAppMain.java": r"""package it.my.data;
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
""",

    "duckdb-pandas-like/src/main/resources/static/data_index.html": r"""<!DOCTYPE html>
<html>
<head><title>Data App</title><script src="https://cdn.tailwindcss.com"></script></head>
<body class="bg-gray-50 p-8">
    <div class="max-w-4xl mx-auto">
        <h1 class="text-3xl font-bold mb-4">DuckDB Data App</h1>
        <div class="mb-6 p-4 bg-white rounded shadow">
            <h2 class="text-xl font-semibold mb-2">1. Upload CSV</h2>
            <input type="file" id="f" class="mb-2"><button onclick="upl()" class="bg-green-600 text-white px-4 py-2 rounded">Upload</button>
            <div id="status" class="mt-2 text-sm"></div>
        </div>
        <div class="p-4 bg-white rounded shadow">
            <h2 class="text-xl font-semibold mb-2">2. Query</h2>
            <textarea id="q" class="w-full border p-2" rows="3">SELECT * FROM uploaded_data LIMIT 5</textarea>
            <button onclick="run()" class="mt-2 bg-indigo-600 text-white px-4 py-2 rounded">Run</button>
            <div id="res" class="mt-4 overflow-auto"></div>
        </div>
    </div>
    <script>
        async function upl() {
            let fd = new FormData(); fd.append('csvFile', document.getElementById('f').files[0]);
            let r = await fetch('/data/upload', {method:'POST', body:fd});
            document.getElementById('status').innerText = await r.text();
        }
        async function run() {
            let q = document.getElementById('q').value;
            let r = await fetch('/data/query', {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body:'query='+encodeURIComponent(q)});
            document.getElementById('res').innerHTML = await r.text();
        }
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