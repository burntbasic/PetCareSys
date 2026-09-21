package com.petcare.controller;

import java.util.List;
import java.sql.SQLException;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;

public class PetOwnerController {
	
	private PetDAO petdb;
	private AppointmentDAO appointmentdb;
	
	public PetOwnerController() {
		petdb = new PetDAO();
		appointmentdb = new AppointmentDAO();
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
}
