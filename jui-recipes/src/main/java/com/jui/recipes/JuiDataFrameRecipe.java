package com.jui.recipes;

import java.io.IOException;
import java.net.URISyntaxException;

import com.st.JuiDataFrame;
import com.st.ST;

public class JuiDataFrameRecipe {
	
	public static void main(String args) throws IOException, URISyntaxException {
		
		
		ConfifurationEnvironment.settingProxy();
	
		JuiDataFrame df = ST.read_csv("https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv");
		df.getHtmlCols().forEach(col -> System.out.println(col));
		df.getHtmlRows().forEach(row -> 
			row.forEach( cell ->  {
				System.out.println(cell);
		}));
		
	}
	
	

}
