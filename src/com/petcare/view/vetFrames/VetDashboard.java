package com.petcare.view.vetFrames;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.BaseDashboard;

public class VetDashboard extends BaseDashboard {

	private static final long serialVersionUID = 1L;

	private VetController controller;

	public VetDashboard(User user) {

		super();

		controller = new VetController();

		setTitle("PetCare - Vet Dashboard");

		// =========================
		// CARDS
		// =========================

		String today = "N/A";
		String pending = "N/A";
		String patient = "N/A";

		try {
			today = String.valueOf(controller.getVetTodaysCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			pending = String.valueOf(controller.getPendingCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			patient = String.valueOf(controller.getVetPatientCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		JPanel cards = createCardGrid(1, 3,
				createCard("Today's Appointments", today),
				createCard("Pending Treatments", pending),
				createCard("Patients Today", patient));

		// =========================
		// TODAY'S APPOINTMENTS TABLE
		// =========================

		DefaultTableModel tableModel = createReadOnlyModel("Time", "Pet", "Owner", "Reason", "Status");

		try {
			List<Appointment> appointments = controller.getVetTodays(user);

			for (Appointment appointment : appointments) {
				tableModel.addRow(new Object[] {
						appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")),
						appointment.getPetName(),
						appointment.getOwnerName(),
						appointment.getReason(),
						appointment.getStatus()
				});
			}
		} catch (SQLException e) {
			ErrorHandler.handleSQLException(e);

			tableModel.addRow(new Object[] { "ERROR", "Could not load appointments", "", "", "" });
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);

			tableModel.addRow(new Object[] { "ERROR", "Could not load appointments", "", "", "" });
		}

		JTable table = new JTable(tableModel);

		// =========================
		// PAGES
		// =========================

		VetMedicalPanel medicalPanel = new VetMedicalPanel(user);

		addPage(createDashboardPage("Welcome, Dr. " + user.getLName(), "Today's veterinary overview",
				cards, "Today's Appointments", table), "dashboard");
		addPage(new VetAppointmentsPanel(user), "appointments");
		addPage(new PatientsPanel(user), "patients");
		addPage(new TreatmentsPanel(user), "treatments");
		addPage(medicalPanel, "medical");
		addPage(new VetReportsPanel(user), "reports");

		// =========================
		// SIDEBAR (logout is handled by BaseDashboard)
		// =========================

		addMenuItem("Dashboard", "dashboard");
		addMenuItem("Appointments", "appointments");
		addMenuItem("Patients", "patients");
		addMenuItem("Treatments", "treatments");
		addMenuItem("Medical Records", "medical", () -> medicalPanel.refreshRecords());
		addMenuItem("Reports", "reports");

		showCard("dashboard");
	}
}
