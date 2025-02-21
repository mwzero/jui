package com.jui.toolkits;

import javax.tools.*;

import lombok.Builder;

import java.io.*;
import java.util.*;

@Builder
public class JavaCodeExecutor {
	
	OutputListener listener;
	String[] cp;
	String rootFolder;
	
	
	
	public void compileAndRunJavaCode(String fileName) throws IOException {
		
	    File sourceFile = new File((rootFolder != null ? rootFolder + File.separator  : "")  + fileName);

	    // Ottieni il nome della classe con il package
	    String className = fileName.replace(".java", "").replace(File.separator, ".");

	    // Compila il file sorgente
	    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
	    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

	    // Configura il classpath per includere la libreria esterna
	    Iterable<String> options = Arrays.asList("-classpath", String.join(";", cp) );
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
	    			"java",
	    		    "-cp", 
	    		    (rootFolder != null ? rootFolder + ";" : ".;") + String.join(";", cp),
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
                    	
                    	listener.println(line);
                    	
                    	
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
                    	listener.println(line);
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
