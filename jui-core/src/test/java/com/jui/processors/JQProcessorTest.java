package com.jui.processors;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jui.utils.FS;

public class JQProcessorTest {
	
	@Test
	public void simple() throws IOException, URISyntaxException, InterruptedException {
    	
        String tmpdir = System.getProperty("java.io.tmpdir");
        
        String outputFilePath1 = tmpdir + "/test1.json";
        String outputFilePath2 = tmpdir + "/test2.json";
        
        String jsonInput = "[\n" +
                "  {\"name\": \"John\", \"age\": 30},\n" +
                "  {\"name\": \"Jane\", \"age\": 25},\n" +
                "  {\"name\": \"Doe\", \"age\": 35}\n" +
                "]";
        String jqFilter = "map(select(.age > 30)) | .[].name";
        
        JQProcessor jq = JQProcessor.builder()
        		.jqCommand("jq-windows-i386.exe")
        		.workingFolder("C:\\mwzero\\_resources").build();
        
        jq.processJsonString(jsonInput, jqFilter, outputFilePath1);
        
        jq.processJsonFile("json/persons.json", jqFilter, outputFilePath2);
    }

}
