package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class NavigationElementsTest {

    @Test
    void tabsUseDefaultAndPersistedSelection() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        assertEquals("Home", new UIContext("s1", sessions)
                .tabs("Main", List.of("Home", "Settings"), "Home"));

        UIContext probe = new UIContext("s1", sessions);
        String id = probe.getNextWidgetId("tabs:Main");
        sessions.updateState("s1", id, "Settings");

        UIContext ui = new UIContext("s1", sessions);
        assertEquals("Settings", ui.tabs("Main", List.of("Home", "Settings"), "Home"));
        assertTrue(ui.getHtml().contains("Selected: Settings"));
    }

    @Test
    void sidebarRendersContentForCurrentSelection() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        sessions.updateState("s1", probe.getNextWidgetId("sidebar:Application"), "Customers");

        UIContext ui = new UIContext("s1", sessions);
        String selected = ui.sidebar("Application", List.of("Home", "Customers"), "Home",
                page -> ui.text("Page: " + page));

        assertEquals("Customers", selected);
        assertTrue(ui.getHtml().contains("Page: Customers"));
        assertTrue(ui.getHtml().contains("<aside"));
        assertTrue(ui.getHtml().contains("<main"));
    }

    @Test
    void emptyNavigationReturnsEmptySelection() {
        UIContext ui = new UIContext("s1", new InMemorySessionManager());
        assertEquals("", ui.tabs("Empty", List.of(), null));
        assertEquals("", ui.sidebar("Empty", List.of(), null));
    }
}
