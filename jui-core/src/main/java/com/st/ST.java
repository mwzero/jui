package com.st;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
	
	public List<List<String>> csv(String csvFile, String commaDelimiter) throws IOException {

		List<List<String>> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(getFileStreamReader(csvFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(commaDelimiter);
		        records.add(Arrays.asList(values));
		    }
		}
		
		return records;
	}
	
	
	public SimpleTable readAsTable(String csvFile, String commaDelimiter) throws FileNotFoundException, IOException {
		
		List<SimpleColumn> cols = new ArrayList<>();
		List<SimpleRow> rows = new ArrayList<>();
		
		boolean headers = true;
		try (BufferedReader br = new BufferedReader(getFileStreamReader(csvFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String[] values = line.split(commaDelimiter);
		    	if ( headers ) {
		    		
		    		for ( String column : values) 
		    			cols.add(SimpleColumn.builder().name(column).build());
		    		
		    		headers=false;
		    	} else {
		    		rows.add(SimpleRow.builder().values(Arrays.asList(values)).build());
		    	}
		    }
		}
		
		return SimpleTable.builder().columns(cols).rows(rows).build();
	}
	
	private boolean isClassLoading() {
		
		if ( options.containsKey("classLoading") ) 
			return Boolean.parseBoolean(options.get("classLoading") );
		
		return false;
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
