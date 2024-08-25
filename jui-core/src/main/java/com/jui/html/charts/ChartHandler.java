package com.jui.html.charts;

import java.util.List;

import com.jui.WebContext;

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
	
	public MapChart map(MapBuilder builder) {

		MapChart mapChart = new MapChart();
		context.add(mapChart);
		
		if ( builder.c_lat != null ) {
			mapChart.setLat(Double.parseDouble(builder.c_lat.getValue()));
			context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.lat".formatted(builder.c_lat.getKey()));
			context.addRelations(builder.c_lat.getKey(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(value, center.lng); 
					%s.panTo(latlng);
					""".formatted(mapChart.getKey(), mapChart.getKey()));
		} else 
			mapChart.setLat(builder.lat);
		
		if ( builder.c_lng != null ) {
			mapChart.setLng(Double.parseDouble(builder.c_lng.getValue()));
			context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.lng".formatted(builder.c_lng.getKey()));
			context.addRelations(builder.c_lng.getKey(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(center.lat, value); 
					%s.panTo(latlng);
					""".formatted(mapChart.getKey(), mapChart.getKey()));
			
		} else 
			mapChart.setLng(builder.lng);
		
		if ( builder.c_zoom != null ) {
			
			mapChart.setZoom(builder.c_zoom.getIntValue());
			context.addRelations(builder.c_zoom.getKey(), 
					"%s.setZoom(value)".formatted(mapChart.getKey()));
			context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.zoom".formatted(builder.c_zoom.getKey()));
		} else 
			mapChart.setZoom(builder.zoom);
		
		
		
		return mapChart;
		
	}
	

}
