package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class ListElementsTest {

    @Test
    void bulletAndNumberedListsEscapeItems() {
        UIContext ui = ui();
        ui.bullets("Languages", List.of("Java", "<AI>"));
        ui.numberedList(List.of("one", "two"));

        String html = ui.getHtml();
        assertTrue(html.contains("<ul"));
        assertTrue(html.contains("<ol"));
        assertTrue(html.contains("Languages"));
        assertTrue(html.contains("&lt;AI&gt;"));
    }

    @Test
    void dropdownButtonReadsSessionSelection() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        sessions.updateState("s1", probe.getNextWidgetId("dropdown:Export"), "JSON");

        UIContext ui = new UIContext("s1", sessions);
        assertEquals("JSON", ui.dropdownButton("Export", List.of("CSV", "JSON")));
        assertTrue(ui.getHtml().contains("Export: JSON"));
    }

    @Test
    void emptyDropdownReturnsEmptyString() {
        assertEquals("", ui().dropdownButton("Empty", List.of()));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
