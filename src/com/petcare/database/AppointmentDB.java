package com.petcare.database;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;  

public class AppointmentDB {
	public List<Appointment> getUpcomingAppointments(User user) throws SQLException, DatabaseConfigException {
		
		List<Appointment> appointments = new ArrayList<>();
		
		String sql = """
				SELECT
					a.appointment_id,
					p.name AS pet_name,
					CONCAT(u.first_name, ' ', u.last_name) AS vet_name,
					a.appointment_date,
					a.status,
					a.reason
				FROM Appointments a
				JOIN Pets p
				ON a.pet_id = p.pet_id
				JOIN Users u
				ON a.vet_id = u.user_id
				AND u.role = 'VET'
				WHERE p.owner_id = ?
				AND a.appointment_date >= NOW()
				ORDER BY a.appointment_date
				""";
		//"AND u.role = 'VET'" is extra validation; vet_id should already reference a VET
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				Appointment appointment = new Appointment(
						result.getInt("appointment_id"),
						result.getString("pet_name"),
						result.getString("vet_name"),
						result.getTimestamp("appointment_date"),
						result.getString("status"),
						result.getString("reason"));
				
				appointments.add(appointment);
			}
			return appointments;
		}
	}
	
	public int getUpcomingCount(User user) throws SQLException, DatabaseConfigException {
		String sql = """
		        SELECT COUNT(*) AS upcoming_count
		        FROM Appointments a
		        JOIN Pets p
		        ON a.pet_id = p.pet_id
		        WHERE p.owner_id = ?
		        AND a.appointment_date >= NOW()
		        """;
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			return result.getInt("upcoming_count");
		}
	}
	
	public int getCompletedCount(User user) throws SQLException, DatabaseConfigException {
		String sql = """
		        SELECT COUNT(*) AS completed_count
		        FROM Appointments a
		        JOIN Pets p
		        ON a.pet_id = p.pet_id
		        WHERE p.owner_id = ?
		        AND a.status = 'Completed'
		        """;
		
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setInt(1, user.getId());
			
			ResultSet result = statement.executeQuery();
			
			result.next();
			return result.getInt("completed_count");
		}
	}
}
