package com.jui.recipes;

import static com.jui.JuiApp.jui;

public class MapZoomerRecipe {
	
	public static void main(String... args) {
		
		//jui.text.header("Map Chart Example", "blue");
		jui.markdown("## Map Chart Example");
		jui.divider("blue");
    	
    	var slider = jui.input.slider("Zoom Level", 0, 19, 13);
    	var lat = jui.input.input("lat", "40.85631", "latitude");
    	var lng = jui.input.input("lng", "14.24641" ,"longitude");
    	
    	jui.chart.map()
					.c_lat(lat)
					.c_lng(lng)
					.c_zoom(slider)
				.build();
    	
    	jui.start();
	}
	

}
