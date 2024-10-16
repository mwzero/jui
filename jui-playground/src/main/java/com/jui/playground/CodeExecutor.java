
package com.jui.playground;

import javax.tools.*;
import java.io.*;
import java.util.*;

public class CodeExecutor {

    public static String compileAndRunJavaCode(String code) {
        String className = "Main";
        File sourceFile = new File(className + ".java");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
        	
            writer.write(code);
        } catch (IOException e) {
            return "Error writing file: " + e.getMessage();
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, sourceFile.getPath());

        if (result != 0) {
            return "Compilation error.";
        }

        try {
            Process process = Runtime.getRuntime().exec("java " + className);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("");
            }
            return output.toString();
        } catch (IOException e) {
            return "Error running code: " + e.getMessage();
        }
    }
}
    