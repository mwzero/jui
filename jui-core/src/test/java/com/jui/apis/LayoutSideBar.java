package com.jui.apis;

import static com.jui.JuiApp.jui;

import com.jui.html.WebContainer;

public class LayoutSideBar {
	
	public static void main(String... args) {
		
    	jui.sidebar()
    		.ul("Unordered list Example")
	    		.add("MapZoomer", "compass", null, null, mapZoomer(), true)
	    		.add("MarkDown", "file-text", null, null, markdownTrials(), false);
    	
    	jui.server()
    		.layout("sidebar")
    		.start();
	}
	
	static WebContainer mapZoomer() {
		
		WebContainer page = jui.container("mapZoomer");
		
		page.markdown("## Map Chart Example");
		page.divider().color("blue");
    	
    	var slider = page.slider("Zoom Level", 0, 19, 13);
    	var lat = page.input("lat", "40.85631", "latitude");
    	var lng = page.input("lng", "14.24641" ,"longitude");
    	
    	page.map()
			.c_lat(lat)
			.c_lng(lng)
			.c_zoom(slider);
    	
    	return page;
    	
	}
	
	static WebContainer markdownTrials() {
		
		WebContainer page = jui.container("markdown");
		
		page.markdown("# Header 1");
		page.markdown("## Header 2");
		page.markdown("### Header 3");
		page.divider().color("blue");
		
		return page;
    	
	}
}
