package com.st.attributes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.st.JuiDataFrame;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DataBaseAttributes {
	
	String driver;
	String url;
	String username;
	String password;
	
	JuiDataFrame juidf;
	
	public JuiDataFrame select(String tableName, Map<String, ValueType> cols) {
		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
            
            juidf = new JuiDataFrame(df);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return juidf;
		
	}

	
	
	public static DataBaseAttributesBuilder builder() {
        return new CustomDataBaseAttributesBuilder();
    }

    private static class CustomDataBaseAttributesBuilder extends DataBaseAttributesBuilder {
    	
    	   	
    	@Override
        public DataBaseAttributes build() {
            
            return super.build();
    	}
    }
}
