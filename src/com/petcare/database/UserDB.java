package com.petcare.database;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB {
	public User userLogin(String username, String password) throws SQLException, DatabaseConfigException {
			String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
			
			try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
				
				statement.setString(1, username);
				statement.setString(2, password);
				
				ResultSet result = statement.executeQuery();
				
				if(result.next()) {
					User user = new User(
							result.getInt("user_id"),
							result.getString("first_name"),
							result.getString("last_name"),
							result.getString("email"),
							result.getString("phone"),
							result.getString("username"),
							result.getString("role")
							);
					return user;
					
					//result.next() checks whether the query returned a row and returns either true or false
				}
				
				return null;
				
				/* Connection and PreparedStatement are automatically closed because they are
				declared as resources in the parentheses of the try-with-resources statement.*/
				
			} 
	}
	
	public boolean petOwnerRegister(String fName, String lName, String email, String phone_num, String username, String password) throws SQLException, DatabaseConfigException {
			String sql = "INSERT INTO Users (first_name, last_name, email, phone, username, password) VALUES (?, ?, ?, ?, ?, ?)";
		
			try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
				
				statement.setString(1, fName);
				statement.setString(2, lName);
				statement.setString(3, email);
				statement.setString(4, phone_num);
				statement.setString(5, username);
				statement.setString(6, password);
				
				return statement.executeUpdate() > 0;
				
				/* Connection and PreparedStatement are automatically closed because they are
				declared as resources in the parentheses of the try-with-resources statement.*/
			}
		}

	//Registration Validation
	public boolean usernameExists(String username) throws SQLException, DatabaseConfigException {
		String sql = "SELECT user_id FROM Users WHERE username = ?";
		
		try (Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, username);
			
			ResultSet result = statement.executeQuery();
			
			return result.next();
			
		}
	}
	
	public boolean emailExists(String email) throws SQLException, DatabaseConfigException {
		String sql = "SELECT user_id FROM Users WHERE email = ?";
		
		try (Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, email);
			
			ResultSet result = statement.executeQuery();
			
			return result.next();
			
		}
	}
}
