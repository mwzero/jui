package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import com.jui.html.input.FormButton.ButtonType;
import com.st.ST;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class CsvRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException {
		
		jui.text.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
		
		 CsvReadOptions options = CsvReadOptions.builder(CsvReadOptions.class.getResourceAsStream("/my_data_1.csv"))
                 .header(false)  // Indica che il CSV ha una riga di intestazione
                 .separator(',')  // Specifica il separatore di colonne (qui è la virgola)
                 .build();
    	
		Table table = Table.read().usingOptions(options);
		 
    	jui.divider();
    	var lines = jui.chart.bars(table, 300, 300);
    	var bars= jui.chart.lines(ST.builder().option("classLoading", "true").build().csv("/my_data_2.csv", ","), 300, 300);
    	//var map= page.chart.map(readCSV(true, "my_data_2.csv"));
    	
    	jui.start();
    	
    }
}
