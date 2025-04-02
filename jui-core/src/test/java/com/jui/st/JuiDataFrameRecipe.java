package com.jui.st;

import com.st.DataFrame;

import static com.st.ST.st;

public class JuiDataFrameRecipe {
	
	public static void main(String[] args) throws Exception {
		
		DataFrame df = st.read_csv("https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv");
		System.out.println(df.limit(100).show(50));

	}
}
