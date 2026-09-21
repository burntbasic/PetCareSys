package com.petcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;

public class PetDAO {
	
	//Pet Owner UI
	public int getOwnerPetCount(User user) throws SQLException, DatabaseConfigException {
		String sql = "SELECT COUNT(*) AS pet_count FROM Pets WHERE owner_id = ?";
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			return result.getInt("pet_count");
		}
	}
	
	//Vet UI
	public int getVetPatientCount(User user) throws SQLException, DatabaseConfigException {
		String sql = """
				SELECT COUNT(DISTINCT a.pet_id) AS patient_count
				FROM Appointments a
				WHERE a.vet_id = ?
				AND a.status = "Scheduled"
				AND DATE(a.appointment_date) = CURDATE()
				""";
		
		try(Connection connection  = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
				
			statement.setInt(1, user.getId());
				
			ResultSet result = statement.executeQuery();
				
			result.next();
			return result.getInt("patient_count");
		}
	}
	
	//Admin UI
		public int getTotPetCount() throws SQLException, DatabaseConfigException {
			String sql = "SELECT COUNT(*) AS pet_count FROM Pets";
			
			try(Connection connection  = DBConnection.getConnection();
					PreparedStatement statement = connection.prepareStatement(sql)) {
						
					ResultSet result = statement.executeQuery();
						
					result.next();
					return result.getInt("pet_count");
				}
		}
}
