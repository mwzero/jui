package com.jui.html.tags.chart;

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
	
	@Override
	public void preProcessBindingAndRelations() {
		
		if ( c_lat != null ) {
			
			lat(Double.parseDouble(c_lat.getValue()));
			this.getWebContext().addRelations(this.getKey(), "document.getElementById('%s').value=value.lat;".formatted(c_lat.getKey()));
			this.getWebContext().addRelations(c_lat.getKey(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(value, center.lng); 
					%s.panTo(latlng);
					""".formatted(this.getKey(), this.getKey()));
		}
		
		if ( c_lng != null ) {
			lng(Double.parseDouble(c_lng.getValue()));
			this.getWebContext().addRelations(this.getKey(), "document.getElementById('%s').value=value.lng;".formatted(c_lng.getKey()));
			this.getWebContext().addRelations(c_lng.getKey(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(center.lat, value); 
					%s.panTo(latlng);
					""".formatted(getKey(), getKey()));
			
		} 		
		if ( c_zoom != null ) {
			
			zoom(c_zoom.getIntValue());
			
			this.getWebContext().addRelations(c_zoom.getKey(), "%s.setZoom(value);".formatted(this.getKey()));
			this.getWebContext().addRelations(this.getKey(), "document.getElementById('%s').value=value.zoom;".formatted(c_zoom.getKey()));
			
		} 
	}
	
	@Override
	public String getPostData() {
		
		return "postData%s();".formatted(this.getKey());
	}
	
}
