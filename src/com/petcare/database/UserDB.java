package com.petcare.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB {
	public boolean userLogin(String username, String password) throws SQLException {
			String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
			
			try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
				
				statement.setString(1, username);
				statement.setString(2, password);
				
				ResultSet result = statement.executeQuery();
				
				return result.next();
				//result.next() checks whether the query returned a row and returns either true or false
				
				/* Connection and PreparedStatement are automatically closed because they are
				declared as resources in the parentheses of the try-with-resources statement.*/
				
			} /*catch (SQLException e) {
				e.printStackTrace();
				return false;
			}*/
	}
	
	public boolean petOwnerRegister(String fName, String lName, String email, String phone_num, String username, String password) throws SQLException {
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
			} /*catch (SQLException e) {
				e.printStackTrace();
				return false;
			}*/
		}

	public boolean usernameExists(String username) throws SQLException {
		String sql = "SELECT id FROM Users WHERE username = ?";
		
		try (Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, username);
			
			ResultSet result = statement.executeQuery();
			
			return result.next();
			
		} /*catch (SQLException e) {
			e.printStackTrace();
			return false;
		}*/
	}
	
	public boolean emailExists(String email) throws SQLException {
		String sql = "SELECT id FROM Users WHERE email = ?";
		
		try (Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, email);
			
			ResultSet result = statement.executeQuery();
			
			return result.next();
			
		} /*catch (SQLException e) {
			e.printStackTrace();
			return false;
		}*/
	}
}
