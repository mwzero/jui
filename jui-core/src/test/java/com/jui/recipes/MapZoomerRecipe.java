package com.jui.recipes;

import static com.jui.JuiCore.jui;
import static com.jui.JuiCore.startJuiServer;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.jui.html.charts.MapBuilder;

import picocli.CommandLine;


public class MapZoomerRecipe {
	
	public static void main(String... args) {
		
		jui.setTemplate("simple-bootstrap-1");
		jui.text.header("Map Chart Example", "blue");
    	
    	var slider = jui.input.slider("Zoom Level", 0, 19, 13);
    	var lat = jui.input.input("lat", "40.85631", "latitude");
    	var lng = jui.input.input("lng", "14.24641" ,"longitude");
    	
    	jui.chart.map(
    			MapBuilder.builder()
    				.c_lat(lat)
    				.c_lng(lng)
    				.c_zoom(slider).build()
    	);
    	
    	startJuiServer();
	}
	

}