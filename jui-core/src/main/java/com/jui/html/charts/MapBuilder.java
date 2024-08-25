package com.jui.html.charts;

import com.jui.html.WebComponent;

import lombok.Builder;

@Builder
public class MapBuilder {
	
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
	
}
