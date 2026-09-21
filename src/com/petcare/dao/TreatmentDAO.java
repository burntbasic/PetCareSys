package com.petcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;

public class TreatmentDAO {
	//Vet UI
	public int getPendingCount(User user) throws SQLException, DatabaseConfigException {
		String sql = """
				SELECT COUNT(*) AS treatment_count
				FROM Treatments
				WHERE vet_id = ?
				AND status = 'Pending'
				""";
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			return result.getInt("treatment_count");
		}
	}

}
