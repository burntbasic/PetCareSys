package com.petcare.model;

import java.sql.Timestamp;

public class Appointment {
	private int id;
	private String petName;
	private String vetName;
	private Timestamp appointmentDate;
	private String status;
	private String reason;
	
	public Appointment(int id, String petName, String vetName, Timestamp appointmentDate, String status, String reason) {
		this.id = id;
		this.petName = petName;
		this.vetName = vetName;
		this.appointmentDate = appointmentDate;
		this.status = status;
		this.reason = reason;
	}
	
	public int getId() {return id;}
	public String getPetName() {return petName;}
	public String getVetName() {return vetName;}
	public Timestamp getAppointmentDate() {return appointmentDate;}
	public String getStatus() {return status;}
	public String getReason() {return reason;}
	
	/*public Appointment() {
	 * }
	 */
	
	//Setters
	//public void setId(int id) {this.id = id;}
	//public void setPetName(String petName) {this.petName = petName;}
	//public void setVetName(String vetName) {this.vetName = vetName;}
	//public void setAppointmentDate(Timestamp appointmentDate) {this.appointmentDate = appointmentDate;}
	//public void setStatus(String status) {this.status = status;}
	//public void setReason(String reason) {this.reason = reason;}

}
