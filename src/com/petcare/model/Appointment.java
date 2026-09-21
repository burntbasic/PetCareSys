package com.petcare.model;

import java.sql.Timestamp;

public class Appointment {
	private int appointmentId;
	private int petId;
	private int vetId;
	private String petName;
	private String ownerName;
	private String vetName;
	private Timestamp appointmentDate;
	private String status;
	private String reason;
	
	//Complete Constructor
	public Appointment(int appointmentId, int petId, int vetId, String petName, String ownerName,
			String vetName, Timestamp appointmentDate, String status, String reason) {
		this.appointmentId = appointmentId;
		this.petId = petId;
		this.vetId = vetId;
		this.petName = petName;
		this.ownerName = ownerName;
		this.vetName = vetName;
		this.appointmentDate = appointmentDate;
		this.status = status;
		this.reason = reason;
	}
	
	//Display Constructor - no pet/vet ids
	public Appointment(int appointmentId, String petName, String ownerName, String vetName, 
			Timestamp appointmentDate, String status, String reason) {
		this.appointmentId = appointmentId;
		this.petName = petName;
		this.ownerName = ownerName;
		this.vetName = vetName;
		this.appointmentDate = appointmentDate;
		this.status = status;
		this.reason = reason;
	}
	
	public int getAppointmentId() {return appointmentId;}
	public int getpetId() {return petId;}
	public int getVetId() {return vetId;}
	public String getPetName() {return petName;}
	public String getOwnerName() {return ownerName;}
	public String getVetName() {return vetName;}
	public Timestamp getAppointmentDate() {return appointmentDate;}
	public String getStatus() {return status;}
	public String getReason() {return reason;}
}
