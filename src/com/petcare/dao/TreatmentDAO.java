package com.petcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Treatment;
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
	
	public List<Treatment> getVetMedicalRecords(User user) throws SQLException, DatabaseConfigException {

	    List<Treatment> treatments = new ArrayList<>();

	    String sql = """
	            SELECT
	                t.treatment_id,
	                t.appointment_id,
	                t.vet_id,
	                p.name AS pet_name,
	                CONCAT(o.first_name, ' ', o.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                t.diagnosis,
	                t.treatment_description,
	                t.medication,
	                t.status,
	                t.treatment_date
	            FROM Treatments t
	            JOIN Appointments a
	            ON t.appointment_id = a.appointment_id
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users o
	            ON p.owner_id = o.user_id
	            JOIN Users v
	            ON t.vet_id = v.user_id
	            WHERE t.vet_id = ?
	            ORDER BY t.treatment_date DESC
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	        	Treatment treatment = new Treatment(
	        	        result.getInt("treatment_id"),
	        	        result.getInt("appointment_id"),
	        	        result.getInt("vet_id"),
	        	        result.getString("pet_name"),
	        	        result.getString("owner_name"),
	        	        result.getString("vet_name"),
	        	        result.getString("diagnosis"),
	        	        result.getString("treatment_description"),
	        	        result.getString("medication"),
	        	        result.getString("status"),
	        	        result.getTimestamp("treatment_date"));

	            treatments.add(treatment);
	        }
	    }

	    return treatments;
	}
	
	public List<Treatment> getVetTreatments(User user) throws SQLException, DatabaseConfigException {

	    List<Treatment> treatments = new ArrayList<>();

	    String sql = """
	            SELECT
	                t.treatment_id,
	                t.appointment_id,
	                t.vet_id,
	                p.name AS pet_name,
	                CONCAT(o.first_name, ' ', o.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                t.diagnosis,
	                t.treatment_description,
	                t.medication,
	                t.status,
	                t.treatment_date
	            FROM Treatments t
	            JOIN Appointments a
	            ON t.appointment_id = a.appointment_id
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users o
	            ON p.owner_id = o.user_id
	            JOIN Users v
	            ON t.vet_id = v.user_id
	            WHERE t.vet_id = ?
	            ORDER BY t.treatment_date DESC
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	            Treatment treatment = new Treatment(
	                    result.getInt("treatment_id"),
	                    result.getInt("appointment_id"),
	                    result.getInt("vet_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getString("diagnosis"),
	                    result.getString("treatment_description"),
	                    result.getString("medication"),
	                    result.getString("status"),
	                    result.getTimestamp("treatment_date"));

	            treatments.add(treatment);
	        }
	    }

	    return treatments;
	}
	
	public boolean hasTreatment(int appointmentId) throws SQLException, DatabaseConfigException {

	    String sql = """
	            SELECT COUNT(*)
	            FROM Treatments
	            WHERE appointment_id = ?
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, appointmentId);

	        ResultSet result = statement.executeQuery();

	        if(result.next()) {
	            return result.getInt(1) > 0;
	        }
	    }

	    return false;
	}
	
	public void addTreatment(int appointmentId, int vetId, String diagnosis,
	        String treatmentDescription, String medication, String status) throws SQLException, DatabaseConfigException {

	    String sql = """
	            INSERT INTO Treatments
	                (appointment_id, vet_id, diagnosis, treatment_description,
	                 medication, status, treatment_date)
	            VALUES (?, ?, ?, ?, ?, ?, NOW())
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, appointmentId);
	        statement.setInt(2, vetId);
	        statement.setString(3, diagnosis);
	        statement.setString(4, treatmentDescription);
	        statement.setString(5, medication);
	        statement.setString(6, status);

	        int rowsInserted = statement.executeUpdate();

	        if(rowsInserted == 0) {
	            throw new SQLException("Treatment could not be added.");
	        }
	    }
	}
	
	public void updateTreatment(Treatment treatment, String diagnosis, String treatmentDescription, String medication, String status) throws SQLException, DatabaseConfigException {

	    String sql = """
	            UPDATE Treatments
	            SET diagnosis = ?,
	                treatment_description = ?,
	                medication = ?,
	                status = ?
	            WHERE treatment_id = ?
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setString(1, diagnosis);
	        statement.setString(2, treatmentDescription);
	        statement.setString(3, medication);
	        statement.setString(4, status);
	        statement.setInt(5, treatment.getTreatmentId());

	        statement.executeUpdate();
	    }
	}
	
	//Pet Owner UI
	public List<Treatment> getPetMedicalRecords(User user, int petId) throws SQLException, DatabaseConfigException {

	    List<Treatment> treatments = new ArrayList<>();

	    String sql = """
	            SELECT
	                t.treatment_id,
	                t.appointment_id,
	                t.vet_id,
	                p.name AS pet_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                t.diagnosis,
	                t.treatment_description,
	                t.medication,
	                t.status,
	                t.treatment_date
	            FROM Treatments t
	            JOIN Appointments a
	            ON t.appointment_id = a.appointment_id
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users v
	            ON t.vet_id = v.user_id
	            WHERE p.owner_id = ?
	            AND p.pet_id = ?
	            ORDER BY t.treatment_date DESC
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());
	        statement.setInt(2, petId);

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	            Treatment treatment = new Treatment(
	                    result.getInt("treatment_id"),
	                    result.getInt("appointment_id"),
	                    result.getInt("vet_id"),
	                    result.getString("pet_name"),
	                    result.getString("vet_name"),
	                    result.getString("diagnosis"),
	                    result.getString("treatment_description"),
	                    result.getString("medication"),
	                    result.getString("status"),
	                    result.getTimestamp("treatment_date"));

	            treatments.add(treatment);
	        }
	    }

	    return treatments;
	}

}
