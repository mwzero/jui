package com.jui.recipes;

import static com.jui.JuiApp.jui;

import com.jui.html.JuiContainer;

public class SideBarRecipe {
	
	public static void main(String... args) {
		
    	jui.sidebar
    		.ul("JUI Example")
	    		.add("MapZoomer", "compass", null, mapZoomer(), true)
	    		.add("MarkDown", "file-text", null, markdownTrials(), false);
    	
    	jui.start();
	}
	
	static JuiContainer mapZoomer() {
		
		JuiContainer page = jui.addContainer("mapZoomer");
		
		page.markdown("## Map Chart Example");
		page.divider("blue");
    	
    	var slider = page.input.slider("Zoom Level", 0, 19, 13);
    	var lat = page.input.input("lat", "40.85631", "latitude");
    	var lng = page.input.input("lng", "14.24641" ,"longitude");
    	
    	page.chart.map()
					.c_lat(lat)
					.c_lng(lng)
					.c_zoom(slider)
				.build();
    	
    	return page;
    	
	}
	
	static JuiContainer markdownTrials() {
		
		JuiContainer page = jui.addContainer("markdown");
		
		page.markdown("# Header 1");
		page.markdown("## Header 2");
		page.markdown("### Header 3");
		page.divider("blue");
		
		return page;
    	
	}
}
