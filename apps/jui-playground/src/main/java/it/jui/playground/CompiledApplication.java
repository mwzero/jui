package it.jui.playground;

import it.jui.JuiApp;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

final class CompiledApplication implements AutoCloseable {

    private final JuiApp application;
    private final URLClassLoader classLoader;
    private final Path workspace;

    CompiledApplication(JuiApp application, URLClassLoader classLoader, Path workspace) {
        this.application = application;
        this.classLoader = classLoader;
        this.workspace = workspace;
    }

    JuiApp application() {
        return application;
    }

    @Override
    public void close() throws IOException {
        classLoader.close();
        try (var paths = Files.walk(workspace)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
