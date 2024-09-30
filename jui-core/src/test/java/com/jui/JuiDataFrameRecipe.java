package com.jui;

import java.io.IOException;
import java.net.URISyntaxException;

import com.st.JuiDataFrame;
import static com.st.ST.st;

public class JuiDataFrameRecipe {
	
	public static void main(String args) throws IOException, URISyntaxException {
		
		
		JuiDataFrame df = st.read_csv("https://raw.githubusercontent.com/mwzero/jui/main/datasets/gapminder_unfiltered.csv");
		df.getDf().getColumns().forEach(col -> System.out.println(col.getName()));
		for ( int irow=0; irow < df.getDf().rowCount(); irow++ ) {
				
			for ( int icol=0; icol< df.getDf().getColumns().size(); icol++ ) {
					System.out.println(df.getDf().getObject(irow,icol));
			
			}
		}
	}
	

}
