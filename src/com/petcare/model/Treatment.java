package com.petcare.model;

import java.sql.Timestamp;

public class Treatment {

    private int treatmentId;
    private int appointmentId;
    private int vetId;
    private String petName;
    private String vetName;
    private String diagnosis;
    private String treatmentDescription;
    private String medication;
    private String status;
    private Timestamp treatmentDate;
    
    private String ownerName;

    public Treatment(int treatmentId, int appointmentId, int vetId, String petName, String vetName,
            String diagnosis, String treatmentDescription, String medication, String status, Timestamp treatmentDate) {
        this.treatmentId = treatmentId;
        this.appointmentId = appointmentId;
        this.vetId = vetId;
        this.petName = petName;
        this.vetName = vetName;
        this.diagnosis = diagnosis;
        this.treatmentDescription = treatmentDescription;
        this.medication = medication;
        this.status = status;
        this.treatmentDate = treatmentDate;
    }
    
	//Constructor used to pass Pet object for Medical panel (Vet UI)
    public Treatment(int treatmentId, int appointmentId, int vetId, String petName, String ownerName, String vetName,
    		String diagnosis, String treatmentDescription, String medication, String status, Timestamp treatmentDate) {

        this.treatmentId = treatmentId;
        this.appointmentId = appointmentId;
        this.vetId = vetId;
        this.petName = petName;
        this.ownerName = ownerName;
        this.vetName = vetName;
        this.diagnosis = diagnosis;
        this.treatmentDescription = treatmentDescription;
        this.medication = medication;
        this.status = status;
        this.treatmentDate = treatmentDate;
    }

    public int getTreatmentId() {return treatmentId;}
    public int getAppointmentId() {return appointmentId;}
    public int getVetId() {return vetId;}
    public String getPetName() {return petName;}
    public String getVetName() {return vetName;}
    public String getDiagnosis() {return diagnosis;}
    public String getTreatmentDescription() {return treatmentDescription;}
    public String getMedication() {return medication;}
    public String getStatus() {return status;}
    public Timestamp getTreatmentDate() {return treatmentDate;}
    public String getOwnerName() {return ownerName;}
}
