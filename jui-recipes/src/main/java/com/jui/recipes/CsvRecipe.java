package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.jui.html.input.FormButton.ButtonType;
import com.st.ST;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataset.CsvDataSet;


public class CsvRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.text.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
		
		

		
		 
    	jui.divider();
    	
    	
    	/*
		 CsvReadOptions options = CsvReadOptions.builder(CsvReadOptions.class.getResourceAsStream("/my_data_1.csv"))
                .header(false)  // Indica che il CSV ha una riga di intestazione
                .separator(',')  // Specifica il separatore di colonne (qui è la virgola)
                .build();
   	
		Table table = Table.read().usingOptions(options);
		var bars = jui.chart.bars(table, 300, 300);
    	
		*/
    	
    	
    	URI uri = ClassLoader.getSystemResource("").toURI();
    	String mainPath = Paths.get(uri).toString();
    	Path path = Paths.get(mainPath ,"my_data_1.csv");
    	
    	DataFrame ordersFromFile  = new CsvDataSet(path.toFile().getAbsolutePath(), "Orders").loadAsDataFrame();
    	var bars = jui.chart.bars(ordersFromFile, 300, 300);
    	
    	
    	var lines= jui.chart.lines(ST.builder().option("classLoading", "true").build().csv("/my_data_2.csv", ","), 300, 300);
		
    	jui.start();
    	
    }
}
