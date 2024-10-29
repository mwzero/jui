package com.st;

import java.sql.Connection;
import java.sql.DriverManager;

import lombok.extern.java.Log;

@Log
public class DB {
	
    public static Connection getConnection(String driver, String url, String username, String password) throws Exception {
    	
		try {
			Class.forName(driver);
			
		} catch (ClassNotFoundException e) {
			
			log.severe(e.getLocalizedMessage());
			throw e;
		}
		
		try { 
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch ( Exception err) {
			
			log.severe("Impossible to get a connection from [%s] with [%s]. Error[%s]".formatted(url, username, err.getLocalizedMessage()));
			throw err;
			
		}
    }
    

}
