package com.petcare.controller;

import java.sql.SQLException;

import com.petcare.dao.UserDAO;
import com.petcare.exception.DatabaseConfigException;

public class RegisterController {
	
	private UserDAO userdb;
	
	public RegisterController() {
		userdb = new UserDAO();
	}
	
	public String validateField(RegisterFieldEnum field, String value) {

	    switch (field) {
	        case FIRST_NAME:
	            if (value.isEmpty()) {
	                return "First name is required.";
	            }
	            if (!value.matches("[a-zA-Z]+")) {
	                return "First name must contain letters only.";
	            }
	            break;

	        case LAST_NAME:
	            if (value.isEmpty()) {
	                return "Last name is required.";
	            }
	            if (!value.matches("[a-zA-Z]+")) {
	                return "Last name must contain letters only.";
	            }
	            break;

	        case EMAIL:
	            if (value.isEmpty()) {
	                return "Email is required.";
	            }
	            if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
	                return "Enter a valid email address.";
	            }
	            break;

	        case PHONE:
	            if (value.isEmpty()) {
	                return "Phone number is required.";
	            }
	            if (value.length() > 9) {
	                return "Maximum 9 digits allowed.";
	            }
	            if (!value.matches("7[0-9]{8}")) {
	                return "Enter a valid number (7XXXXXXXX) upto 9 digits.";
	            }
	            break;

	        case USERNAME:
	            if (value.isEmpty()) {
	                return "Username is required.";
	            }
	            break;

	        case PASSWORD:
	            if (value.isEmpty()) {
	                return "Password is required.";
	            }
	            break;
	    }
	    return null;
	}
	
	public String validateConfirmPassword(String password, String confirmPassword) {

	    if (confirmPassword.isEmpty()) {
	        return "Please confirm your password.";
	    }
	    if (!password.equals(confirmPassword)) {
	        return "Passwords do not match.";
	    }
	    return null;
	}
	
	public boolean usernameExists(String username) throws SQLException, DatabaseConfigException {
		return userdb.usernameExists(username);
	}
	
	public boolean emailExists(String email) throws SQLException, DatabaseConfigException {
		return userdb.emailExists(email);
	}
	
	public boolean petOwnerRegister(String fName, String lName, String email, String phone, String username, String password) throws SQLException, DatabaseConfigException {
		phone = "+94" + phone;
		
		return userdb.petOwnerRegister(fName, lName, email, phone, username, password);
	}
}
