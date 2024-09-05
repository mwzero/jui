package com.jui.html.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ImmutableList;

import com.jui.WebContext;
import com.jui.html.charts.map.MapAttributes.MapAttributesBuilder;
import com.st.JuiDataFrame;

import io.github.vmzakharov.ecdataframe.dataframe.DataFrame;
import io.github.vmzakharov.ecdataframe.dataframe.DfColumn;

public class ChartHandler {
	
	WebContext context;
	
	public ChartHandler(WebContext context) {
		this.context = context;
	}

	public LinesChart lines(JuiDataFrame df, int max_width, int max_height) {

		LinesChart lines = LinesChart.builder()
				.data(df)
				.max_height(max_height)
				.max_width(max_width)
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
		
		return com.jui.html.charts.map.MapAttributes.builder().context(context);
	}

	


}
