package com.petcare.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;  

import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.*;

public class AppointmentDAO {

	//Pet-Owner UI
	public List<Appointment> getOwnerUpcoming(User user) throws SQLException, DatabaseConfigException {

		List<Appointment> appointments = new ArrayList<>();

		String sql = """
				SELECT
					a.appointment_id,
					p.name AS pet_name,
					CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
					CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
					a.appointment_date,
					a.status,
					a.reason
				FROM Appointments a
				JOIN Pets p
				ON a.pet_id = p.pet_id
				JOIN Users u
				ON p.owner_id = u.user_id
				JOIN Users v
				ON a.vet_id = v.user_id
				AND v.role = 'VET'
				WHERE p.owner_id = ?
				AND a.appointment_date >= NOW()
				AND a.status = 'Scheduled'
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
						result.getString("owner_name"),
						result.getString("vet_name"),
						result.getTimestamp("appointment_date"),
						result.getString("status"),
						result.getString("reason"));

				appointments.add(appointment);
			}
			return appointments;
		}
	}

	public int getOwnerUpcomingCount(User user) throws SQLException, DatabaseConfigException {
		String sql = """
				SELECT COUNT(*) AS upcoming_count
				FROM Appointments a
				JOIN Pets p
				ON a.pet_id = p.pet_id
				WHERE p.owner_id = ?
				AND a.appointment_date >= NOW()
				AND a.status = "Scheduled"
				""";

		try(Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, user.getId());

			ResultSet result = statement.executeQuery();

			result.next();
			return result.getInt("upcoming_count");
		}
	}

	public int getOwnerCompletedCount(User user) throws SQLException, DatabaseConfigException {
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

	public void cancelAppointment(Appointment appointment) throws SQLException, DatabaseConfigException {

		String sql = "UPDATE Appointments SET status = 'Cancelled' WHERE appointment_id = ?";

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, appointment.getAppointmentId());

			int rowsUpdated = statement.executeUpdate();

			if (rowsUpdated == 0) {
				throw new SQLException("Appointment could not be cancelled.");
			}
		}
	}
	
	public List<Appointment> getOwnerPast(User user) throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.appointment_date,
	                a.status,
	                a.reason
	            FROM Appointments a
	            JOIN Pets p
	                ON a.pet_id = p.pet_id
	            JOIN Users u
	                ON p.owner_id = u.user_id
	            JOIN Users v
	                ON a.vet_id = v.user_id
	                AND v.role = 'VET'
	            WHERE p.owner_id = ?
	                AND a.status = 'Completed'
	            ORDER BY a.appointment_date DESC
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while (result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}
	
	public List<Appointment> getOwnerCancelled(User user) throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.appointment_date,
	                a.status,
	                a.reason
	            FROM Appointments a
	            JOIN Pets p
	                ON a.pet_id = p.pet_id
	            JOIN Users u
	                ON p.owner_id = u.user_id
	            JOIN Users v
	                ON a.vet_id = v.user_id
	                AND v.role = 'VET'
	            WHERE p.owner_id = ?
	                AND a.status = 'Cancelled'
	            ORDER BY a.appointment_date DESC
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while (result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}
	
	public void bookAppointment(int petId, int vetId, Timestamp appointmentDate, String reason) throws SQLException, DatabaseConfigException {

	    String sql = """
	            INSERT INTO Appointments
	                (pet_id, vet_id, appointment_date, status, reason)
	            VALUES (?, ?, ?, 'Scheduled', ?)
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, petId);
	        statement.setInt(2, vetId);
	        statement.setTimestamp(3, appointmentDate);
	        statement.setString(4, reason);

	        int rowsInserted = statement.executeUpdate();

