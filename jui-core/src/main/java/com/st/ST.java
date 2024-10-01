package com.st;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.jui.utils.FS;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ST {
	
	public static final ST st = new ST();
	
	Map<String, String> options;
	
    public ST() {
    	
		options = Map.of("classLoading",  "true");
    					
    }
    
	public DataFrame read_csv(String endpoint) throws IOException, URISyntaxException {
		
		File csvFile = FS.getFile(endpoint, options);
		DataSetCSV ds = new DataSetCSV(csvFile.toString());
		return new DataFrame(ds);
	}
	
	public DataFrame read_sql_query(Connection conn, String query) throws SQLException {

		DataSetDB ds = new DataSetDB(conn, query);
		ds.load();
				
        return new DataFrame(ds);
        
	}

	
	public DataFrame import_csv(String tableName, String endpoint, String query,  Connection conn) throws SQLException, IOException, URISyntaxException {
    	
    	File file = FS.getFile(endpoint, options);
    	
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE %s AS SELECT * FROM CSVREAD('%s')".formatted(tableName, file.getAbsolutePath()));
    	
    	return read_sql_query(conn, query);
    }

    
}
