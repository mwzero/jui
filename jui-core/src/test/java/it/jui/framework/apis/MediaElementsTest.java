package it.jui.framework.apis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.jui.framework.core.UIContext;
import it.jui.framework.server.InMemorySessionManager;

class MediaElementsTest {

    @Test
    void imageAudioAndVideoRenderSafeSources() {
        UIContext ui = ui();
        ui.image("/image?a=1&b=2", "<Preview>");
        ui.audio("/sound?x=1&y=2");
        ui.video("/movie.mp4");

        String html = ui.getHtml();
        assertTrue(html.contains("/image?a=1&amp;b=2"));
        assertTrue(html.contains("&lt;Preview&gt;"));
        assertTrue(html.contains("<audio"));
        assertTrue(html.contains("<video"));
        assertTrue(html.contains("controls"));
    }

    @Test
    void videoCanRenderWithoutControls() {
        UIContext ui = ui();
        ui.video("/movie.mp4", false);
        String html = ui.getHtml();
        assertTrue(html.contains("<video"));
        assertFalse(html.contains(" controls "));
    }

    private UIContext ui() {
        return new UIContext("s1", new InMemorySessionManager());
    }
}
