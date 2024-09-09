package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.st.JuiDataFrame;
import com.st.ST;


public class CsvRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.text.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
		
    	jui.divider();
    	
    	
    	jui.chart.bars(
    			ST.read_csv("my_data_1.csv"), 300, 300);
    	
    	
    	jui.chart.lines(
    			ST.read_csv("my_data_2.csv"), 300, 300);
    	
    	JuiDataFrame df = ST.read_csv("https://raw.githubusercontent.com/plotly/datasets/master/gapminder_unfiltered.csv");
		df.getHtmlCols().forEach(col -> System.out.println(col));
		df.getHtmlRows().forEach(row -> 
			row.forEach( cell ->  {
				System.out.println(cell);
		}));
    	jui.start();
    	
    }
}
