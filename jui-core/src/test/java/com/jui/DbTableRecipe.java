package com.jui;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import com.jui.utils.FS;
import com.st.DB;
import com.st.DataFrame;

import java.io.File;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

import org.h2.tools.DeleteDbFiles;




public class DbTableRecipe {
	
	public static void main(String... args) throws Exception {
		
		populateH2();
		
		Connection  connection = DB.getConnection("org.h2.Driver","jdbc:h2:~/testdb", "", "");
		
		jui.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.table("Simple Table", 
    			st.read_sql_query( connection, "select id, first_name, second_name from test"));
    	
    	jui.table("CSV from Table",
    			import_csv("city", "cities.csv", "select * from city", connection));
    	
    	jui.table("CSV from Table",
    			import_csv(
    					"countries", 
    					"https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv", 
    					"select * from countries", connection));
    	
    	jui.start();
    	
    }
	
	public static void populateH2() throws Exception {
		
        DeleteDbFiles.execute("~", "testdb", true);
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb");
        Statement stat = conn.createStatement();
        stat.execute("runscript from './src/test/resources/sql/init.sql'");
        stat.close();
        conn.close();
    }
	
	public static DataFrame import_csv(String tableName, String endpoint, String query,  Connection conn) throws Exception {
    	
    	Reader file = FS.getFile(endpoint, Map.of("classLoading", "True"));
    	Statement stmt = conn.createStatement();
        stmt.execute(
        		"CREATE TABLE %s AS SELECT * FROM CSVREAD('%s')".formatted(tableName, file));
    	
    	return st.read_sql_query(conn, query);
    }


}
