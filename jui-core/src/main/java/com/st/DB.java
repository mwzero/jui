package com.st;

import java.sql.Connection;
import java.sql.DriverManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DB {
	
    public static Connection getConnection(String driver, String url, String username, String password) throws Exception {
    	
		try {
			Class.forName(driver);
			
		} catch (ClassNotFoundException e) {
			
			log.error("Impossible to load driver", e);
			throw e;
		}
		
		try { 
			Connection conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch ( Exception err) {
			
			log.error("Impossible to get a connection from [{}] with [{}]. Error[{}]", url, username, err.getLocalizedMessage());
			throw err;
			
		}
    }
    

}
