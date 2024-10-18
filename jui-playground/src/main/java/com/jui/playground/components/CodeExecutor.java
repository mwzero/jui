package com.jui.playground.components;

import javax.tools.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.jui.playground.config.WebSocketConfig;

import java.io.*;
import java.util.*;

@Component
public class CodeExecutor {
	
	@Autowired
    private WebSocketConfig webSocketConfig;

	public void compileAndRunJavaCode(String fileName) throws IOException {
	    File sourceFile = new File(fileName);

	    // Ottieni il nome della classe con il package
	    String className = fileName.replace(".java", "").replace(File.separator, ".");

	    // Percorso al file .jar
	    String libPath = "libs\\jui-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar";

	    // Compila il file sorgente
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

	    // Configura il classpath per includere la libreria esterna
	    Iterable<String> options = Arrays.asList("-classpath", libPath + File.pathSeparator + "src");
	    Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
	    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
	    boolean success = task.call();
	    fileManager.close();

	    if (!success) {
	        StringBuilder errorOutput = new StringBuilder("Compilation error:\n");
	        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
	            errorOutput.append(diagnostic.getMessage(null)).append("\n");
	        }
	        throw new IOException(errorOutput.toString());
	    }

	    // Se la compilazione è riuscita, esegui il codice compilato
	    try {
	        // Esegui la classe compilata utilizzando il nome completo del pacchetto
	    	ProcessBuilder processBuilder = new ProcessBuilder(
	    		    //"..\\\\..\\\\..\\\\res\\\\jbr-17.0.11-windows-x64-b1207.30\\\\bin\\\\java.exe", 
	    			"java",
	    		    "-cp", 
	    		    "libs/jui-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar;", 
	    		    "-Dlogging.level.root=INFO",
	    		    "-Dlogging.level.com.jui=INFO",
	    		    className
	    		);
	        
	        processBuilder.directory(new File("."));  // Assicurati che il processo venga eseguito nella directory corrente
	        Process process = processBuilder.start();

	        // Thread per gestire l'output standard
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                    	webSocketConfig.getWebSocketHandler().broadcastMessage(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            StringBuilder errorOutput = new StringBuilder();  
            // Thread per gestire l'output degli errori
            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                    	webSocketConfig.getWebSocketHandler().broadcastMessage(line);
                    	errorOutput.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

         // Avvia i thread
         outputThread.start();
         errorThread.start();
         
      // Attendi il termine del processo
         //int exitCode = process.waitFor();
         //outputThread.join();
         //errorThread.join();

         //System.out.println("Process exited with code: " + exitCode);
         

        // Se c'è stato un errore durante l'esecuzione, lancia un'eccezione
        if (errorOutput.length() > 0) {
            throw new IOException("Runtime error:\n" + errorOutput.toString());
        }

        //return output.toString();
        
	    } catch (Exception e) {
	        throw new IOException("Errore durante l'esecuzione del codice: " + e.getMessage());
	    } 
	}




}
