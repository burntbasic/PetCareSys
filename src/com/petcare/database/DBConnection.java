package com.petcare.database;
import com.petcare.exception.DatabaseConfigException;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static final Properties properties = new Properties();
	
	private static IOException configError;
	
	//static initialization block, ran when DBConnection class is loaded
	static {
		try {
			FileInputStream file = new FileInputStream("config/database.properties");
			
			properties.load(file);
			file.close();
			
		} catch (IOException e) { //Input-Output Exception
			configError = e;
			//e.printStackTrace(); //Prints error to console
			//add code to show error message dialog
		}
	}
	public static Connection getConnection() throws SQLException, DatabaseConfigException {
		/*this static method is declared for other objects to connect to the database
		without needing to instantiate a DBConnection object (static)*/
		//"throws" lets the caller of this method handle the SQLException (in a try-catch block)
		
		if (configError!=null) {
			throw new DatabaseConfigException("Could not load config/database.properties.", configError);
		}
		
		final String URL = properties.getProperty("db.url");
		final String user = properties.getProperty("db.user");
		final String password = properties.getProperty("db.password");
		
		if (URL == null || user == null || password == null) {
			throw new DatabaseConfigException("Database Configuration is incomplete");
		}
		
		return DriverManager.getConnection(URL, user, password);
	}
}
	

