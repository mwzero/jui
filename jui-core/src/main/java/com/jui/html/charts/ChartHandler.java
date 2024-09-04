package com.jui.html.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ImmutableList;

import com.jui.WebContext;
import com.jui.html.charts.map.MapAttributes.MapAttributesBuilder;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.DfColumn;

public class ChartHandler {
	
	WebContext context;
	
	public ChartHandler(WebContext context) {
		this.context = context;
	}

	public LinesChart lines(DataFrame df, int max_width, int max_height) {

		List<List<String>> data = new ArrayList<>();
		
		df.forEach(row -> {
			
			List<String> newRow = new ArrayList<>();
			for ( DfColumn col : df.getColumns()) {
				
				newRow.add("" + row.getObject(col.getName()));
			}
			
			data.add(newRow);
        });

		LinesChart lines = LinesChart.builder()
				.data(data)
				.max_height(max_height)
				.max_width(max_width)
				.build();
		context.add(lines);
		
		return lines;
		
	}
	
	public BarChart bars(DataFrame df, int max_width, int max_height) {
		
		List<List<String>> data = new ArrayList<>();
		
		var cols = df.getColumns();
		
		df.forEach(row -> {
            data.add(
            		Arrays.asList(row.getString(cols.get(0).getName()),"" + row.getLong(cols.get(1).getName())));
            
        });
		
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
