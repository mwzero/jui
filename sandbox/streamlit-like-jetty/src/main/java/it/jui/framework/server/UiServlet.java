package it.jui.framework.server;

import it.jui.cli.HotReloadService;
import it.jui.framework.*;
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