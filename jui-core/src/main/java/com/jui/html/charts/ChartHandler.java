package com.jui.html.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.icu.impl.UResource.Array;
import com.jui.WebContext;
import com.jui.html.charts.map.MapAttributes.MapAttributesBuilder;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

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
	
	public BarChart bars(Table table, int max_width, int max_height) {

		List<List<String>> data = new ArrayList<>();
		
		for (Row row : table) {
			
            String col1Value = row.getString(0);
            int col2Value = row.getInt(1);

            data.add(
            		Arrays.asList(col1Value,col2Value + ""));
        }
		
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
