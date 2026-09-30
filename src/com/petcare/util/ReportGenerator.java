package com.petcare.util;

import java.sql.Connection;
import java.util.HashMap;

import com.petcare.dao.DBConnection;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class ReportGenerator {

	public static void showReport(String reportPath) {

		try {
			JasperReport report = JasperCompileManager.compileReport(reportPath);
			Connection connection = DBConnection.getConnection();
			JasperPrint print = JasperFillManager.fillReport(report, new HashMap<String, Object>(), connection);
			JasperViewer.viewReport(print, false);
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showReport(String reportPath, String parameterName, int userId) {

		try {
			JasperReport report = JasperCompileManager.compileReport(reportPath);
			Connection connection = DBConnection.getConnection();

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(parameterName, userId);

			JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
			JasperViewer.viewReport(print, false);
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}