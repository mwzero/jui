package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.st.ST;


public class TableRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.text.markdown("""
    			# Table Examples
    			""");
    	jui.divider();
    	
    	
    	jui.text.caption("Simple Table");
    	var table1 = jui.table(ST.read_csv("table_1.csv"));
    	
    	
    	jui.text.caption("Striped Table");
    	var table2 = jui.table(ST.read_csv("table_1.csv"), "table-striped");
    	
    	
    	jui.text.caption("Striped and Dark Table");
    	var table3 = jui.table(ST.read_csv("table_1.csv"), "table-striped", "table-dark");
    	
    	jui.start();
    	
    }

}
