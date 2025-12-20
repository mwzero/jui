package it.jui.framework.server;

import it.jui.cli.HotReloadService;
import it.jui.framework.core.AppProvider;
import it.jui.framework.core.UIApp;
import it.jui.framework.core.UIContext;
import it.jui.framework.session.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class UiServlet extends HttpServlet {

    private final SessionManager sessionManager;
    private final AppProvider appProvider;
    private final Gson gson = new Gson();

    public UiServlet(SessionManager sessionManager, AppProvider appProvider) {
        this.sessionManager = sessionManager;
        this.appProvider = appProvider;
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
                sessionManager.updateState(sid, widgetId, value);
            }
            render(sid, resp);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }

    private void render(String sessionId, HttpServletResponse resp) throws IOException {

        // Ottiene l'istanza aggiornata di UIApp. Utile nel caso sia usato hot-reload da file
        UIApp app = appProvider.getApp();

        // Crea il contesto UI 
        UIContext ui = new UIContext(sessionId, sessionManager);

        // Esegue l'applicazione con gestione degli errori
        try { app.run(ui); } 
        catch (Exception e) { 
            ui.title("Runtime Error"); 
            ui.info(e.getMessage()); 
        }
        resp.setContentType("text/html");
        resp.getWriter().write(ui.getHtml());
    }
}