	        if (rowsInserted == 0) {
	            throw new SQLException("Appointment could not be booked.");
	        }
	    }
	}
	
	public boolean isVetAvailable(int vetId, Timestamp appointmentDate)
	        throws SQLException, DatabaseConfigException {

	    String sql = """
	            SELECT COUNT(*)
	            FROM Appointments
	            WHERE vet_id = ?
	            AND appointment_date = ?
	            AND status = 'Scheduled'
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, vetId);
	        statement.setTimestamp(2, appointmentDate);

	        ResultSet result = statement.executeQuery();

	        if (result.next()) {
	            return result.getInt(1) == 0;
	        }
	    }

	    return false;
	}
	
	public List<Timestamp> getBookedTimes(int vetId, LocalDate date) throws SQLException, DatabaseConfigException {

	    List<Timestamp> bookedTimes = new ArrayList<>();

	    String sql = """
	            SELECT appointment_date
	            FROM Appointments
	            WHERE vet_id = ?
	            AND DATE(appointment_date) = ?
	            AND status = 'Scheduled'
	            ORDER BY appointment_date
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, vetId);
	        statement.setDate(2, Date.valueOf(date));

	        ResultSet result = statement.executeQuery();

	        while (result.next()) {
	            bookedTimes.add(result.getTimestamp("appointment_date"));
	        }
	    }

	    return bookedTimes;
	}

	//Vet UI	
	public int getVetTodaysCount(User user) throws SQLException, DatabaseConfigException {
		String sql = """
				SELECT COUNT(*) AS appointment_count
				FROM Appointments
				WHERE vet_id = ?
				AND DATE(appointment_date) = CURDATE();
				""";

		try(Connection connection  = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, user.getId());

			ResultSet result = statement.executeQuery();

			result.next();
			return result.getInt("appointment_count");
		}
	}

	public List<Appointment> getVetTodays(User user) throws SQLException, DatabaseConfigException {

		List<Appointment> appointments = new ArrayList<>();

		String sql = """
				SELECT            
				a.appointment_id,
				a.appointment_date, 
				p.name AS pet_name,
				         CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
				         CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
				         a.reason,
				         a.status
				         FROM Appointments a
				         JOIN Pets p ON a.pet_id = p.pet_id
				         JOIN Users u ON p.owner_id = u.user_id
				         JOIN Users v ON a.vet_id = v.user_id AND v.role = 'VET'
				         WHERE a.vet_id = ?
				         AND DATE(a.appointment_date) = CURDATE()
				         AND a.status = 'Scheduled'
				         ORDER BY a.appointment_date
				""";

		try(Connection connection  = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, user.getId());

			ResultSet result = statement.executeQuery();

			while(result.next()) {
				Appointment appointment = new Appointment(
						result.getInt("appointment_id"),	
						result.getString("pet_name"),
						result.getString("owner_name"),
						result.getString("vet_name"),
						result.getTimestamp("appointment_date"),
						result.getString("status"),
						result.getString("reason"));

				appointments.add(appointment);
			}
			return appointments;
		}
	}
	
	public List<Appointment> getVetUpcoming(User user) throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                a.appointment_date,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.reason,
	                a.status
	            FROM Appointments a
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users u
	            ON p.owner_id = u.user_id
	            JOIN Users v
	            ON a.vet_id = v.user_id
	            AND v.role = 'VET'
	            WHERE a.vet_id = ?
	            AND a.appointment_date > NOW()
	            AND a.status = 'Scheduled'
	            ORDER BY a.appointment_date
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}

	public List<Appointment> getVetPast(User user) throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                a.appointment_date,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.reason,
	                a.status
	            FROM Appointments a
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users u
	            ON p.owner_id = u.user_id
	            JOIN Users v
	            ON a.vet_id = v.user_id
	            AND v.role = 'VET'
	            WHERE a.vet_id = ?
	            AND a.appointment_date < NOW()
	            AND a.status = 'Completed'
	            ORDER BY a.appointment_date DESC
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}

	public List<Appointment> getVetCancelled(User user) throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                a.appointment_date,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.reason,
	                a.status
	            FROM Appointments a
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users u
	            ON p.owner_id = u.user_id
	            JOIN Users v
	            ON a.vet_id = v.user_id
	            AND v.role = 'VET'
	            WHERE a.vet_id = ?
	            AND a.status = 'Cancelled'
	            ORDER BY a.appointment_date DESC
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}
	
	public List<Appointment> getVetCompletedForTreatment(User user) throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                a.appointment_date,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.reason,
	                a.status
	            FROM Appointments a
	            JOIN Pets p
	            ON a.pet_id = p.pet_id
	            JOIN Users u
	            ON p.owner_id = u.user_id
	            JOIN Users v
	            ON a.vet_id = v.user_id
	            AND v.role = 'VET'
	            WHERE a.vet_id = ?
	            AND a.status = 'Completed'
	            ORDER BY a.appointment_date DESC
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, user.getId());

	        ResultSet result = statement.executeQuery();

	        while(result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}
	
	public void completeAppointment(Appointment appointment) throws SQLException, DatabaseConfigException {

	    String sql = """
	            UPDATE Appointments
	            SET status = 'Completed'
	            WHERE appointment_id = ?
	            AND status = 'Scheduled'
	            """;

	    try(Connection connection = DBConnection.getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, appointment.getAppointmentId());

	        int rowsUpdated = statement.executeUpdate();

	        if(rowsUpdated == 0) {
	            throw new SQLException("Appointment could not be completed.");
	        }
	    }
	}

	//Admin UI
	public int getTotUpcomingCount() throws SQLException, DatabaseConfigException {
		String sql = "SELECT COUNT(*) AS appointment_count FROM Appointments WHERE appointment_date >= NOW()";

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			ResultSet result = statement.executeQuery();

			result.next();
			return result.getInt("appointment_count");
		}
	}
	
	public List<Appointment> getAllAppointments() throws SQLException, DatabaseConfigException {

	    List<Appointment> appointments = new ArrayList<>();

	    String sql = """
	            SELECT
	                a.appointment_id,
	                a.pet_id,
	                a.vet_id,
	                p.name AS pet_name,
	                CONCAT(u.first_name, ' ', u.last_name) AS owner_name,
	                CONCAT(v.first_name, ' ', v.last_name) AS vet_name,
	                a.appointment_date,
	                a.status,
	                a.reason
	            FROM Appointments a
	            JOIN Pets p
	                ON a.pet_id = p.pet_id
	            JOIN Users u
	                ON p.owner_id = u.user_id
	            JOIN Users v
	                ON a.vet_id = v.user_id
	                AND v.role = 'VET'
	            ORDER BY a.appointment_date DESC
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        ResultSet result = statement.executeQuery();

	        while (result.next()) {

	            Appointment appointment = new Appointment(
	                    result.getInt("appointment_id"),
	                    result.getInt("pet_id"),
	                    result.getInt("vet_id"),
	                    result.getString("pet_name"),
	                    result.getString("owner_name"),
	                    result.getString("vet_name"),
	                    result.getTimestamp("appointment_date"),
	                    result.getString("status"),
	                    result.getString("reason"));

	            appointments.add(appointment);
	        }

	        return appointments;
	    }
	}
	
	public boolean updateAppointment(Appointment appointment) throws SQLException, DatabaseConfigException {

	    String sql = """
	            UPDATE Appointments
	            SET pet_id = ?,
	                vet_id = ?,
	                appointment_date = ?,
	                reason = ?,
	                status = ?
	            WHERE appointment_id = ?
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, appointment.getPetId());
	        statement.setInt(2, appointment.getVetId());
	        statement.setTimestamp(3, appointment.getAppointmentDate());
	        statement.setString(4, appointment.getReason());
	        statement.setString(5, appointment.getStatus());
	        statement.setInt(6, appointment.getAppointmentId());

	        return statement.executeUpdate() > 0;
	    }
	}
	
	public boolean deleteAppointment(int appointmentId) throws SQLException, DatabaseConfigException {

	    String sql = """
	            DELETE FROM Appointments
	            WHERE appointment_id = ?
	            """;

	    try (Connection connection = DBConnection.getConnection();
	            PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, appointmentId);

	        return statement.executeUpdate() > 0;
	    }
	}
	
	
}