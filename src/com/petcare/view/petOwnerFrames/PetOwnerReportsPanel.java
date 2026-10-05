package com.petcare.view.petOwnerFrames;

import com.petcare.model.User;
import com.petcare.util.ReportGenerator;
import com.petcare.view.ReportsPanel;

public class PetOwnerReportsPanel extends ReportsPanel {

	private static final long serialVersionUID = 1L;

	private User user;

	public PetOwnerReportsPanel(User user) {

		super("Generate pet care reports");

		this.user = user;

		addOwnerReport("Appointment Report",
				"View your upcoming and completed appointments.",
				"Generate Appointment Report",
				"reports/pet_owner/Appointment_Report.jrxml");

		addOwnerReport("Pet Report",
				"View your pets and their registered information.",
				"Generate Pet Report",
				"reports/pet_owner/Pet_Report.jrxml");

		addOwnerReport("Treatment Report",
				"View treatments and medical history of your pets.",
				"Generate Treatment Report",
				"reports/pet_owner/Treatment_Report.jrxml");

		addOwnerReport("Medical Record Report",
				"View the medical records and treatment history of your pets.",
				"Generate Medical Report",
				"reports/pet_owner/Medical_Report.jrxml");
	}

	private void addOwnerReport(String title, String description, String buttonText, String reportPath) {
		addReport(title, description, buttonText,
				() -> ReportGenerator.showReport(reportPath, "OWNER_ID", user.getId()));
	}
}
