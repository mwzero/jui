package com.jui.builders;

import com.jui.WebContext;
import com.jui.builders.ChartMapAttributes.ChartMapAttributesBuilder;
import com.jui.html.charts.BarChart;
import com.jui.html.charts.LinesChart;
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
	
	public ChartMapAttributesBuilder map() {
		
		return com.jui.builders.ChartMapAttributes.builder().context(context);
	}

}
