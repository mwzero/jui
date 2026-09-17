package it.jui.playground;

import it.jui.UIContext;
import it.jui.server.InMemorySessionManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaygroundCompilerTest {

    private final PlaygroundCompiler compiler = new PlaygroundCompiler();

    @Test
    void compilesAndRunsAJuiApplication() throws Exception {
        String source = """
                import it.jui.JuiApp;
                import it.jui.UIContext;

                public class TestPreview implements JuiApp {
                    public void run(UIContext ui) {
                        ui.text("Compiled preview");
                    }
                }
                """;

        try (CompiledApplication compiled = compiler.compile(source)) {
            UIContext ui = new UIContext("test", new InMemorySessionManager());
            compiled.application().run(ui);
            assertTrue(ui.getHtml().contains("Compiled preview"));
        }
    }

    @Test
    void reportsCompilerDiagnostics() {
        IOException error = assertThrows(IOException.class,
                () -> compiler.compile("public class Broken { syntax error }"));

        assertTrue(error.getMessage().contains("Compilation failed"));
        assertTrue(error.getMessage().contains("line 1"));
    }
}
