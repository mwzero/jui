package com.jui.html.elements.chart;

import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class ChartMap extends WebElement {
	
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
	WebElement c_lat;
	WebElement c_lng;
	WebElement c_zoom;
	
	Double[] location;
	
	public ChartMap() {
		super("ChartMap");
	}
	
	@Override
	public void preProcessBindingAndRelations() {
		
		if ( c_lat != null ) {
			
			lat(Double.parseDouble(c_lat.getValue()));
			this.webContext().addRelations(this.clientId(), "document.getElementById('%s').value=value.lat;".formatted(c_lat.clientId()));
			this.webContext().addRelations(c_lat.clientId(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(value, center.lng); 
					%s.panTo(latlng);
					""".formatted(this.clientId(), this.clientId()));
		}
		
		if ( c_lng != null ) {
			lng(Double.parseDouble(c_lng.getValue()));
			this.webContext().addRelations(this.clientId(), "document.getElementById('%s').value=value.lng;".formatted(c_lng.clientId()));
			this.webContext().addRelations(c_lng.clientId(), """
					var center = %s.getCenter(); 
					var latlng = L.latLng(center.lat, value); 
					%s.panTo(latlng);
					""".formatted(clientId(), clientId()));
			
		} 		
		if ( c_zoom != null ) {
			
			zoom(c_zoom.getIntValue());
			
			this.webContext().addRelations(c_zoom.clientId(), "%s.setZoom(value);".formatted(this.clientId()));
			this.webContext().addRelations(this.clientId(), "document.getElementById('%s').value=value.zoom;".formatted(c_zoom.clientId()));
			
		} 
	}
	
	@Override
	public String getPostData() {
		
		return "postData%s();".formatted(this.clientId());
	}
	
}
