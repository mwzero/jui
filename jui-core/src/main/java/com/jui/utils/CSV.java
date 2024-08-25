package com.jui.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jui.st.SimpleColumn;
import com.jui.st.SimpleRow;
import com.jui.st.SimpleTable;

public class CSV {

	public static List<List<String>> readAsList(Boolean classLoading, String csvFile) throws FileNotFoundException, IOException {
		return readAsList(classLoading, csvFile, ",");
	}

	public static List<List<String>> readAsList(Boolean classLoading, String csvFile, String commaDelimiter) throws FileNotFoundException, IOException {
		
		String file = null;
		if ( classLoading) {
			ClassLoader classLoader = CSV.class.getClassLoader();
			URL resource = classLoader.getResource(csvFile);
			if (resource != null) {
				file = resource.getFile();
			} 
		} 
		List<List<String>> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(commaDelimiter);
		        records.add(Arrays.asList(values));
		    }
		}
		
		return records;
	}
	
	public static SimpleTable readCSV(Boolean classLoading, String csvFile) throws FileNotFoundException, IOException {
		return readAsTable(classLoading, csvFile, ",");
	}

	public static SimpleTable readAsTable(Boolean classLoading, String csvFile, String commaDelimiter) throws FileNotFoundException, IOException {
		
		List<SimpleColumn> cols = new ArrayList<>();
		List<SimpleRow> rows = new ArrayList<>();
		
		String file = null;
		if ( classLoading) {
			ClassLoader classLoader = CSV.class.getClassLoader();
			URL resource = classLoader.getResource(csvFile);
			if (resource != null) {
				file = resource.getFile();
			} 
		} 
		boolean headers = true;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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

}
