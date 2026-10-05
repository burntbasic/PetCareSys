package com.petcare.controller;

import java.sql.SQLException;
import java.util.List;

import com.petcare.dao.ActivityLogDAO;
import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.TreatmentDAO;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.Pet;
import com.petcare.model.Treatment;
import com.petcare.model.User;

public class VetController {

    private AppointmentDAO appointmentdb;
    private TreatmentDAO treatmentdb;
    private PetDAO petdb;
    private ActivityLogDAO activitydb;

    public VetController() {
        appointmentdb = new AppointmentDAO();
        treatmentdb = new TreatmentDAO();
        petdb = new PetDAO();
        activitydb = new ActivityLogDAO();
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

    //Dashboard Table & Appointments Panel
    public List<Appointment> getVetTodays(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetTodays(user);
    }
    
    //Appointments Panel
    public List<Appointment> getVetUpcoming(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetUpcoming(user);
    }

    public List<Appointment> getVetPast(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetPast(user);
    }

    public List<Appointment> getVetCancelled(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetCancelled(user);
    }
    
    public void completeAppointment(User user, Appointment appointment) throws SQLException, DatabaseConfigException {
        appointmentdb.completeAppointment(appointment);
        activitydb.addActivity(user.getId(), "Completed appointment");
    }
    
    //Patients Panel
    public List<Pet> getVetPatients(User user) throws SQLException, DatabaseConfigException {
        return petdb.getVetPatients(user);
    }
    
    //Medical Records Panels
    public List<Treatment> getVetMedicalRecords(User user) throws SQLException, DatabaseConfigException {
        return treatmentdb.getVetMedicalRecords(user);
    }
    
    //Treatment Panel
    public List<Treatment> getVetTreatments(User user) throws SQLException, DatabaseConfigException {
        return treatmentdb.getVetTreatments(user);
    }
    
    public void updateTreatment(User user, Treatment treatment, String diagnosis, String treatmentDescription, String medication, String status) throws SQLException, DatabaseConfigException {

        if(treatment == null) {
            throw new IllegalArgumentException("Treatment cannot be null.");
        }

        if(diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty.");
        }

        if(treatmentDescription == null || treatmentDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment description cannot be empty.");
        }

        if(medication == null || medication.trim().isEmpty()) {
            throw new IllegalArgumentException("Medication cannot be empty.");
        }

        if(status == null || (!status.equals("Pending") && !status.equals("Completed"))) {
            throw new IllegalArgumentException("Status must be Pending or Completed.");
        }

        treatmentdb.updateTreatment(treatment, diagnosis.trim(), treatmentDescription.trim(), medication.trim(), status);
        activitydb.addActivity(user.getId(), "Updated treatment");
    }
    
    public List<Appointment> getVetCompletedForTreatment(User user) throws SQLException, DatabaseConfigException {
        return appointmentdb.getVetCompletedForTreatment(user);
    }
        
    public void addTreatment(User user, int appointmentId, String diagnosis,
            String treatmentDescription, String medication, String status) throws SQLException, DatabaseConfigException {

        if(user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if(diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty.");
        }

        if(treatmentDescription == null || treatmentDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment description cannot be empty.");
        }

        if(medication == null || medication.trim().isEmpty()) {
            throw new IllegalArgumentException("Medication cannot be empty.");
        }

        if(status == null || (!status.equals("Pending") && !status.equals("Completed"))) {
            throw new IllegalArgumentException("Status must be Pending or Completed.");
        }

        if(treatmentdb.hasTreatment(appointmentId)) {
            throw new IllegalArgumentException("A treatment already exists for this appointment.");
        }

        treatmentdb.addTreatment(appointmentId, user.getId(), diagnosis.trim(), treatmentDescription.trim(), medication.trim(), status);
        activitydb.addActivity(user.getId(), "Added treatment");
    }
}
