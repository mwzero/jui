package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.st.ST;


public class TableRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.table("Simple Table", 
    			ST.read_csv("table_1.csv"));
    	
    	jui.table("Striped Table", 
    			ST.read_csv("table_1.csv")).setStyles("table-striped");
    	
    	
    	jui.table("Striped and Dark Table", 
    			ST.read_csv("table_1.csv")).setStyles("table-striped", "table-dark");
    	
    	jui.start();
    	
    }

}
