package com.jui.builders;

import com.jui.WebContext;
import com.jui.html.WebComponent;
import com.jui.html.charts.MapChart;

import lombok.Builder;

@Builder
public class ChartMapAttributes {
	
	WebContext context;
	MapChart mapChart;
	
	boolean scaleControl;
	boolean zoomControl;
	boolean mapBounds;
	
	double minLat;
	double maxLat;
	double minLon;
	double maxLon;
	
	//
	double lat;
	double lng;
	int zoom;
	
	//
	WebComponent c_lat;
	WebComponent c_lng;
	WebComponent c_zoom;
	
	Double[] location;
	
	
	
    public static ChartMapAttributesBuilder builder() {
        return new CustomMapAttributesBuilder();
    }

    private static class CustomMapAttributesBuilder extends ChartMapAttributesBuilder {
    	
        @Override
        public ChartMapAttributes build() {
        	
    		super.mapChart = new MapChart();
    		super.context.add(super.mapChart);
    		
    		if ( super.c_lat != null ) {
    			super.mapChart.setLat(Double.parseDouble(super.c_lat.getValue()));
    			super.context.addRelations(super.mapChart.getKey(), "document.getElementById('%s').value=value.lat".formatted(super.c_lat.getKey()));
    			super.context.addRelations(super.c_lat.getKey(), """
    					var center = %s.getCenter(); 
    					var latlng = L.latLng(value, center.lng); 
    					%s.panTo(latlng);
    					""".formatted(super.mapChart.getKey(), super.mapChart.getKey()));
    		} else 
    			super.mapChart.setLat(super.lat);
    		
    		if ( super.c_lng != null ) {
    			super.mapChart.setLng(Double.parseDouble(super.c_lng.getValue()));
    			super.context.addRelations(super.mapChart.getKey(), "document.getElementById('%s').value=value.lng".formatted(super.c_lng.getKey()));
    			super.context.addRelations(super.c_lng.getKey(), """
    					var center = %s.getCenter(); 
    					var latlng = L.latLng(center.lat, value); 
    					%s.panTo(latlng);
    					""".formatted(super.mapChart.getKey(), super.mapChart.getKey()));
    			
    		} else 
    			super.mapChart.setLng(super.lng);
    		
    		if ( super.c_zoom != null ) {
    			
    			super.mapChart.setZoom(super.c_zoom.getIntValue());
    			super.context.addRelations(super.c_zoom.getKey(), 
    					"%s.setZoom(value)".formatted(super.mapChart.getKey()));
    			super.context.addRelations(super.mapChart.getKey(), "document.getElementById('%s').value=value.zoom".formatted(super.c_zoom.getKey()));
    		} else 
    			super.mapChart.setZoom(super.zoom);
        		
            return super.build();
        }
    }
    
    
    
	
}
