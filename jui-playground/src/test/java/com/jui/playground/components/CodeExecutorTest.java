package com.jui.playground.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.jui.playground.config.MyWebSocketHandler;
import com.jui.playground.config.WebSocketConfig;

class CodeExecutorTest {
	
	@Mock
    private WebSocketConfig webSocketConfig;
	
	@Mock
    private MyWebSocketHandler webSocketHandler;

    private CodeExecutor codeExecutor;


    @BeforeEach
    void setUp() {
    	MockitoAnnotations.openMocks(this);
    	
        codeExecutor = new CodeExecutor();
        codeExecutor.webSocketConfig = webSocketConfig;
        
        when(webSocketConfig.getWebSocketHandler()).thenReturn(webSocketHandler);
    }

    @Test
    @Disabled
    void testCompileAndRunJavaCode_Success() throws Exception {
    	
    	String fileName = "examples/TextElements.java";
    	
        assertDoesNotThrow(() -> codeExecutor.compileAndRunJavaCode(fileName));
        
        Thread.sleep(60000);

    }

}
