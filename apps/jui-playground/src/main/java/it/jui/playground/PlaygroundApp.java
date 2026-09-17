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

    static final String EXECUTION_ENV = "JUI_PLAYGROUND_EXECUTION_ENABLED";
    private static final Map<String, PlaygroundExample> EXAMPLES = loadExamples();

    private final boolean executionEnabled;
    private final PlaygroundCompiler compiler;
    private final Map<String, CompiledApplication> compiledApplications = new ConcurrentHashMap<>();
    private final Map<String, String> compilationErrors = new ConcurrentHashMap<>();

    public PlaygroundApp() {
        this(Boolean.parseBoolean(System.getenv().getOrDefault(EXECUTION_ENV, "false")));
    }

    PlaygroundApp(boolean executionEnabled) {
        this.executionEnabled = executionEnabled;
        this.compiler = executionEnabled ? new PlaygroundCompiler() : null;
    }

    @Override
    public void run(UIContext ui) {
        ui.title("JUI Playground", "code");
        ui.text("Explore Java-only applications and preview their JUI interface below.");
        if (executionEnabled) {
            ui.warning("Local execution mode: compiled code runs with the permissions of this process.");
        } else {
            ui.info("Cloud demo mode: editing is available, but arbitrary code execution is disabled.");
        }

        String selected = ui.select("Example", List.copyOf(EXAMPLES.keySet()), EXAMPLES.keySet().iterator().next());
        PlaygroundExample example = EXAMPLES.get(selected);
        String source = ui.codeEditor("Source · " + selected, example.source());

        if (!executionEnabled) {
            ui.caption("The preview uses the repository version of this example. Run the playground locally to execute edits.");
            renderPreview(ui, example.preview());
            return;
        }

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

        renderPreview(ui, compiled.application());
    }

    private void renderPreview(UIContext ui, JuiApp application) {
        ui.divider();
        ui.subheader("Preview");
        try {
            application.run(ui);
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

    private static Map<String, PlaygroundExample> loadExamples() {
        Map<String, PlaygroundExample> examples = new LinkedHashMap<>();
        examples.put("Hello", new PlaygroundExample(read("/examples/HelloExample.java"), PlaygroundApp::helloPreview));
        examples.put("Dashboard", new PlaygroundExample(read("/examples/DashboardExample.java"), PlaygroundApp::dashboardPreview));
        examples.put("Survey", new PlaygroundExample(read("/examples/SurveyExample.java"), PlaygroundApp::surveyPreview));
        return Collections.unmodifiableMap(examples);
    }

    private static void helloPreview(UIContext ui) {
        ui.title("Hello JUI");
        String name = ui.textInput("Name", "Guest");
        if (ui.button("Say hello")) ui.success("Hello " + name + "!");
    }

    private static void dashboardPreview(UIContext ui) {
        ui.title("Team Dashboard", "chart-bar");
        ui.columns(
                () -> ui.metric("Users", 128),
                () -> ui.metric("Tasks", 42),
                () -> ui.metric("Completion", "87%"));
        ui.progressBar("Sprint", 87);
    }

    private static void surveyPreview(UIContext ui) {
        String level = ui.radio("Experience", List.of("Junior", "Mid", "Senior"), "Mid");
        List<String> interests = ui.multiCheckbox(
                "Interests", List.of("Java", "AI", "Cloud"), List.of("Java"));
        if (ui.button("Submit")) ui.success("Saved: " + level + " · " + interests);
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

    private record PlaygroundExample(String source, JuiApp preview) {
    }
}
