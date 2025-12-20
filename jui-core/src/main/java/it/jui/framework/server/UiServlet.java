package it.jui.framework.server;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import it.jui.framework.core.AppProvider;
import it.jui.framework.core.UIApp;
import it.jui.framework.core.UIContext;
import it.jui.framework.session.SessionManager;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class UiServlet extends HttpServlet {

    private final SessionManager sessionManager;
    private final AppProvider appProvider;
    private Gson gson = new Gson();

    public UiServlet(SessionManager sessionManager, AppProvider appProvider) {
        this.sessionManager = sessionManager;
        this.appProvider = appProvider;
        gson = new GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("sessionId");
        if (sid == null) sid = req.getSession(true).getId();
        render(sid, resp, true);
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
            render(sid, resp, false);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }

    private void render(String sessionId, HttpServletResponse resp, boolean fullPage) throws IOException {

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
        UiResponse uiResponse = new UiResponse(ui.getHtml(), ui.getHtmlDependencies(), fullPage);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(uiResponse));
    }

    private record UiResponse(String html, Map<String, String> htmlDependencies, boolean fullPage) { }

}