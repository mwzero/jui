package it.jui.framework.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.jui.framework.auth.AuthUser;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.InMemorySessionManager;

class MigratedApisTest {

    @Test
    void textHelpersRenderSafely() {
        UIContext ui = ui();
        ui.caption("<caption>");
        ui.code("<tag>", "java");
        ui.markdown("# Title\n- **one**\n- `two`");
        ui.divider();
        String html = ui.getHtml();
        assertTrue(html.contains("&lt;caption&gt;"));
        assertTrue(html.contains("&lt;tag&gt;"));
        assertTrue(html.contains("<strong>one</strong>"));
        assertTrue(html.contains("<hr"));
    }

    @Test
    void layoutCapturesNestedContent() {
        UIContext ui = ui();
        ui.columns(() -> ui.text("A"), () -> ui.text("B"));
        ui.expander("More", () -> ui.text("Inside"));
        ui.dialog("Details", () -> ui.text("Dialog body"));
        String html = ui.getHtml();
        assertTrue(html.contains("md:grid-cols-2"));
        assertTrue(html.contains("Inside"));
        assertTrue(html.contains("<dialog"));
    }

    @Test
    void mediaAndChartsRenderDependencies() {
        UIContext ui = ui();
        ui.image("/image.png", "Image");
        ui.audio("/sound.mp3");
        ui.video("/movie.mp4");
        ui.lineChart("Revenue", List.of("Jan", "Feb"), Map.of("Revenue", List.of(1, 2)));
        assertTrue(ui.getHtml().contains("<img"));
        assertTrue(ui.getHtml().contains("<audio"));
        assertTrue(ui.getHtml().contains("ApexCharts"));
        assertTrue(ui.getHtmlDependencies().containsKey("apexcharts"));
    }

    @Test
    void inputElementsReadSessionState() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext first = new UIContext("s1", sessions);
        String radioId = first.getNextWidgetId("radio:Size");
        String multiId = first.getNextWidgetId("multi-checkbox:Tags");
        sessions.updateState("s1", radioId, "Large");
        sessions.updateState("s1", multiId, List.of("Java", "AI"));

        UIContext ui = new UIContext("s1", sessions);
        assertEquals("Large", ui.radio("Size", List.of("Small", "Large"), "Small"));
        assertEquals(List.of("Java", "AI"), ui.multiCheckbox("Tags", List.of("Java", "AI", "Web"), List.of()));
    }

    @Test
    void fileUploaderConvertsBrowserPayload() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        String id = probe.getNextWidgetId("file:Document");
        sessions.updateState("s1", id, Map.of(
                "name", "a.txt", "contentType", "text/plain", "size", 3L, "base64", "YWJj"));

        var file = new UIContext("s1", sessions).fileUploader("Document").orElseThrow();
        assertEquals("a.txt", file.name());
        assertEquals("abc", new String(file.bytes()));
    }

    @Test
    void sidebarRendersSelectedPageAndAuthUsesSession() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        sessions.updateState("s1", AuthUser.SESSION_KEY, new AuthUser("1", "a@example.com", "Ada", null));
        UIContext ui = new UIContext("s1", sessions);
        ui.sidebar("Menu", List.of("Home", "Settings"), "Home", selected -> ui.text("Page: " + selected));
        assertTrue(ui.getHtml().contains("Page: Home"));
        assertEquals("Ada", ui.authUser().orElseThrow().name());
        assertFalse(ui.logoutButton("Logout"));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
