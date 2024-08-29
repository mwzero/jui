package com.jui.html.charts;

import java.util.List;

import com.jui.WebContext;
import com.jui.html.charts.map.MapAttributes.MapAttributesBuilder;

public class ChartHandler {
	
	WebContext context;
	
	public ChartHandler(WebContext context) {
		this.context = context;
	}

	public LinesChart lines(List<List<String>> data, int max_width, int max_height) {

		LinesChart lines = LinesChart.builder()
				.data(data)
				.max_height(max_height)
				.max_width(max_width)
				.build();
		context.add(lines);
		
		return lines;
		
	}
	
	public BarChart bars(List<List<String>> data, int max_width, int max_height) {

		BarChart lines = BarChart.builder()
				.data(data)
				.max_height(max_height)
				.max_width(max_width)
				.build();
		context.add(lines);
		
		return lines;
		
	}
	
	public MapAttributesBuilder map() {
		
		return com.jui.html.charts.map.MapAttributes.builder().context(context);
	}


}
