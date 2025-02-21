package com.jui.playground.ctrls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jui.playground.config.WebSocketConfig;
import com.jui.playground.model.ExampleFile;

import com.jui.toolkits.JavaCodeExecutor;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:8080")
public class CodeExecutionController {
	
	@Autowired
	private WebSocketConfig webSocketConfig;
	
	JavaCodeExecutor codeExecutor;
	
	private static final String EXAMPLES_DIR = "examples" + File.separator;
	
	@PostConstruct
	public void init() {
		
		codeExecutor = JavaCodeExecutor.builder()
    			.listener(webSocketConfig)
    			.cp(new String[] {"libs\\jui-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar"})
    			//.rootFolder(";")
    			.build();
		
        // Initialization logic here
        System.out.println("MyController has been initialized");
        
    }

	@PostMapping("/compile")
	public ResponseEntity<String> compileCode(@RequestBody Map<String, String> requestBody) {
	    String fileName = requestBody.get("fileName");

	    try {
	        //String output = 
	    	codeExecutor.compileAndRunJavaCode(EXAMPLES_DIR + fileName);
	        return ResponseEntity.ok("");
	    } catch (IOException e) {
	    	
	        // Restituisci un errore 500 con il messaggio dell'eccezione
	        return ResponseEntity.status(500).body(e.getMessage());
	    }
	}

    
    

    @GetMapping("/examples")
    public List<ExampleFile> getExamples() {
    	
    	List<ExampleFile> examples = new ArrayList<ExampleFile>();
    	
        File folder = new File(EXAMPLES_DIR);
        File[] files = folder.listFiles();
        
        if  ( files == null) return examples; 
        
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                examples.add(new ExampleFile(file.getName(), file.getName()));
            }
        }
        return examples;
    }

    @GetMapping("/examples/{fileName}")
    public ResponseEntity<String> getExampleCode(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(EXAMPLES_DIR + fileName);
            String code = Files.readString(filePath);
            return ResponseEntity.ok(code);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error loading file.");
        }
    }

 // Endpoint per salvare il codice con un nome specificato
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveCode(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        String fileName = requestBody.get("fileName"); // Recupera il nome del file dal body della richiesta

        // Se non viene specificato un nome, usa un nome di default
        if (fileName == null || fileName.isEmpty()) {
            fileName = "CustomCode.java";
        }

        try {
            Path path = Paths.get(EXAMPLES_DIR + fileName);
            Files.writeString(path, code);
            return ResponseEntity.ok(Map.of("message", "Codice salvato con successo come " + fileName));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Errore nel salvataggio del file."));
        }
    }


}
    