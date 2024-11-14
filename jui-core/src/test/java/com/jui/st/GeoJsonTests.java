package com.jui.st;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jui.st.GeoJsonParser.Feature;
import com.jui.st.GeoJsonParser.FeatureCollection;

public class GeoJsonTests {
	
	@Test
	public void gson() throws FileNotFoundException {
		
        String geoJsonFilePath = "./src/test/resources/limits_IT_regions.geojson";
        
        Gson gson = new Gson();

        JsonReader reader = new JsonReader(new FileReader(geoJsonFilePath));
        FeatureCollection featureCollection = gson.fromJson(reader, FeatureCollection.class); // contains the whole reviews list
        
        System.out.println("Type: " + featureCollection.type);
        for (Feature feature : featureCollection.features) {
        	
        	
            System.out.println("Feature Type: " + feature.type);
            System.out.println("Geometry Type: " + feature.geometry.type);
            GeoJsonParser.printCoordinates(feature.geometry);
            System.out.println("Properties: " + feature.properties);
        }
    }
	
	@Test
	@Disabled
	public void duckdb() {
		
        String url = "jdbc:duckdb:"; // Connessione in-memory o file DB
        String geoJsonFilePath = "./src/test/resources/limits_IT_regions.geojson_modified";

        try (Connection conn = DriverManager.getConnection(url)) {
            // Caricamento del file GeoJSON in una tabella temporanea
            //String createTableQuery = "CREATE TABLE geojson_data AS SELECT * FROM read_json('" + geoJsonFilePath + "', columns = {features: 'JSON'})";
            
        	//SELECT json_extract(content, '$.features') AS features
            String createTableQuery = """
                    CREATE TABLE geojson_data AS
                    from read_json('%s');
                    """.formatted(geoJsonFilePath);

            try (PreparedStatement stmt = conn.prepareStatement(createTableQuery)) {
                stmt.execute();
                System.out.println("GeoJSON data loaded into the table.");
            }

            // Esecuzione di una query per estrarre informazioni specifiche dal GeoJSON
            String selectQuery = "SELECT " + 
            		"type, " +
            		"geometry as geometry," +
            		"json_extract(properties, '$.reg_name') as reg_name " +
            		"FROM geojson_data;";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery);
            		
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                
                    System.out.println(
                    		"Type: " + rs.getString("type") +  " Reg: " + rs.getString("reg_name"));
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
