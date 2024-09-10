package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

import org.h2.tools.DeleteDbFiles;

import com.st.DbDataSet;

import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;


public class DbTableRecipe {
	
	public static void main(String... args) throws Exception {
		
		populateH2();
		
		DbDataSet dbSet = DbDataSet.builder()
			.driver("org.h2.Driver")
			.url("jdbc:h2:~/testdb")
		.build();
		
		jui.text.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.table("Simple Table", 
    			dbSet.select("test", Map.of(
    					    "id", ValueType.INT,
    					    "first_name", ValueType.STRING,
    					    "second_name", ValueType.STRING)));
    	
    	jui.table("Very Simple Table", 
    			dbSet.execute("select first_name from test"));
    	
    	
    	jui.table("CSV from Table",
    			dbSet.import_csv("city", "cities.csv", "select * from city"));
    	
    	jui.table("CSV from Table",
    			dbSet.import_csv(
    					"countries", 
    					"https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv", 
    					"select * from countries"));
    	
    	jui.start();
    	
    }
	
	public static void populateH2() throws Exception {
		
        DeleteDbFiles.execute("~", "testdb", true);
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb");
        Statement stat = conn.createStatement();

        // this line would initialize the database
        // from the SQL script file 'init.sql'
        stat.execute("runscript from './src/main/resources/sql/init.sql'");

        //stat.execute("create table test(id int primary key, name varchar(255))");
        //stat.execute("insert into test values(1, 'Hello')");
        /*
        ResultSet rs;
        rs = stat.executeQuery("select * from test");
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
        */
        stat.close();
        conn.close();
    }


}
