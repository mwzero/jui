package it.jui.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import it.jui.UIContext;
import it.jui.map.MapState;
import it.jui.server.InMemorySessionManager;

class MapElementsTest {

    @Test
    void mapUsesInitialStateAndDeclaresLeafletDependencies() {
        UIContext ui = ui();
        MapState state = ui.map("Naples", 40.8518, 14.2681, 12);

        assertEquals(40.8518, state.latitude(), 0.00001);
        assertEquals(14.2681, state.longitude(), 0.00001);
        assertEquals(12, state.zoom());
        assertTrue(ui.getHtmlDependencies().containsKey("leaflet-css"));
        assertTrue(ui.getHtmlDependencies().containsKey("leaflet-js"));
        assertTrue(ui.getHtml().contains("OpenStreetMap"));
    }

    @Test
    void mapRestoresBrowserCenterAndZoomFromSession() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        String id = probe.getNextWidgetId("map:Naples");
        sessions.updateState("s1", id, Map.of(
                "latitude", 41.0,
                "longitude", 15.0,
                "zoom", 9L));

        MapState state = new UIContext("s1", sessions).map("Naples", 40.8518, 14.2681, 12);
        assertEquals(41.0, state.latitude(), 0.00001);
        assertEquals(15.0, state.longitude(), 0.00001);
        assertEquals(9, state.zoom());
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
