package it.jui.framework.apis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.jui.framework.core.UIContext;
import it.jui.framework.server.InMemorySessionManager;

class TextElementsTest {

    @Test
    void textApisEscapeUntrustedContentAndAllowExplicitHtml() {
        UIContext ui = ui();

        ui.title("<Customers>", "<icon>");
        ui.header("A & B");
        ui.subheader("<sub>");
        ui.text("<script>alert(1)</script>");
        ui.caption("'caption'");
        ui.code("<tag>", "java<script>");
        ui.divider();
        ui.html("<strong id='trusted'>trusted</strong>");

        String html = ui.getHtml();
        assertTrue(html.contains("&lt;Customers&gt;"));
        assertTrue(html.contains("&lt;icon&gt;"));
        assertTrue(html.contains("A &amp; B"));
        assertTrue(html.contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
        assertTrue(html.contains("&#39;caption&#39;"));
        assertTrue(html.contains("java&lt;script&gt;"));
        assertTrue(html.contains("<hr"));
        assertTrue(html.contains("<strong id='trusted'>trusted</strong>"));
    }

    @Test
    void markdownSupportsSafeSubsetAndEscapesRawHtml() {
        UIContext ui = ui();
        ui.markdown("# Report\n- **one**\n- `two`\nVisit [JUI](https://example.com)\n<script>x</script>");

        String html = ui.getHtml();
        assertTrue(html.contains("<h1"));
        assertTrue(html.contains("<strong>one</strong>"));
        assertTrue(html.contains("<code"));
        assertTrue(html.contains("href='https://example.com'"));
        assertTrue(html.contains("&lt;script&gt;x&lt;/script&gt;"));
        assertFalse(html.contains("<script>x</script>"));
    }

    @Test
    void buttonClickIsConsumedExactlyOnce() {
        InMemorySessionManager sessions = new InMemorySessionManager();
        UIContext probe = new UIContext("s1", sessions);
        String buttonId = probe.getNextWidgetId("Save");
        sessions.updateState("s1", buttonId, true);

        assertTrue(new UIContext("s1", sessions).button("Save"));
        assertFalse(new UIContext("s1", sessions).button("Save"));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
