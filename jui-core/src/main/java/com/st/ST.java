package com.st;

import java.io.File;
import java.io.FileNotFoundException;
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

import com.st.attributes.DataBaseAttributes;
import com.st.attributes.DataBaseAttributes.DataBaseAttributesBuilder;

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
	
	
	public static DataBaseAttributesBuilder DB() {
		return DataBaseAttributes.builder();
	}
	
	
	public static JuiDataFrame read_csv(String endpoint) throws IOException, URISyntaxException {
		
		ST st = ST.builder()
				.option("classLoading",  "true")
				.build();
			
		
		File filePath;
		
		if ( endpoint.startsWith("http") )  {
			
			URL url = new URL(endpoint);
			String endpointPath = url.getPath();
			String fileName = endpointPath.substring(endpointPath.lastIndexOf("/") + 1);
			Path tempDir = Files.createTempDirectory("csv_temp");
			Path tempFile = tempDir.resolve(fileName);
			
	        try (InputStream in = url.openStream()) {
	        	Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
	        }
	        filePath = tempFile.toFile();
			
		} else {
			filePath = st.getFileabsolutePath(endpoint);
				
		}
		return st.csv(filePath, ",", null);
	}
	
	private JuiDataFrame csv(File csvFile, String delimiter, String csvName) throws IOException {

		CsvDataSet ds = new CsvDataSet(csvFile.toString(), csvName);
		return new JuiDataFrame(ds.loadAsDataFrame());
	}
	
	
	private boolean isClassLoading() {
		
		if ( options.containsKey("classLoading") ) 
			return Boolean.parseBoolean(options.get("classLoading") );
		
		return false;
	}
	
	private File getFileabsolutePath(String fileName) throws FileNotFoundException, URISyntaxException {
		
		if ( isClassLoading()) {
			URI uri = ClassLoader.getSystemResource("").toURI();
			String mainPath = Paths.get(uri).toString();
	    	Path path = Paths.get(mainPath ,fileName);
	    	return path.toFile();
		} else {
			return new File(fileName);
		}
	}

}
