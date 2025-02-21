package com.jui.toolkits;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaCodeExecutorTest {
	
    private JavaCodeExecutor codeExecutor;


    @BeforeEach
    void setUp() {
    	
    	codeExecutor = JavaCodeExecutor.builder()
    			.listener(new ConsoleOutputListener())
    			.cp(new String[] {"libs\\jui-core-0.0.1-SNAPSHOT-jar-with-dependencies.jar"})
    			.rootFolder("src/test/resources")
    			.build();
    	
    }

    @Test
    void testCompileAndRunJavaCode_Success() throws Exception {
    	
    	String fileName = "examples/TextElements.java";
    	
        assertDoesNotThrow(() -> codeExecutor.compileAndRunJavaCode(fileName));
        
        Thread.sleep(60000);

    }
}
