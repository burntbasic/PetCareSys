package com.petcare.controller;

import java.sql.SQLException;
import java.util.List;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.TreatmentDAO;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;

public class VetController {

    private AppointmentDAO appointmentdb;
    private TreatmentDAO treatmentdb;
    private PetDAO petdb;

    public VetController() {
        appointmentdb = new AppointmentDAO();
        treatmentdb = new TreatmentDAO();
        petdb = new PetDAO();
    }

    //Dashboard Cards
    public int getVetTodaysCount(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetTodaysCount(user);
    }

    public int getPendingCount(User user) throws SQLException, DatabaseConfigException {
        return treatmentdb.getPendingCount(user);
    }

    public int getVetPatientCount(User user) throws SQLException, DatabaseConfigException {
        return petdb.getVetPatientCount(user);
    }

    //Dashboard Table
    public List<Appointment> getVetTodays(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetTodays(user);
    }
}
