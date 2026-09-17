package it.jui.playground;

import it.jui.Jui;
import it.jui.JuiApp;
import it.jui.UIContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlaygroundApp implements JuiApp {

    private static final Map<String, String> EXAMPLES = loadExamples();

    private final PlaygroundCompiler compiler = new PlaygroundCompiler();
    private final Map<String, CompiledApplication> compiledApplications = new ConcurrentHashMap<>();
    private final Map<String, String> compilationErrors = new ConcurrentHashMap<>();

    @Override
    public void run(UIContext ui) {
        ui.title("JUI Playground", "code");
        ui.text("Edit a Java application, compile it with the local JDK and preview it below.");
        ui.warning("Local development tool: compiled code runs with the permissions of this process.");

        String selected = ui.select("Example", List.copyOf(EXAMPLES.keySet()), EXAMPLES.keySet().iterator().next());
        String source = ui.codeEditor("Source · " + selected, EXAMPLES.get(selected));

        if (ui.button("Run")) compile(selected, source);

        String error = compilationErrors.get(selected);
        if (error != null) {
            ui.error(error);
            return;
        }

        CompiledApplication compiled = compiledApplications.get(selected);
        if (compiled == null) {
            ui.caption("Press Run to compile and preview this example.");
            return;
        }

        ui.divider();
        ui.subheader("Preview");
        try {
            compiled.application().run(ui);
        } catch (RuntimeException e) {
            ui.error("Preview failed: " + e.getMessage());
        }
    }

    private void compile(String selected, String source) {
        try {
            CompiledApplication next = compiler.compile(source);
            CompiledApplication previous = compiledApplications.put(selected, next);
            if (previous != null) {
                try {
                    previous.close();
                } catch (IOException ignored) {
                    // The new preview remains usable even if an old temporary folder cannot be removed.
                }
            }
            compilationErrors.remove(selected);
        } catch (Exception e) {
            compilationErrors.put(selected, e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    private static Map<String, String> loadExamples() {
        Map<String, String> examples = new LinkedHashMap<>();
        examples.put("Hello", read("/examples/HelloExample.java"));
        examples.put("Dashboard", read("/examples/DashboardExample.java"));
        examples.put("Survey", read("/examples/SurveyExample.java"));
        return Collections.unmodifiableMap(examples);
    }

    private static String read(String resource) {
        try (InputStream input = PlaygroundApp.class.getResourceAsStream(resource)) {
            if (input == null) throw new IllegalStateException("Missing playground resource: " + resource);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read playground resource: " + resource, e);
        }
    }

    public static void main(String[] args) throws Exception {
        Jui.run(new PlaygroundApp());
    }
}
