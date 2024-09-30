package com.jui.recipes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;


public class TableRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	jui.table("Simple Table", 
    			st.read_csv("table_1.csv"));
    	
    	jui.table("Striped Table", 
    			st.read_csv("table_1.csv")).setStyles("table-striped");
    	
    	
    	jui.table("Striped and Dark Table", 
    			st.read_csv("table_1.csv")).setStyles("table-striped", "table-dark");
    	
    	jui.start();
    	
    }

}
