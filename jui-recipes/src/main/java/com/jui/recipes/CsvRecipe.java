package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.collections.api.factory.Lists;

import com.st.JuiDataFrame;
import com.st.ST;

import io.github.vmzakharov.ecdataframe.dataframe.AggregateFunction;

public class CsvRecipe {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.text.markdown("""
    			# JUI - CSV Recipes - Life Expetation
    			""");
		
    	jui.divider();
    	
    	//country,continent,year,lifeExp,pop,gdpPercap
    	JuiDataFrame df = ST.read_csv("csv/gapminder_unfiltered.csv");
    	
    	jui.chart.lines(
    			new JuiDataFrame(
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
    	
    	jui.start();
    	
    }
}
