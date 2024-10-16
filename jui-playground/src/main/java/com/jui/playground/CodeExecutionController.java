package com.jui.playground;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class CodeExecutionController {

    private static final String EXAMPLES_DIR = "src/main/resources/examples/";

    @GetMapping("/examples")
    public List<ExampleFile> getExamples() {
        File folder = new File(EXAMPLES_DIR);
        File[] files = folder.listFiles();
        List<ExampleFile> examples = new ArrayList<ExampleFile>();
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

    @PostMapping("/compile")
    public ResponseEntity<String> compileCode(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        String output = CodeExecutor.compileAndRunJavaCode(code);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveCode(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");
        try {
            Path path = Paths.get(EXAMPLES_DIR + "CustomCode.java");
            Files.writeString(path, code);
            return ResponseEntity.ok(Map.of("message", "Code saved successfully!"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error saving file."));
        }
    }
}
    