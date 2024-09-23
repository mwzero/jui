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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.jui.utils.FS;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
public class DbDataSet {
	
	String driver;
	String url;
	String username;
	String password;
	
	Map<String, ValueType> mapTypes;
	
	public static DbDataSetBuilder builder() {
        return new CustomDataDbDataSetBuilder();
    }

    private static class CustomDataDbDataSetBuilder extends DbDataSetBuilder {
    	
    	@Override
        public DbDataSet build() {
            
    		try {
    			Class.forName(super.driver);
    			
    		} catch (ClassNotFoundException e1) {
    			
    			log.error("Impossible to load driver", e1);
    		}
    		
    		
    		super.mapTypes = Map.of(
    				"CHARACTER VARYING", ValueType.STRING 
    		);
    		 
            return super.build();
    	}
    }
    
    public JuiDataFrame select(String tableName, Map<String, ValueType> cols) {
		
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
        	
            String query = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            
            // Creare un DataFrame
            DataFrame df = new DataFrame(tableName);
            for ( var col : cols.entrySet()) {
            	df.addColumn(col.getKey(), col.getValue());
            }

            while (rs.next()) {
            	
            	Object[] row = new Object[cols.size()];
            	int i=0;
            	for ( var col : cols.entrySet()) 
            		row[i++] = rs.getObject(col.getKey());

            	df.addRow(row);
            }
            
            return new JuiDataFrame(df);
            
        } catch (Exception e) {
        	
        	log.error("Impossible to execute select ", e);
        	return null;
        }

	}
    
    public JuiDataFrame execute(String query) throws SQLException {
		
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
        	
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            
            DataFrame df = new DataFrame("");

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
            	
            	if ( mapTypes.containsKey(metaData.getColumnTypeName(i))) {
            		
            		df.addColumn(
                			metaData.getColumnName(i), 
                			mapTypes.get(metaData.getColumnTypeName(i)));
            		
            	} else 
            		df.addColumn(
            			metaData.getColumnName(i), 
            			metaData.getColumnTypeName(i));
            }
            
            while (rs.next()) {
            	
            	Object[] row = new Object[columnCount];
            	for (int i = 0; i < columnCount; i++) {
            		row[i] = rs.getObject(metaData.getColumnName(i+1));
            	}
            	df.addRow(row);
            }
            
            return new JuiDataFrame(df);
            
        }
    }
    
    public JuiDataFrame import_csv(String tableName, String endpoint, String query) throws SQLException, IOException, URISyntaxException {
    	
    	File file = FS.getFile(endpoint, Map.of("classLoading",  "true"));
    	
    	try (Connection conn = DriverManager.getConnection(url, username, password)) {
        	
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE %s AS SELECT * FROM CSVREAD('%s')".formatted(tableName, file.getAbsolutePath()));
    	}
    	
    	return this.execute(query);
    }
    
}
