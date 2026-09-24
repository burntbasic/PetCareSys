package com.petcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
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

	public List<Pet> getOwnerPets(User user) throws SQLException, DatabaseConfigException {
		List<Pet> pets = new ArrayList<>();

		String sql = """
				SELECT pet_id, owner_id, name, species, gender
				FROM Pets
				WHERE owner_id = ?
				ORDER BY name
				""";

		try(Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, user.getId());

			ResultSet result = statement.executeQuery();

			while(result.next()) {
				Pet pet = new Pet(
						result.getInt("pet_id"),
						result.getInt("owner_id"),
						result.getString("name"),
						result.getString("species"),
						result.getString("gender"));

				pets.add(pet);
			}
			return pets;
		}
	}
	
	public void updatePet(Pet pet, String name, String species, String gender) throws SQLException, DatabaseConfigException {

	    String sql = "UPDATE Pets SET name = ?, species = ?, gender = ? WHERE pet_id = ?";

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setString(1, name);
	        statement.setString(2, species);
	        statement.setString(3, gender);
	        statement.setInt(4, pet.getPetId());

	        int rowsUpdated = statement.executeUpdate();
	        
	        if (rowsUpdated == 0) {
	            throw new SQLException("Pet could not be updated.");
	        }
	    }
	}
	
	public void deletePet(Pet pet) throws SQLException, DatabaseConfigException {

	    String sql = "DELETE FROM Pets WHERE pet_id = ?";

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, pet.getPetId());

	        int rowsDeleted = statement.executeUpdate();

	        if (rowsDeleted == 0) {
	            throw new SQLException("Pet could not be deleted.");
	        }
	    }
	}
	
	public void addPet(User user, String name, String species, String gender) throws SQLException, DatabaseConfigException {

	    String sql = "INSERT INTO Pets (owner_id, name, species, gender) VALUES (?, ?, ?, ?)";

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());
	        statement.setString(2, name);
	        statement.setString(3, species);
	        statement.setString(4, gender);

	        int rowsInserted = statement.executeUpdate();

	        if (rowsInserted == 0) {
	            throw new SQLException("Pet could not be added.");
	        }
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
