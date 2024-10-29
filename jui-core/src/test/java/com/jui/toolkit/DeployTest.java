package com.jui.toolkit;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.extern.java.Log;

@Log
public class DeployTest {
	
	@Test
	public void simple() throws IOException {
        
    	JuiDeploy deploy = JuiDeploy.builder()
				        		.sourceFilePath("")
				        		.sourceFilePath("")
				        		.build();
    	
    	deploy.process();
    	
    	String deployFile = deploy.getDeployFile();
    	
    	log.info("Deploy to [%s]".formatted(deployFile));
        	
    }

}
