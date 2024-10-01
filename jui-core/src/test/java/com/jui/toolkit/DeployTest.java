package com.jui.toolkit;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class DeployTest {
	
	@Test
	public void simple() {
        
		String sourceDir = "percorso/al/tuo/directory";  // Cartella da comprimere
        String zipFile = "percorso/al/tuo/file.zip";  // File zip da creare

        try {
        	
        	JuiDeploy.zipFiles(sourceDir, zipFile);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
