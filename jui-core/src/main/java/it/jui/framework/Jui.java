package it.jui.framework;

import it.jui.framework.app.JuiApp;
import it.jui.framework.app.JuiProvider;
import it.jui.framework.server.JuiServer;

import java.io.IOException;
import java.util.Objects;

/** Entry point for running a JUI application with the default server configuration. */
public final class Jui {

    private Jui() {
    }

    /**
     * Runs an application and blocks until the server is stopped.
     *
     * @param application application to run
     * @throws IOException if the HTTP server cannot be created
     * @throws InterruptedException if the server thread is interrupted
     */
    public static void run(JuiApp application) throws IOException, InterruptedException {
        Objects.requireNonNull(application, "application");
        new JuiServer(new JuiProvider(application)).start();
    }
}
