package com.jui.st;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DuckDBTests {
	
	public static void main(String[] args) {
		
        String url = "jdbc:duckdb:"; // Connessione in-memory o file DB
        String geoJsonFilePath = "./src/test/resources/limits_IT_regions.geojson";

        try (Connection conn = DriverManager.getConnection(url)) {
            // Caricamento del file GeoJSON in una tabella temporanea
            //String createTableQuery = "CREATE TABLE geojson_data AS SELECT * FROM read_json('" + geoJsonFilePath + "', columns = {features: 'JSON'})";
            
        	//SELECT json_extract(content, '$.features') AS features
            String createTableQuery = """
                    CREATE TABLE geojson_data AS
                    from read_json('%s', columns={features:'JSON'});
                    """.formatted(geoJsonFilePath);

            try (PreparedStatement stmt = conn.prepareStatement(createTableQuery)) {
                stmt.execute();
                System.out.println("GeoJSON data loaded into the table.");
            }

            // Esecuzione di una query per estrarre informazioni specifiche dal GeoJSON
            String selectQuery = "SELECT " + 
            		"json_extract(features, '$[*].type') as reg_name, " +
            		"json_extract(features, '$[*].geometry.type') as type," +
            		"json_extract(features, '$[*].geometry.coordinates') as coordinates " +
            		"FROM geojson_data;";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery);
            		
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                
                    System.out.println("Reg: " + rs.getString("reg_name"));
                    System.out.println("type" + rs.getString("type"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
