package com.jui;

import java.io.IOException;
import java.net.URISyntaxException;

import com.st.DataFrame;

import static com.st.ST.st;

public class JuiDataFrameRecipe {
	
	public static void main(String args) throws IOException, URISyntaxException {
		
		
		DataFrame df = st.read_csv("https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv");
		df.show();
	}
	

}
