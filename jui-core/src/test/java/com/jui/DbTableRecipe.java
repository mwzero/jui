package com.jui;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;
import com.st.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
    			st.read_sql_query("select id, first_name, second_name from test", connection));
    	
    	jui.table("CSV from Table",
    			st.import_csv("city", "cities.csv", "select * from city", connection));
    	
    	jui.table("CSV from Table",
    			st.import_csv(
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
        stat.execute("runscript from './src/main/resources/sql/init.sql'");
        stat.close();
        conn.close();
    }


}
