package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.st.ST;


public class CsvRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.text.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
		
    	jui.divider();
    	
    	
    	var bars = jui.chart.bars(
    			ST.builder()
    				.option("classLoading",  "true")
    				.build()
    				.csv("my_data_1.csv", ","), 300, 300);
    	
    	
    	var lines= jui.chart.lines(
    			ST.builder()
    				.option("classLoading", "true")
    				.build()
    				.csv("my_data_2.csv", ","), 300, 300);
		
    	jui.start();
    	
    }
}
