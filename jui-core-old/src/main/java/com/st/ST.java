package com.st;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import com.jui.utils.FS;
import com.st.dataset.DataSetCSV;
import com.st.dataset.DataSetDB;
import com.st.dataset.DataSetJson;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class ST {
	
	public static final ST st = new ST();
	
	Map<String, String> options = Map.of("classLoading", "true");
	
    public ST() {;}
    
	public DataFrame read_csv(String endpoint) throws Exception {
		
		Reader csvFile = FS.getFile(endpoint, options);
		DataSetCSV ds = new DataSetCSV(csvFile);
		ds.load();
		
		return new DataFrame(ds);
	}
	
	public DataFrame read_sql_query(Connection conn, String query) throws Exception {

		DataSetDB ds = new DataSetDB(conn, query);
		ds.load();
				
        return new DataFrame(ds);
        
	}
	
	public DataFrame read_json(String endpoint) throws Exception {
		
		Reader jsonFile = FS.getFile(endpoint, options);
		
		DataSetJson ds = new DataSetJson(jsonFile);
		ds.load();
		return new DataFrame(ds);
	}

	public DataFrame read_csv_string(String csv) throws Exception {
		
		Reader csvFile = new StringReader(csv);
		DataSetCSV ds = new DataSetCSV(csvFile);
		ds.load();
		
		return new DataFrame(ds);
		
	}

	public Connection getConnection(String driver, String url, String username, String password) throws Exception {
    	
		try {
			Class.forName(driver);
			
		} catch (ClassNotFoundException e) {
			
			log.severe(e.getLocalizedMessage());
			throw e;
		}
		
		try { 
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
			
		} catch ( Exception err) {
			
			log.severe("Impossible to get a connection from [%s] with [%s]. Error[%s]".formatted(url, username, err.getLocalizedMessage()));
			throw err;
			
		}
    }
	
	
	
	

	
	

    
}
