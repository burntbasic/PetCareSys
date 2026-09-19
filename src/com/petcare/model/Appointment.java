package com.petcare.model;

import java.sql.Timestamp;

public class Appointment {
	private int id;
	private String petName;
	private String ownerName;
	private String vetName;
	private Timestamp appointmentDate;
	private String status;
	private String reason;
	
	public Appointment(int id, String petName, String ownerName, String vetName, Timestamp appointmentDate, String status, String reason) {
		this.id = id;
		this.petName = petName;
		this.ownerName = ownerName;
		this.vetName = vetName;
		this.appointmentDate = appointmentDate;
		this.status = status;
		this.reason = reason;
	}
	
	public int getId() {return id;}
	public String getPetName() {return petName;}
	public String getOwnerName() {return ownerName;}
	public String getVetName() {return vetName;}
	public Timestamp getAppointmentDate() {return appointmentDate;}
	public String getStatus() {return status;}
	public String getReason() {return reason;}
}
