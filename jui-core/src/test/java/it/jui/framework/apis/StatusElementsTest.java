package it.jui.framework.apis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.jui.framework.core.UIContext;
import it.jui.framework.server.InMemorySessionManager;

class StatusElementsTest {

    @Test
    void alertsAndSpinnerEscapeMessages() {
        UIContext ui = ui();
        ui.success("<ok>");
        ui.info("A & B");
        ui.warning("<warn>");
        ui.error("<error>");
        ui.spinner("<loading>");

        String html = ui.getHtml();
        assertTrue(html.contains("&lt;ok&gt;"));
        assertTrue(html.contains("A &amp; B"));
        assertTrue(html.contains("&lt;warn&gt;"));
        assertTrue(html.contains("&lt;error&gt;"));
        assertTrue(html.contains("&lt;loading&gt;"));
    }

    @Test
    void progressValuesAreClampedAndAnimatedVariantIsMarked() {
        UIContext ui = ui();
        assertEquals(0, ui.progressBar("Low", -20));
        assertEquals(100, ui.progressBar("High", 140));
        assertEquals(42, ui.progressBarAnimated("Build", 42));

        String html = ui.getHtml();
        assertTrue(html.contains("width:0%"));
        assertTrue(html.contains("width:100%"));
        assertTrue(html.contains("width:42%"));
        assertTrue(html.contains("animate-pulse"));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
