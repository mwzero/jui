package com.jui.processors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import lombok.Builder;
import lombok.extern.java.Log;

@Builder
@Log
public class JQProcessor {
	
	String workingFolder;
	String jqCommand;
	
	
	public int processJsonString(String jsonString, String jqFilter, String outputFilePath) throws IOException, InterruptedException {
        
	    String[] command = {
        		"%s/%s".formatted(workingFolder, jqCommand), jqFilter};

        return process(command, jsonString, jqFilter, outputFilePath);

    }
	
	public int processJsonFile(String filePath, String jqFilter, String outputFilePath) throws IOException, InterruptedException {
		
		String[] command = {
				"%s/%s".formatted(workingFolder, jqCommand), jqFilter, filePath};
		
		return process(command, null, jqFilter, outputFilePath);
	}
	
	private int process(String[] command, String jsonString, String jqFilter, String outputFilePath) throws IOException, InterruptedException {
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(workingFolder));
        Process process = processBuilder.start();

        if ( jsonString != null ) {
	        log.finer("Writing json string to process input stream");
	        BufferedWriter processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	        processInput.write(jsonString);
	        processInput.close(); 
        }

        log.finer("Reading jq output");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
        }

        writer.close();
        reader.close();

        log.finer("waiting for process ending");
        int exitCode = process.waitFor();
        
        return exitCode;

    }

}
