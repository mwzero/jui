package com.jui.recipes;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;
import com.st.DB;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import com.st.DataFrame;

public class CsvRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.markdown("""
    			# JUI - CSV Recipes - Life Expetation
    			""");
		
    	jui.divider();
    	
    	//country,continent,year,lifeExp,pop,gdpPercap
    	DataFrame df = st.read_csv("csv/gapminder_unfiltered.csv");
    	
    	/*
    	jui.chart.lines(
    			new DataFrame(
    					df.getDf()
    						.selectBy("year == 2007")
    						.selectBy("continent=='Europe'")
    						.dropColumn("continent")
    						.dropColumn("pop")
    						.dropColumn("gdpPercap")
    						.dropColumn("year")), 0, 300);
    	
    	
    	jui.table("Italy", 
    			new JuiDataFrame(
    					df.getDf()
    						.selectBy("country=='Italy'")
    						.selectBy("year > 1990")
    						.dropColumn("continent")
    						.dropColumn("pop")
    						.dropColumn("gdpPercap")
    						));
    	
    	jui.table("gdpPercap", 
    			new JuiDataFrame(
    					df.getDf()
    						.selectBy("continent=='Europe'")
    						.aggregateBy(
    							Lists.immutable.of(AggregateFunction.avg("gdpPercap")), Lists.immutable.of("country"))
    					));
    	
    	*/
    	jui.start();
    	
    }
}
