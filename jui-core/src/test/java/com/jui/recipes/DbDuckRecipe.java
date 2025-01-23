package com.jui.recipes;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;
import com.st.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;



public class DbDuckRecipe {
	
	public static void main(String... args) throws Exception {
		
		populateDuckDB();
		Connection  connection = DB.getConnection("org.duckdb.DuckDBDriver","jdbc:duckdb:test", "", "");
		
		jui.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.table("Simple Table",
    			st.read_sql_query( connection, "select id, first_name, second_name from test"));
    	
    	jui.server().start();
    	
    }
	
	public static void populateDuckDB() throws Exception {
		
		Class.forName("org.duckdb.DuckDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:duckdb:test");
        
        
        Statement stmt = conn.createStatement();
        
        stmt.execute("DROP TABLE IF EXISTS test");
        stmt.execute("create table test(id int primary key, first_name varchar(255), second_name varchar(255))");
        stmt.execute("insert into test values(1, 'Cristoforo', 'Colombo')");
        stmt.execute("insert into test values(2, 'Marco', 'Polo')");
        stmt.execute("insert into test values(3, 'Giovanni', 'Gaboto')");
        stmt.close();
        conn.close();
    }


}
