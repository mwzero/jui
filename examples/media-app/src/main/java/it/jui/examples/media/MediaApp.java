package it.jui.examples.media;

import java.util.List;

import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.core.UIContext;
import it.jui.framework.server.JuiServer;

public class MediaApp implements JuiApp {

    @Override
    public void run(UIContext ui) {
        ui.title("Content Showcase", "collections");
        ui.markdown("# Java-first content\nJUI can mix **structured text**, lists, layout and native browser media without application-side HTML.");

        ui.expander("What this example shows", () ->
                ui.bullets(List.of("Markdown", "Image", "Audio", "Video", "Dialog")));

        ui.image("https://picsum.photos/900/320", "Remote image");
        ui.audio("https://www.w3schools.com/html/horse.mp3");
        ui.video("https://www.w3schools.com/html/mov_bbb.mp4");

        ui.dialog("About", () -> {
            ui.header("Content APIs");
            ui.text("Media elements render native browser tags while JUI keeps application code in Java.");
        });
    }

    public static void main(String[] args) throws Exception {
        new JuiServer(new JuiProvider(new MediaApp())).start();
    }
}
