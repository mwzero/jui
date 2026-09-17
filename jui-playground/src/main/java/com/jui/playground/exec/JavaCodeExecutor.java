package com.jui.playground.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Small playground-only Java compiler/runner based entirely on the JDK.
 * It replaces the former jui-toolkits dependency.
 */
public final class JavaCodeExecutor {

    private static final Pattern PACKAGE = Pattern.compile("(?m)^\\s*package\\s+([\\w.]+)\\s*;");

    private final OutputListener listener;
    private final List<String> classpath;
    private final Path rootFolder;

    public JavaCodeExecutor(OutputListener listener, String[] classpath, String rootFolder) {
        this.listener = Objects.requireNonNull(listener, "listener");
        this.classpath = classpath == null
                ? List.of()
                : Arrays.stream(classpath).filter(value -> value != null && !value.isBlank()).toList();
        this.rootFolder = rootFolder == null || rootFolder.isBlank() ? null : Path.of(rootFolder);
    }

    public void compileAndRunJavaCode(String fileName) throws IOException {
        Path source = rootFolder == null ? Path.of(fileName) : rootFolder.resolve(fileName);
        if (!Files.isRegularFile(source)) throw new IOException("Source file not found: " + source);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IOException("A JDK is required to compile playground code");

        Path output = Files.createTempDirectory("jui-playground-");
        try {
            compile(compiler, source, output);
            run(source, output);
        } finally {
            deleteRecursively(output);
        }
    }

    private void compile(JavaCompiler compiler, Path source, Path output) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            List<String> options = new ArrayList<>();
            options.add("-d");
            options.add(output.toString());
            if (!classpath.isEmpty()) {
                options.add("-classpath");
                options.add(String.join(File.pathSeparator, classpath));
            }

            var units = manager.getJavaFileObjects(source.toFile());
            boolean success = Boolean.TRUE.equals(compiler.getTask(null, manager, diagnostics, options, null, units).call());
            if (!success) {
                StringBuilder message = new StringBuilder("Compilation error:\n");
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    message.append(diagnostic.getMessage(null)).append('\n');
                }
                throw new IOException(message.toString());
            }
        }
    }

    private void run(Path source, Path output) throws IOException {
        String sourceText = Files.readString(source);
        Matcher matcher = PACKAGE.matcher(sourceText);
        String packageName = matcher.find() ? matcher.group(1) : "";
        String fileName = source.getFileName().toString();
        String simpleName = fileName.endsWith(".java") ? fileName.substring(0, fileName.length() - 5) : fileName;
        String className = packageName.isBlank() ? simpleName : packageName + "." + simpleName;

        List<String> runtimeClasspath = new ArrayList<>();
        runtimeClasspath.add(output.toString());
        runtimeClasspath.addAll(classpath);

        Process process = new ProcessBuilder(
                "java",
                "-cp", String.join(File.pathSeparator, runtimeClasspath),
                className)
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) listener.println(line);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) throw new IOException("Java process exited with code " + exitCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Java process interrupted", e);
        }
    }

    private void deleteRecursively(Path root) {
        try (var paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Temporary output is best-effort cleanup.
                }
            });
        } catch (IOException ignored) {
            // Temporary output is best-effort cleanup.
        }
    }
}
