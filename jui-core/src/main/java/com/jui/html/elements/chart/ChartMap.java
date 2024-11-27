package com.jui.html.elements.chart;

import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class ChartMap extends WebComponent {
	
	boolean scaleControl;
	boolean zoomControl;
	boolean mapBounds;
	
	double minLat;
	double maxLat;
	double minLon;
	double maxLon;
	
	double lat;
	double lng;
	Integer zoom;
	
	//
	WebComponent c_lat;
	WebComponent c_lng;
	WebComponent c_zoom;
	
	Double[] location;
	
	public ChartMap() {
		super("ChartMap");
	}
	
	@Override
	public void preProcessBindingAndRelations() {
		
		if ( c_lat != null ) {
			
			lat(Double.parseDouble(c_lat.getValue()));
			this.webContext().addRelations(this.key(), "document.getElementById('%s').value=value.lat;".formatted(c_lat.key()));
			this.webContext().addRelations(c_lat.key(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(value, center.lng); 
					%s.panTo(latlng);
					""".formatted(this.key(), this.key()));
		}
		
		if ( c_lng != null ) {
			lng(Double.parseDouble(c_lng.getValue()));
			this.webContext().addRelations(this.key(), "document.getElementById('%s').value=value.lng;".formatted(c_lng.key()));
			this.webContext().addRelations(c_lng.key(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(center.lat, value); 
					%s.panTo(latlng);
					""".formatted(key(), key()));
			
		} 		
		if ( c_zoom != null ) {
			
			zoom(c_zoom.getIntValue());
			
			this.webContext().addRelations(c_zoom.key(), "%s.setZoom(value);".formatted(this.key()));
			this.webContext().addRelations(this.key(), "document.getElementById('%s').value=value.zoom;".formatted(c_zoom.key()));
			
		} 
	}
	
	@Override
	public String getPostData() {
		
		return "postData%s();".formatted(this.key());
	}
	
}
