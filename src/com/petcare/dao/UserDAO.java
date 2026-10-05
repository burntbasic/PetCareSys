package com.petcare.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;

public class UserDAO {

    public User userLogin(String username, String password) throws SQLException, DatabaseConfigException {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
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

    public boolean petOwnerRegister(String fName, String lName, String email, String phone, String username, String password) throws SQLException, DatabaseConfigException {
        String sql = "INSERT INTO Users (first_name, last_name, email, phone, username, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fName);
            statement.setString(2, lName);
            statement.setString(3, email);
            statement.setString(4, phone);
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

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            return result.next();
        }
    }

    public boolean emailExists(String email) throws SQLException, DatabaseConfigException {
        String sql = "SELECT user_id FROM Users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            ResultSet result = statement.executeQuery();

            return result.next();
        }
    }

    //Pet Owner - Appointments Panel
    public List<User> getVeterinarians() throws SQLException, DatabaseConfigException {

        List<User> vets = new ArrayList<>();

        String sql = """
				SELECT user_id, first_name, last_name
				FROM Users
				WHERE role = 'VET'
				ORDER BY CONCAT(first_name, ' ', last_name)
				""";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                User vet = new User(
                        result.getInt("user_id"),
                        result.getString("first_name"),
                        result.getString("last_name"));
                vets.add(vet);
            }
        }
        return vets;
    }

    //Admin UI
    public int getOwnerCount() throws SQLException, DatabaseConfigException {
        String sql = "SELECT COUNT(*) AS pet_owner_count FROM Users WHERE role = 'PET_OWNER'";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            result.next();
            return result.getInt("pet_owner_count");
        }
    }

    public int getVetCount() throws SQLException, DatabaseConfigException {
        String sql = "SELECT COUNT(*) AS vet_count FROM Users WHERE role = 'VET'";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            result.next();
            return result.getInt("vet_count");
        }
    }

    public List<User> getAllUsers() throws SQLException, DatabaseConfigException {

        List<User> users = new ArrayList<>();

        String sql = """
	            SELECT user_id, first_name, last_name, email, phone, username, role
	            FROM Users
	            ORDER BY user_id
	            """;

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                User user = new User(
                        result.getInt("user_id"),
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getString("email"),
                        result.getString("phone"),
                        result.getString("username"),
                        result.getString("role"));

                users.add(user);
            }
        }
        return users;
    }

    public boolean updateUser(int userId, String fName, String lName, String email, String phone, String username, String role) throws SQLException, DatabaseConfigException {

        String sql = """
	            UPDATE Users
	            SET first_name = ?,
	                last_name = ?,
	                email = ?,
	                phone = ?,
	                username = ?,
	                role = ?
	            WHERE user_id = ?
	            """;

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fName);
            statement.setString(2, lName);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, username);
            statement.setString(6, role);
            statement.setInt(7, userId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException, DatabaseConfigException {

        String sql = "DELETE FROM Users WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean addUser(String fName, String lName, String email, String phone, String username, String password, String role) throws SQLException, DatabaseConfigException {

        String sql = """
	            INSERT INTO Users
	            (first_name, last_name, email, phone, username, password, role)
	            VALUES (?, ?, ?, ?, ?, ?, ?)
	            """;

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fName);
            statement.setString(2, lName);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.setString(5, username);
            statement.setString(6, password);
            statement.setString(7, role);

            return statement.executeUpdate() > 0;
        }
    }

    public List<User> getPetOwners() throws SQLException, DatabaseConfigException {

        List<User> owners = new ArrayList<>();

        String sql = """
	            SELECT user_id, first_name, last_name
	            FROM Users
	            WHERE role = 'PET_OWNER'
	            ORDER BY CONCAT(first_name, ' ', last_name)
	            """;

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                User owner = new User(
                        result.getInt("user_id"),
                        result.getString("first_name"),
                        result.getString("last_name"));

                owners.add(owner);
            }
        }
        return owners;
    }

    public boolean usernameExistsForOtherUser(String username, int userId) throws SQLException, DatabaseConfigException {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ? AND user_id != ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }

    public boolean emailExistsForOtherUser(String email, int userId) throws SQLException, DatabaseConfigException {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND user_id != ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }
}
