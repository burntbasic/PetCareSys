package com.petcare.model;

public class User {
	//Implemented to handle user roles along with userLogin.
	private int id;
	private String fName;
	private String lName;
	private String email;
	private String phone;
	private String username;
	private String role;
	
	public User(int id, String fName, String lName, String email, String phone, String username, String role) {
		this.id = id;
		this.fName = fName;
		this.lName = lName;
		this.email = email;
		this.phone = phone;
		this.username = username;
		this.role = role;
	}
	
	public int getId() {return id;}
	public String getFName() {return fName;}
	public String getLName() {return lName;}
	public String getEmail() {return email;}
	public String getPhone() {return phone;}
	public String getUsername() {return username;}
	public String getRole() {return role;}
}
