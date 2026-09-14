package com.petcare.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDB {
	public boolean userLogin(String username, String password) {
			String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
			
			try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
				
				statement.setString(1, username);
				statement.setString(2, password);
				
				ResultSet result = statement.executeQuery();
				
				return result.next();
				//result.next() checks whether the query returned a row and returns either true or false
				
				/* Connection and PreparedStatement are automatically closed because they are
				declared as resources in the parentheses of the try-with-resources statement.*/
				
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
	}

}
