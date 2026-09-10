package it.jui.framework.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.jui.framework.server.InMemorySessionManager;

class UIContextTest {

    @Test
    void deterministicWidgetIdIsStable() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext first = new UIContext("s1", sessions);
        UIContext second = new UIContext("s1", sessions);

        assertEquals(first.getNextWidgetId("Name"), second.getNextWidgetId("Name"));
    }

    @Test
    void textInputReadsStateUsingTheSameWidgetKeyUsedByBrowserUpdates() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext firstRender = new UIContext("s1", sessions);
        firstRender.textInput("Name", "Guest");

        String widgetId = firstRender.getNextWidgetId("Name");
        sessions.updateState("s1", widgetId, "Maurizio");

        UIContext secondRender = new UIContext("s1", sessions);
        assertEquals("Maurizio", secondRender.textInput("Name", "Guest"));
    }

    @Test
    void buttonClickIsConsumedAfterOneRender() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext initialRender = new UIContext("s1", sessions);
        String widgetId = initialRender.getNextWidgetId("Save");

        assertFalse(initialRender.button("Save"));

        sessions.updateState("s1", widgetId, true);

        UIContext clickedRender = new UIContext("s1", sessions);
        assertTrue(clickedRender.button("Save"));

        UIContext nextRender = new UIContext("s1", sessions);
        assertFalse(nextRender.button("Save"));
    }

    @Test
    void normalTextApisEscapeHtmlWhileRawHtmlIsExplicit() {
        UIContext ui = new UIContext("s1", new InMemorySessionManager());

        ui.text("<script>alert('x')</script>");
        ui.html("<strong>trusted</strong>");

        String html = ui.getHtml();
        assertTrue(html.contains("&lt;script&gt;"));
        assertFalse(html.contains("<script>alert('x')</script>"));
        assertTrue(html.contains("<strong>trusted</strong>"));
    }
}
