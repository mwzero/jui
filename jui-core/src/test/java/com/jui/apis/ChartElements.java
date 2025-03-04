package com.jui.apis;

import static com.jui.JuiApp.jui;

public class ChartElements {
	
	public static void main(String... args) {
		
		jui.markdown("## Map Chart Example");
		jui.divider().color("blue");
    	
    	var slider = jui.slider("Zoom Level", 0, 19, 13);
    	var lat = jui.input("lat", "40.85631", "latitude");
    	var lng = jui.input("lng", "14.24641" ,"longitude");
    	
    	jui.map()
			.c_lat(lat)
			.c_lng(lng)
			.c_zoom(slider);
    	
    	jui.server()
			.start();
	}

}
