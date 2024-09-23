package com.jui.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FS {
	
	public static File getFile(String endpoint, Map<String, String> options) throws IOException, URISyntaxException {
		
		if ( endpoint.startsWith("http") )  {
			
			URL url = new URL(endpoint);
			String endpointPath = url.getPath();
			String fileName = endpointPath.substring(endpointPath.lastIndexOf("/") + 1);
			Path tempDir = Files.createTempDirectory("csv_temp");
			Path tempFile = tempDir.resolve(fileName);
			
	        try (InputStream in = url.openStream()) {
	        	Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
	        }
	        
	        return tempFile.toFile();
			
		} else if ( isClassLoading(options)) {
			
			URI uri = ClassLoader.getSystemResource("").toURI();
			String mainPath = Paths.get(uri).toString();
			
	    	Path path = Paths.get(mainPath ,endpoint);
	    	
	    	return path.toFile();
	    	
		} else {
			return new File(endpoint);
		}
	}
	
	private static boolean isClassLoading(Map<String, String> options) {
		
		if ( options.containsKey("classLoading") ) 
			return Boolean.parseBoolean(options.get("classLoading") );
		
		return false;
	}
	

}
