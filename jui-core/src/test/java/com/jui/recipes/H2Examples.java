package com.jui.recipes;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;
import org.junit.jupiter.api.Test;

public class H2Examples {
	
	@Test
	public void alltables() throws ClassNotFoundException, SQLException {
		
		DeleteDbFiles.execute("~", "testdb", true);
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb");
        
        Statement stat = conn.createStatement();
        stat.execute("runscript from './src/main/resources/sql/init.sql'");
        
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet rs = databaseMetaData.getTables(null, null, null,null);
        
        while(rs.next()) {
            System.out.println(rs.getString("TABLE_NAME"));
        }
        conn.close();
        
	}

}
