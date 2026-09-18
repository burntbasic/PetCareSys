package com.petcare.util;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.petcare.exception.DatabaseConfigException;

public class ErrorHandler {
	public static void handleSQLException(SQLException e) {
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "A database error occurred.\nPlease try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void handleDatabaseConfigException(DatabaseConfigException e) {
		e.printStackTrace();
        JOptionPane.showMessageDialog(null, "There is a problem with the database configuration.\n" + "Please check config/database.properties.", "Configuration Error", JOptionPane.ERROR_MESSAGE);
	}

}
