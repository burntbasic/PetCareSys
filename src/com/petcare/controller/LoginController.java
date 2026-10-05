package com.petcare.controller;

import java.sql.SQLException;

import com.petcare.model.User;
import com.petcare.dao.UserDAO;
import com.petcare.exception.DatabaseConfigException;

public class LoginController {
	
	private UserDAO userdb;
	
	public LoginController() {
		userdb = new UserDAO();
	}
	
	public User loginValidation(String username, String password) throws SQLException, DatabaseConfigException {
		return userdb.userLogin(username, password);
	}
}
