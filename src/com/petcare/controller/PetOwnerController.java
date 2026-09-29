package com.petcare.controller;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.petcare.dao.ActivityLogDAO;
import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.TreatmentDAO;
import com.petcare.dao.UserDAO;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.Pet;
import com.petcare.model.Treatment;
import com.petcare.model.User;

public class PetOwnerController {
	
	private PetDAO petdb;
	private AppointmentDAO appointmentdb;
	private UserDAO userdb;
	private TreatmentDAO treatmentdb;
	private ActivityLogDAO activitydb;
	
	public PetOwnerController() {
		petdb = new PetDAO();
		appointmentdb = new AppointmentDAO();
		userdb = new UserDAO();
		treatmentdb = new TreatmentDAO();
		activitydb = new ActivityLogDAO();
	}
	
	//Dashboard Cards
	public int getOwnerPetCount(User user) throws SQLException, DatabaseConfigException {
		return petdb.getOwnerPetCount(user);
	}
	
	public int getOwnerUpcomingCount(User user) throws SQLException, DatabaseConfigException {
		return appointmentdb.getOwnerUpcomingCount(user);
	}
	
	public int getOwnerCompletedCount(User user) throws SQLException, DatabaseConfigException {
		return appointmentdb.getOwnerCompletedCount(user);
	}
	
	//Dashboard Table
	public List<Appointment> getOwnerUpcoming(User user) throws SQLException, DatabaseConfigException {
		return appointmentdb.getOwnerUpcoming(user);
	}
	
	//My Pets Panel
	public List<Pet> getOwnerPets(User user) throws SQLException, DatabaseConfigException {
		return petdb.getOwnerPets(user);
	}
	
	public void updatePet(User user, Pet pet, String name, String species, String otherSpecies, String gender) throws SQLException, DatabaseConfigException {

	    if (name == null || name.trim().isEmpty()) {
	        throw new IllegalArgumentException("Pet name cannot be empty.");
	    }

	    if (name.length() > 100) {
	        throw new IllegalArgumentException("Pet name cannot exceed 100 characters.");
	    }
	    
	    if (species.equals("Other")) {

	        if (otherSpecies == null || otherSpecies.trim().isEmpty()) {
	            throw new IllegalArgumentException("Please enter the animal type.");
	        }

	        if (otherSpecies.length() > 50) {
	            throw new IllegalArgumentException("Animal type cannot exceed 50 characters.");
	        }

	        species = otherSpecies.trim();
	    }

	    petdb.updatePet(pet, name, species, gender);
	    activitydb.addActivity(user.getId(), "Updated pet");
	}
	
	public void deletePet(User user, Pet pet) throws SQLException, DatabaseConfigException {
	    petdb.deletePet(pet);
	    activitydb.addActivity(user.getId(), "Deleted pet");
	}
	
	public void addPet(User user, String name, String species, String otherSpecies, String gender)
	        throws SQLException, DatabaseConfigException {

	    if (name == null || name.trim().isEmpty()) {
	        throw new IllegalArgumentException("Pet name cannot be empty.");
	    }

	    if (name.length() > 100) {
	        throw new IllegalArgumentException("Pet name cannot exceed 100 characters.");
	    }

	    if (species.equals("Other")) {

	        if (otherSpecies == null || otherSpecies.trim().isEmpty()) {
	            throw new IllegalArgumentException("Please enter the animal type.");
	        }

	        if (otherSpecies.length() > 50) {
	            throw new IllegalArgumentException("Animal type cannot exceed 50 characters.");
	        }

	        species = otherSpecies.trim();
	    }
	    
	    petdb.addPet(user, name.trim(), species, gender);
	    activitydb.addActivity(user.getId(), "Added pet");
	}
	
	//Appointments Panel
	public void cancelAppointment(User user, Appointment appointment) throws SQLException, DatabaseConfigException {

	    appointmentdb.cancelAppointment(appointment);
	    activitydb.addActivity(user.getId(), "Cancelled appointment");
	}
	
	public List<Appointment> getOwnerPast(User user) throws SQLException, DatabaseConfigException {

	    return appointmentdb.getOwnerPast(user);
	}
	
	public List<Appointment> getOwnerCancelled(User user) throws SQLException, DatabaseConfigException {

	    return appointmentdb.getOwnerCancelled(user);
	}
	
	public List<User> getVeterinarians() throws SQLException, DatabaseConfigException {

	    return userdb.getVeterinarians();
	}
	
	public void bookAppointment(User owner, Pet pet, User vet, LocalDate date, LocalTime time, String reason) throws SQLException, DatabaseConfigException {

	    if (owner == null) {
	        throw new IllegalArgumentException("Owner is required.");
	    }

	    if (pet == null) {
	        throw new IllegalArgumentException("Please select a pet.");
	    }

	    if (vet == null) {
	        throw new IllegalArgumentException("Please select a veterinarian.");
	    }

	    if (date == null) {
	        throw new IllegalArgumentException("Please select a date.");
	    }

	    if (time == null) {
	        throw new IllegalArgumentException("Please select a time.");
	    }

	    if (reason == null || reason.trim().isEmpty()) {
	        throw new IllegalArgumentException("Please enter a reason for the appointment.");
	    }

	    if (date.isBefore(LocalDate.now())) {
	        throw new IllegalArgumentException("Appointment date cannot be in the past.");
	    }

	    LocalTime startTime = LocalTime.of(8, 30);
	    LocalTime endTime = LocalTime.of(17, 30);

	    if (time.isBefore(startTime) || time.isAfter(endTime)) {
	        throw new IllegalArgumentException("Selected time is outside working hours.");
	    }

	    Timestamp appointmentDate = Timestamp.valueOf(LocalDateTime.of(date, time));

	    if (!appointmentdb.isVetAvailable(vet.getId(), appointmentDate)) {
	        throw new IllegalArgumentException(
	                "This time is no longer available. Please select another time.");
	    }

	    appointmentdb.bookAppointment(
	            pet.getPetId(),
	            vet.getId(),
	            appointmentDate,
	            reason.trim());

	    activitydb.addActivity(owner.getId(), "Added appointment");
	}
	
	public boolean isVetAvailable(int vetId, Timestamp appointmentDate) throws SQLException, DatabaseConfigException {

	    return appointmentdb.isVetAvailable(vetId, appointmentDate);
	}
	
	public List<LocalTime> getAvailableTimes(int vetId, LocalDate date) throws SQLException, DatabaseConfigException {

	    List<Timestamp> bookedTimes = appointmentdb.getBookedTimes(vetId, date);
	    List<LocalTime> availableTimes = new ArrayList<>();

	    LocalTime startTime = LocalTime.of(8, 30);
	    LocalTime endTime = LocalTime.of(17, 30);

	    for (LocalTime time = startTime;
	            !time.isAfter(endTime);
	            time = time.plusMinutes(30)) {

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
	
	//Medical Panel
	public List<Treatment> getPetMedicalRecords(User user, int petId)
	        throws SQLException, DatabaseConfigException {

	    return treatmentdb.getPetMedicalRecords(user, petId);
	}
}