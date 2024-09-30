package com.st;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.jui.utils.FS;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ST {
	
	public static final ST st = new ST();
	
	Map<String, String> options;
	
	Map<String, ValueType> mapTypes;
	
    public ST() {
    	
		mapTypes = Map.of(
				"CHARACTER VARYING", ValueType.STRING,
				"INTEGER", ValueType.INT,
				"DATE", ValueType.DATE,
				"DECIMAL", ValueType.DECIMAL
		);
		
		options = Map.of("classLoading",  "true");
    					
    }
    
	public JuiDataFrame read_csv(String endpoint) throws IOException, URISyntaxException {
		
		File csvFile = FS.getFile(endpoint, options);

		CsvDataSet ds = new CsvDataSet(csvFile.toString(), null);
		return new JuiDataFrame(ds.loadAsDataFrame());
	}
	
	public JuiDataFrame read_sql_query(String query, Connection conn) throws SQLException {
		
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
        		Object obj = rs.getObject(metaData.getColumnName(i+1));
        		if ( obj instanceof java.sql.Date) {
        			row[i] = ((java.sql.Date) obj).toLocalDate();
        		} else {
        			row[i] = obj;
        		}
        	}
        	df.addRow(row);
        }
            
        return new JuiDataFrame(df);
    }
	
	public JuiDataFrame import_csv(String tableName, String endpoint, String query,  Connection conn) throws SQLException, IOException, URISyntaxException {
    	
    	File file = FS.getFile(endpoint, options);
    	
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE %s AS SELECT * FROM CSVREAD('%s')".formatted(tableName, file.getAbsolutePath()));
    	
    	return read_sql_query(query, conn);
    }

    
}
