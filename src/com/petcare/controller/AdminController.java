package com.petcare.controller;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.petcare.dao.ActivityLogDAO;
import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.UserDAO;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Activity;
import com.petcare.model.Appointment;
import com.petcare.model.Pet;
import com.petcare.model.User;

public class AdminController {

	private UserDAO userdb;
	private PetDAO petdb;
	private AppointmentDAO appointmentdb;
	private ActivityLogDAO activitydb;
	private User loggedInUser;

	public AdminController(User user) {

		loggedInUser = user;

		userdb = new UserDAO();
		petdb = new PetDAO();
		appointmentdb = new AppointmentDAO();
		activitydb = new ActivityLogDAO();
	}

	// Dashboard Cards

	public int getOwnerCount() throws SQLException, DatabaseConfigException {
		return userdb.getOwnerCount();
	}

	public int getVetCount() throws SQLException, DatabaseConfigException {
		return userdb.getVetCount();
	}

	public int getTotPetCount() throws SQLException, DatabaseConfigException {
		return petdb.getTotPetCount();
	}

	public int getTotUpcomingCount() throws SQLException, DatabaseConfigException {
		return appointmentdb.getTotUpcomingCount();
	}

	// Dashboard Table

	public List<Activity> getRecentActivity() throws SQLException, DatabaseConfigException {
		return activitydb.getRecentActivity();
	}

	// User Management

	public List<User> getAllUsers() throws SQLException, DatabaseConfigException {
		return userdb.getAllUsers();
	}

	public boolean updateUser(int userId, String fName, String lName, String email, String phone, String username, String role) throws SQLException, DatabaseConfigException {

		phone = "+94" + phone;
		boolean updated = userdb.updateUser(userId, fName, lName, email, phone, username, role);

		if (updated) {
			activitydb.addActivity(loggedInUser.getId(), "Updated user");
		}

		return updated;
	}

	public boolean deleteUser(int userId) throws SQLException, DatabaseConfigException {

		boolean deleted = userdb.deleteUser(userId);

		if (deleted) {
			activitydb.addActivity(loggedInUser.getId(), "Deleted user");
		}

		return deleted;
	}

	public boolean addUser(String fName, String lName, String email, String phone, String username, String password, String role) throws SQLException, DatabaseConfigException {

		phone = "+94" + phone;
		boolean added = userdb.addUser(fName, lName, email, phone, username, password, role);

		if (added) {
			activitydb.addActivity(loggedInUser.getId(), "Added user");
		}

		return added;
	}

	public boolean usernameExists(String username) throws SQLException, DatabaseConfigException {
		return userdb.usernameExists(username);
	}

	public boolean emailExists(String email) throws SQLException, DatabaseConfigException {
		return userdb.emailExists(email);
	}

	// Pets

	public List<Pet> getOwnerPets(User owner) throws SQLException, DatabaseConfigException {
		return petdb.getOwnerPets(owner);
	}

	public List<Pet> getAllPets() throws SQLException, DatabaseConfigException {
		return petdb.getAllPets();
	}

	public List<User> getPetOwners() throws SQLException, DatabaseConfigException {
		return userdb.getPetOwners();
	}

	public void addPet(User user, String name, String species, String gender) throws SQLException, DatabaseConfigException {

		petdb.addPet(user, name, species, gender);
		activitydb.addActivity(loggedInUser.getId(), "Added pet");
	}

	// Appointments

	public List<Appointment> getAllAppointments() throws SQLException, DatabaseConfigException {
		return appointmentdb.getAllAppointments();
	}

	public boolean updateAppointment(Appointment appointment) throws SQLException, DatabaseConfigException {

		boolean updated = appointmentdb.updateAppointment(appointment);

		if (updated) {
			activitydb.addActivity(loggedInUser.getId(), "Updated appointment");
		}

		return updated;
	}

	public void cancelAppointment(Appointment appointment) throws SQLException, DatabaseConfigException {

		appointmentdb.cancelAppointment(appointment);
		activitydb.addActivity(loggedInUser.getId(), "Cancelled appointment");
	}

	public boolean deleteAppointment(int appointmentId) throws SQLException, DatabaseConfigException {

		boolean deleted = appointmentdb.deleteAppointment(appointmentId);

		if (deleted) {
			activitydb.addActivity(loggedInUser.getId(), "Deleted appointment");
		}

		return deleted;
	}

	public List<User> getVeterinarians() throws SQLException, DatabaseConfigException {
		return userdb.getVeterinarians();
	}

	public List<LocalTime> getAvailableTimes(int vetId, LocalDate date) throws SQLException, DatabaseConfigException {

		List<Timestamp> bookedTimes = appointmentdb.getBookedTimes(vetId, date);
		List<LocalTime> availableTimes = new ArrayList<>();

		LocalTime startTime = LocalTime.of(8, 30);
		LocalTime endTime = LocalTime.of(17, 30);

		for (LocalTime time = startTime; !time.isAfter(endTime); time = time.plusMinutes(30)) {

			boolean booked = false;

			for (Timestamp bookedTimestamp : bookedTimes) {

				LocalTime bookedTime = bookedTimestamp.toLocalDateTime().toLocalTime();

				if (bookedTime.equals(time)) {
					booked = true;
					break;
				}
			}

			if (!booked) {
				availableTimes.add(time);
			}
		}

		return availableTimes;
	}

	public void addAppointment(Pet pet, User vet, LocalDate date, LocalTime time, String reason) throws SQLException, DatabaseConfigException {

		Timestamp appointmentDate = Timestamp.valueOf(date.atTime(time));

		appointmentdb.bookAppointment(pet.getPetId(), vet.getId(), appointmentDate, reason);
		activitydb.addActivity(loggedInUser.getId(), "Added appointment");
	}
}
