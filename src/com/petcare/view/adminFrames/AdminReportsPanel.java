package com.petcare.view.adminFrames;

import com.petcare.model.User;
import com.petcare.util.ReportGenerator;
import com.petcare.view.ReportsPanel;

public class AdminReportsPanel extends ReportsPanel {

	private static final long serialVersionUID = 1L;

	public AdminReportsPanel(User user) {

		super("Generate system reports");

		addAdminReport("Appointment Report",
				"View appointments, pets, owners and veterinarians.",
				"Generate Appointment Report",
				"reports/admin/Appointment_Report.jrxml");

		addAdminReport("User Report",
				"View registered users and their roles.",
				"Generate User Report",
				"reports/admin/User_Report.jrxml");

		addAdminReport("Pet Report",
				"View pets and their registered owners.",
				"Generate Pet Report",
				"reports/admin/Pet_Report.jrxml");

		addAdminReport("Treatment Report",
				"View treatments, pets and veterinarians.",
				"Generate Treatment Report",
				"reports/admin/Treatment_Report.jrxml");
	}

	private void addAdminReport(String title, String description, String buttonText, String reportPath) {
		addReport(title, description, buttonText, () -> ReportGenerator.showReport(reportPath));
	}
}
