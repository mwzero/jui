package com.jui.html.charts;

import com.jui.WebContext;
import com.jui.html.charts.MapAttributes.MapAttributesBuilder;
import com.st.JuiDataFrame;


public class ChartHandler {
	
	WebContext context;
	
	public ChartHandler(WebContext context) {
		this.context = context;
	}

	public LinesChart lines(JuiDataFrame df, int max_width, int max_height) {

		LinesChart lines = LinesChart.builder()
				.data(df)
				.height(max_height)
				.width(max_width)
				.build();
		context.add(lines);
		
		return lines;
		
	}
	
	public BarChart bars(JuiDataFrame df, int max_width, int max_height) {
		
		BarChart lines = BarChart.builder()
				.data(df)
				.max_height(max_height)
				.max_width(max_width)
				.build();
		context.add(lines);
		
		return lines;
	}
	
	public MapAttributesBuilder map() {
		
		return com.jui.html.charts.MapAttributes.builder().context(context);
	}

	


}
