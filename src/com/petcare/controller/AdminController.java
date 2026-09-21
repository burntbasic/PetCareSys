package com.petcare.controller;

import java.sql.SQLException;
import java.util.List;

import com.petcare.dao.ActivityLogDAO;
import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.UserDAO;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Activity;

public class AdminController {

    private UserDAO userdb;
    private PetDAO petdb;
    private AppointmentDAO appointmentdb;
    private ActivityLogDAO activitydb;

    public AdminController() {
        userdb = new UserDAO();
        petdb = new PetDAO();
        appointmentdb = new AppointmentDAO();
        activitydb = new ActivityLogDAO();
    }

    //Dashboard Cards
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

    //Dashboard Table
    public List<Activity> getRecentActivity() throws SQLException, DatabaseConfigException {
        return activitydb.getRecentActivity();
    }
}
