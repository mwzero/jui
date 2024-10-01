package com.st;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jui.utils.FS;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
public class ST {
	
	public static final ST st = new ST();
	
	@Singular
	Map<String, String> options;
	
    public ST() {
    	
    }
    
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
	
	
	
	

	
	

    
}
