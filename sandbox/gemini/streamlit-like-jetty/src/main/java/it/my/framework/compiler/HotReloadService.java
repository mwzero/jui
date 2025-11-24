package it.my.framework.compiler;

import it.my.framework.UIApp;
import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class HotReloadService {

    private final File sourceFile;
    private UIApp currentAppInstance;
    private long lastModified = 0;

    public HotReloadService(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public synchronized UIApp getApp() {
        long currentModified = sourceFile.lastModified();
        
        if (currentModified > lastModified || currentAppInstance == null) {
            try {
                long start = System.currentTimeMillis();
                currentAppInstance = compileAndLoad();
                lastModified = currentModified;
                System.out.println("Ricompilato " + sourceFile.getName() + " in " + (System.currentTimeMillis() - start) + "ms");
            } catch (Exception e) {
                final String errorMsg = e.getMessage();
                return ui -> {
                    ui.title("Errore di Compilazione");
                    ui.info(errorMsg.replace("\n", "<br>"));
                };
            }
        }
        return currentAppInstance;
    }

    private UIApp compileAndLoad() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("Compilatore JDK non trovato. Assicurati di usare un JDK e non una JRE.");
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        File parentDir = sourceFile.getParentFile();
        
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));

        String currentClasspath = System.getProperty("java.class.path");
        List<String> options = new ArrayList<>();
        options.add("-classpath");
        options.add(currentClasspath + File.pathSeparator + parentDir.getAbsolutePath());

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);

        boolean success = task.call();
        fileManager.close();

        if (!success) throw new RuntimeException("Errore di sintassi nel codice.");

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{parentDir.toURI().toURL()})) {
            String className = sourceFile.getName().replace(".java", "");
            Class<?> loadedClass = classLoader.loadClass(className);
            return (UIApp) loadedClass.getDeclaredConstructor().newInstance();
        }
    }
}