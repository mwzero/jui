package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

import com.st.DbDataSet;

import io.github.vmzakharov.ecdataframe.dsl.value.ValueType;


public class DbDuckRecipe {
	
	public static void main(String... args) throws Exception {
		
		populateDuckDB();
		
		DbDataSet dbSet = DbDataSet.builder()
			.driver("org.duckdb.DuckDBDriver")
			.url("jdbc:duckdb:test")
		.build();
		
		jui.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.table("Simple Table", 
    			dbSet.select("test", Map.of(
    					    "id", ValueType.INT,
    					    "first_name", ValueType.STRING,
    					    "second_name", ValueType.STRING)));
    	
    	/*
    	jui.table("Very Simple Table", 
    			dbSet.execute("select first_name from test"));
    	*/
    	
    	/*
    	jui.table("CSV from Table",
    			dbSet.import_csv("city", "cities.csv", "select * from city"));
    	
    	
    	jui.table("CSV from Table",
    			dbSet.import_csv(
    					"countries", 
    					"https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv", 
    					"select * from countries"));
    	
    	*/
    	jui.start();
    	
    }
	
	public static void populateDuckDB() throws Exception {
		
		Class.forName("org.duckdb.DuckDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:duckdb:test");
        
        
        Statement stmt = conn.createStatement();
        
        stmt.execute("DROP TABLE IF EXISTS test");
        stmt.execute("create table test(id int primary key, first_name varchar(255), second_name varchar(255))");
        stmt.execute("insert into test values(1, 'Cristoforo', 'Colombo')");
        stmt.close();
        conn.close();
    }


}
