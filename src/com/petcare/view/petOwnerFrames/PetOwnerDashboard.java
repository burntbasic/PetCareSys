package com.petcare.view.petOwnerFrames;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.BaseDashboard;

public class PetOwnerDashboard extends BaseDashboard {

	private static final long serialVersionUID = 1L;

	private PetOwnerController controller;

	public PetOwnerDashboard(User user) {

		super();

		controller = new PetOwnerController();

		setTitle("PetCare - Pet Owner Dashboard");

		// =========================
		// CARDS
		// =========================

		String petCount = "N/A";
		String upcoming = "N/A";
		String completed = "N/A";

		try {
			petCount = String.valueOf(controller.getOwnerPetCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			upcoming = String.valueOf(controller.getOwnerUpcomingCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			completed = String.valueOf(controller.getOwnerCompletedCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		JPanel cards = createCardGrid(1, 3,
				createCard("My Pets", petCount),
				createCard("Upcoming Appointments", upcoming),
				createCard("Completed Appointments", completed));

		// =========================
		// UPCOMING APPOINTMENTS TABLE
		// =========================

		DefaultTableModel tableModel = createReadOnlyModel("Pet", "Veterinarian", "Date & Time", "Status");
		JTable table = new JTable(tableModel);

		try {
			List<Appointment> appointments = controller.getOwnerUpcoming(user);

			for (Appointment appointment : appointments) {
				tableModel.addRow(new Object[] {
						appointment.getPetName(),
						appointment.getVetName(),
						appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
						appointment.getStatus()
				});
			}
		} catch (SQLException e) {
			ErrorHandler.handleTableLoadError(e, table, tableModel);
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleTableLoadError(e, table, tableModel);
		}

		// =========================
		// PAGES
		// =========================

		MedicalPanel medicalPanel = new MedicalPanel(user);

		addPage(createDashboardPage("Welcome back, " + user.getFName() + "!", "Your PetCare overview",
				cards, "Upcoming Appointments", table), "dashboard");
		addPage(new MyPetsPanel(user), "myPets");
		addPage(new AppointmentsPanel(user), "appointments");
		addPage(medicalPanel, "medical");
		addPage(new PetOwnerReportsPanel(user), "reports");

		// =========================
		// SIDEBAR (logout is handled by BaseDashboard)
		// =========================

		addMenuItem("Dashboard", "dashboard");
		addMenuItem("My Pets", "myPets");
		addMenuItem("Appointments", "appointments");
		addMenuItem("Medical Records", "medical", () -> medicalPanel.refreshPets());
		addMenuItem("Reports", "reports");

		showCard("dashboard");
	}
}
