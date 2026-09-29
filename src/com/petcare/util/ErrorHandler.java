package com.petcare.util;

import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

	public static void handleTableLoadError(Exception e, JTable table, DefaultTableModel tableModel) {
	    if (e instanceof SQLException) {
	        handleSQLException((SQLException) e);

	    } else if (e instanceof DatabaseConfigException) {
	        handleDatabaseConfigException((DatabaseConfigException) e);
	    }

	    tableModel.setRowCount(0);

	    Object[] errorRow = new Object[table.getColumnCount()];

	    errorRow[0] = "ERROR";

	    if (table.getColumnCount() > 1) {
	        errorRow[1] = "Could not load data";
	    }

	    tableModel.addRow(errorRow);
	}
}
