package com.jui.recipes;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Map;

import com.st.FS;

public class JsonTableRecipe {
	
    public static void main(String[] args) throws IOException, URISyntaxException {
    	
        String tmpdir = System.getProperty("java.io.tmpdir");
        String outputFilePath1 = tmpdir + "/test1.json";
        String outputFilePath2 = tmpdir + "/test2.json";
        String jsonInput = "[\n" +
                "  {\"name\": \"John\", \"age\": 30},\n" +
                "  {\"name\": \"Jane\", \"age\": 25},\n" +
                "  {\"name\": \"Doe\", \"age\": 35}\n" +
                "]";
        String jqFilter = "map(select(.age > 30)) | .[].name";
        processJsonString(jsonInput, jqFilter, outputFilePath1);
        processJsonFile(
        		FS.getFile("yourfile.json", Map.of("classLoadin", "true")).getAbsolutePath(), jqFilter, outputFilePath2);
    }

    public static void processJsonString(String jsonString, String jqFilter, String outputFilePath) {
        try {
            // Comando jq
            String[] command = {"C:\\mwzero\\_resources\\jq-windows-i386.exe", jqFilter};

            // Creiamo il processo usando ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File("C:\\mwzero\\_resources\\"));
            Process process = processBuilder.start();

            // Scriviamo la stringa JSON nell'input del processo
            BufferedWriter processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processInput.write(jsonString);
            processInput.close();  // Chiudiamo lo stream dopo aver scritto la stringa

            // Leggiamo l'output di jq
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            reader.close();

            // Attendere che il processo termini
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Comando jq eseguito con successo su stringa JSON. Risultato scritto in: " + outputFilePath);
            } else {
                System.err.println("Errore durante l'esecuzione del comando jq. Codice di uscita: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Metodo per eseguire jq su un file JSON
    public static void processJsonFile(String filePath, String jqFilter, String outputFilePath) {
        try {
            // Comando jq con file JSON come input
            String[] command = {"C:\\mwzero\\_resources\\jq-windows-i386.exe", jqFilter, filePath};

            // Creiamo il processo usando ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File("C:\\mwzero\\_resources\\"));
            Process process = processBuilder.start();

            // Lettura dell'output del comando jq
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            reader.close();

            // Attendere che il processo termini
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Comando jq eseguito con successo su file JSON. Risultato scritto in: " + outputFilePath);
            } else {
                System.err.println("Errore durante l'esecuzione del comando jq. Codice di uscita: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
