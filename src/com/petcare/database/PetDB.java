package com.petcare.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;

public class PetDB {
	public int getPetCount(User user) throws SQLException, DatabaseConfigException {
		String sql = "SELECT COUNT(*) AS pet_count FROM Pets WHERE owner_id = ?";
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			return result.getInt("pet_count");
		}
	}
}
