package com.jui.st;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class GeoJsonParser {

	public static class FeatureCollection {
		
		@SerializedName("type") String type;

		@SerializedName("features")
		List<Feature> features;

		// Getters and setters
	}

	public static class Feature {
		@SerializedName("type") String type;

		@SerializedName("geometry") Geometry geometry;

		@SerializedName("properties") Object properties; // Can be a Map or a custom class

		// Getters and setters
	}

	@Getter
	public static class Geometry {
		
		@SerializedName("type") String type;
		@SerializedName("coordinates") Object coordinates; // Use a nested list for complex types

		// Getters and setters
	}
	
	public static void printCoordinates(Geometry geometry) {
		
	    if ("Point".equals(geometry.getType())) {
	        List<Double> pointCoordinates = (List<Double>) geometry.getCoordinates();
	        System.out.println("Point coordinates: " + pointCoordinates);
	    } else if ("LineString".equals(geometry.getType()) || "MultiPoint".equals(geometry.getType())) {
	        List<List<Double>> lineOrMultiPointCoordinates = (List<List<Double>>) geometry.getCoordinates();
	        System.out.println("LineString/MultiPoint coordinates: " + lineOrMultiPointCoordinates);
	    } else if ("Polygon".equals(geometry.getType()) || "MultiLineString".equals(geometry.getType())) {
	        List<List<List<Double>>> polygonOrMultiLineCoordinates = (List<List<List<Double>>>) geometry.getCoordinates();
	        System.out.println("Polygon/MultiLineString coordinates: " + polygonOrMultiLineCoordinates);
	    } else if ("MultiPolygon".equals(geometry.getType())) {
	        List<List<List<List<Double>>>> multiPolygonCoordinates = (List<List<List<List<Double>>>>) geometry.getCoordinates();
	        System.out.println("MultiPolygon coordinates: " + multiPolygonCoordinates);
	    } else {
	        System.out.println("Tipo di geometria non riconosciuto: " + geometry.getType());
	    }
	}

	
	
}
