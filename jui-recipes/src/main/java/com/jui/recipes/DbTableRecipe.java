package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;

import com.st.ST;


public class DbTableRecipe {
	
	public static void main(String... args) throws Exception {
		
		populateH2();
		
		jui.text.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.text.caption("Simple Table");
    	var table1 = jui.table(ST.loadDB("org.h2.Driver", "jdbc:h2:~/testdb", null, null));
    	
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
        
        ResultSet rs;
        rs = stat.executeQuery("select * from test");
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
        stat.close();
        conn.close();
    }


}
