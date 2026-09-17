package it.jui.playground;

import it.jui.JuiApp;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PlaygroundCompiler {

    private static final Pattern PACKAGE = Pattern.compile("(?m)^\\s*package\\s+([\\w.]+)\\s*;");
    private static final Pattern PUBLIC_TYPE = Pattern.compile("(?m)^\\s*public\\s+(?:final\\s+)?class\\s+(\\w+)");

    CompiledApplication compile(String source) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IOException("The playground requires a JDK, not a JRE.");

        String packageName = match(PACKAGE, source, "");
        String simpleName = match(PUBLIC_TYPE, source, null);
        if (simpleName == null) throw new IOException("Source must declare a public class.");

        Path workspace = Files.createTempDirectory("jui-playground-");
        Path output = workspace.resolve("classes");
        Path sourceRoot = workspace.resolve("src");
        Path sourceFile = sourceRoot.resolve(packageName.replace('.', '/')).resolve(simpleName + ".java");
        Files.createDirectories(sourceFile.getParent());
        Files.createDirectories(output);
        Files.writeString(sourceFile, source, StandardCharsets.UTF_8);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            var units = manager.getJavaFileObjects(sourceFile.toFile());
            List<String> options = List.of("-classpath", System.getProperty("java.class.path"), "-d", output.toString());
            boolean success = Boolean.TRUE.equals(compiler.getTask(null, manager, diagnostics, options, null, units).call());
            if (!success) {
                String message = formatDiagnostics(diagnostics);
                deleteWorkspace(workspace);
                throw new IOException(message);
            }
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[]{output.toUri().toURL()}, getClass().getClassLoader());
        try {
            String className = packageName.isBlank() ? simpleName : packageName + "." + simpleName;
            Object instance = classLoader.loadClass(className).getDeclaredConstructor().newInstance();
            if (!(instance instanceof JuiApp application)) {
                throw new IOException(className + " must implement JuiApp.");
            }
            return new CompiledApplication(application, classLoader, workspace);
        } catch (ReflectiveOperationException | IOException e) {
            classLoader.close();
            deleteWorkspace(workspace);
            throw new IOException("Cannot load compiled application: " + e.getMessage(), e);
        }
    }

    private String match(Pattern pattern, String source, String fallback) {
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : fallback;
    }

    private String formatDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
        StringBuilder message = new StringBuilder("Compilation failed:\n");
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            message.append("line ").append(diagnostic.getLineNumber()).append(": ")
                    .append(diagnostic.getMessage(null)).append('\n');
        }
        return message.toString();
    }

    private void deleteWorkspace(Path workspace) throws IOException {
        try (var paths = Files.walk(workspace)) {
            for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
