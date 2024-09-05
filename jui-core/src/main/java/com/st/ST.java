package com.st;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Builder
@Getter
@Setter
public class ST {
	
	@Singular
	Map<String, String> options;
	
	public static JuiDataFrame read_csv(String csvFile) throws IOException {
		
		return ST.builder()
				.option("classLoading",  "true")
				.build()
				.csv(csvFile, ",");
	}
	
	public  JuiDataFrame csv(String csvFile, String commaDelimiter) throws IOException {

		try {
			return new JuiDataFrame(new CsvDataSet(getFileabsolutePath(csvFile), "Orders").loadAsDataFrame());
			
		} catch (FileNotFoundException | URISyntaxException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
	}
	
	
	private boolean isClassLoading() {
		
		if ( options.containsKey("classLoading") ) 
			return Boolean.parseBoolean(options.get("classLoading") );
		
		return false;
	}
	
	private String getFileabsolutePath(String fileName) throws FileNotFoundException, URISyntaxException {
		
		if ( isClassLoading()) {
			URI uri = ClassLoader.getSystemResource("").toURI();
			String mainPath = Paths.get(uri).toString();
	    	Path path = Paths.get(mainPath ,fileName);
	    	return path.toFile().getAbsolutePath();
		} else {
			return fileName;
		}
	}

	private InputStreamReader getFileStreamReader(String fileName) throws FileNotFoundException {
		
		InputStream is = null;
		if ( isClassLoading()) {
			is = getClass().getResourceAsStream(fileName);
		} else {
			
			is = new FileInputStream(new File(fileName));
		}
		return new InputStreamReader(is);
	} 
}
