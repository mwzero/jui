package com.jui.toolkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
public class JuiDeploy {
	
	@Getter
	String deployFile;
	
	@Singular
	List<String> sourceFilePaths;
	
	public void process() throws IOException {
		
		Path deployPath;
		if ( deployFile == null) {
			
			deployPath = Files.createTempFile("JUI_" + UUID.randomUUID().toString(), ".deploy");
			
		} else {
			deployPath = Paths.get(deployFile);
		}
		
		deployFile = deployPath.toAbsolutePath().toString();
		
	}

}
