package com.jui.html.charts.builders;

import com.jui.html.WebContext;
import com.jui.html.base.builders.BaseBuilder;
import com.jui.html.charts.tags.BarChart;
import com.jui.html.charts.tags.LinesChart;
import com.jui.html.charts.tags.MapChart;
import com.st.DataFrame;

public class ChartBuilder extends BaseBuilder {
	
	public ChartBuilder(WebContext context) {
		super(context);
	}

	public LinesChart lines(DataFrame df, int max_width, int max_height) {

		LinesChart lines = LinesChart.builder()
				.df(df)
				.height(max_height)
				.width(max_width)
				.build();
		context.add(lines);
		
		return lines;
		
	}
	
	public BarChart bars(DataFrame df, int max_width, int max_height) {
		
		BarChart lines = BarChart.builder()
				.df(df)
				.max_height(max_height)
				.max_width(max_width)
				.build();
		context.add(lines);
		
		return lines;
	}
	
	public MapChart map() {
		
		MapChart map = new MapChart();
		context.add(map);
		
		return map;
	}

}
