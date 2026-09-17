package it.jui.playground;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaygroundAppTest {

    @Test
    void cloudModeShowsTrustedPreviewWithoutRunAction() {
        UIContext ui = new UIContext("cloud", new InMemorySessionManager());

        new PlaygroundApp(false).run(ui);

        String html = ui.getHtml();
        assertTrue(html.contains("Cloud demo mode"));
        assertTrue(html.contains("Hello JUI"));
        assertFalse(html.contains(">Run</button>"));
    }

    @Test
    void localExecutionModeOffersRunAction() {
        UIContext ui = new UIContext("local", new InMemorySessionManager());

        new PlaygroundApp(true).run(ui);

        String html = ui.getHtml();
        assertTrue(html.contains("Local execution mode"));
        assertTrue(html.contains(">Run</button>"));
    }
}
