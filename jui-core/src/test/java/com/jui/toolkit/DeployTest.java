package com.jui.toolkit;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeployTest {
	
	@Test
	public void simple() throws IOException {
        
    	JuiDeploy deploy = JuiDeploy.builder()
				        		.sourceFilePath("")
				        		.sourceFilePath("")
				        		.build();
    	
    	deploy.process();
    	
    	String deployFile = deploy.getDeployFile();
    	
    	log.info("Deploy to [{}]", deployFile);
        	
    }

}
