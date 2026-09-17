package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;

class InputElementsTest {

    @Test
    void radioSelectColorAndDateReadCurrentSessionValues() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        sessions.updateState("s1", probe.getNextWidgetId("radio:Size"), "L");
        sessions.updateState("s1", probe.getNextWidgetId("select:Country"), "Italy");
        sessions.updateState("s1", probe.getNextWidgetId("color:Accent"), "#112233");
        sessions.updateState("s1", probe.getNextWidgetId("date:Start"), "2026-09-16");

        UIContext ui = new UIContext("s1", sessions);
        assertEquals("L", ui.radio("Size", List.of("S", "M", "L"), "M"));
        assertEquals("Italy", ui.select("Country", List.of("France", "Italy"), "France"));
        assertEquals("#112233", ui.colorPicker("Accent", "#ffffff"));
        assertEquals("2026-09-16", ui.dateInput("Start", "2026-01-01"));
        assertTrue(ui.getHtml().contains("checked"));
        assertTrue(ui.getHtml().contains("selected"));
    }

    @Test
    void multiCheckboxUsesDefaultsAndThenBrowserState() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext first = new UIContext("s1", sessions);
        assertEquals(List.of("Java"), first.multiCheckbox("Tags", List.of("Java", "AI"), List.of("Java")));

        UIContext probe = new UIContext("s1", sessions);
        String id = probe.getNextWidgetId("multi-checkbox:Tags");
        sessions.updateState("s1", id, List.of("AI"));

        assertEquals(List.of("AI"), new UIContext("s1", sessions)
                .multiCheckbox("Tags", List.of("Java", "AI"), List.of("Java")));
    }

    @Test
    void emptyChoiceCollectionsReturnEmptyString() {
        UIContext ui = new UIContext("s1", new InMemorySessionManager());
        assertEquals("", ui.radio("Empty", List.of(), null));
        assertEquals("", ui.select("Empty", List.of(), null));
    }

    @Test
    void fileUploaderConvertsStructuredBrowserPayload() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        String id = probe.getNextWidgetId("file:Document");
        sessions.updateState("s1", id, Map.of(
                "name", "hello.txt",
                "contentType", "text/plain",
                "size", 5L,
                "base64", "aGVsbG8="));

        var file = new UIContext("s1", sessions).fileUploader("Document").orElseThrow();
        assertEquals("hello.txt", file.name());
        assertEquals("text/plain", file.contentType());
        assertEquals(5L, file.size());
        assertEquals("hello", new String(file.bytes(), StandardCharsets.UTF_8));
    }
}
