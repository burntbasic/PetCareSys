package com.petcare.view.vetFrames;

import com.petcare.model.User;
import com.petcare.util.ReportGenerator;
import com.petcare.view.ReportsPanel;

public class VetReportsPanel extends ReportsPanel {

	private static final long serialVersionUID = 1L;

	private User user;

	public VetReportsPanel(User user) {

		super("Generate veterinary reports");

		this.user = user;

		addVetReport("Appointment Report",
				"View your appointments, pets, owners and appointment details.",
				"Generate Appointment Report",
				"reports/vet/Appointment_Report.jrxml");

		addVetReport("Patient Report",
				"View patients and their owner information.",
				"Generate Patient Report",
				"reports/vet/Patient_Report.jrxml");

		addVetReport("Treatment Report",
				"View treatments provided to your patients.",
				"Generate Treatment Report",
				"reports/vet/Treatment_Report.jrxml");

		addVetReport("Medical Record Report",
				"View medical records and treatment history of patients.",
				"Generate Medical Report",
				"reports/vet/Medical_Report.jrxml");
	}

	private void addVetReport(String title, String description, String buttonText, String reportPath) {
		addReport(title, description, buttonText,
				() -> ReportGenerator.showReport(reportPath, "VET_ID", user.getId()));
	}
}
