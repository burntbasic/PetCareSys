package com.petcare.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static final Properties properties = new Properties();
	
	//static initialization block, ran when DBConnection class is loaded
	static {
		try {
			FileInputStream file = new FileInputStream("config/database.properties");
			
			properties.load(file);
			file.close();
			
		} catch (IOException e) { //Input-Output Exception
			e.printStackTrace(); //Prints error to console
			//add code to show error message dialog
		}
	}
	public static Connection getConnection() throws SQLException {
		/*this static method is declared for other objects to connect to the database
		without needing to instantiate a DBConnection object (static)*/
		//"throws" lets the caller of this method handle the SQLException (in a try-catch block)
		
		final String URL = properties.getProperty("db.url");
		final String user = properties.getProperty("db.user");
		final String password = properties.getProperty("db.password");
		
		return DriverManager.getConnection(URL, user, password);
	}
}
	
	/*
	Maintaining only a single database connection (singleton)
	
	 private static Connection connection;
	 
	 public static Connection getConnection() throws SQLException {
	 
	 	if (connection == null || connection.isClosed()) {
	 		connection = DriverManager.getConnection(URL, user, password);
	 	}
	 	
	 	return connection;
	 }
	 */
	

