package com.petcare.view.adminFrames;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Activity;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.BaseDashboard;

public class AdminDashboard extends BaseDashboard {

	private static final long serialVersionUID = 1L;

	private AdminController controller;
	private DefaultTableModel activityTableModel;
	private JTable activityTable;

	public AdminDashboard(User user) {

		super();

		controller = new AdminController(user);

		setTitle("PetCare - Admin Dashboard");

		// =========================
		// CARDS
		// =========================

		String petOwnerCount = "N/A";
		String vetCount = "N/A";
		String petTotCount = "N/A";
		String upcomingAppointmentsCount = "N/A";

		try {
			petOwnerCount = String.valueOf(controller.getOwnerCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			vetCount = String.valueOf(controller.getVetCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			petTotCount = String.valueOf(controller.getTotPetCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			upcomingAppointmentsCount = String.valueOf(controller.getTotUpcomingCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		JPanel cards = createCardGrid(2, 2,
				createCard("Pet Owners", petOwnerCount),
				createCard("Veterinarians", vetCount),
				createCard("Total Pets", petTotCount),
				createCard("Appointments", upcomingAppointmentsCount));

		// =========================
		// ACTIVITY TABLE
		// =========================

		activityTableModel = createReadOnlyModel("User", "Activity", "Date");
		activityTable = new JTable(activityTableModel);

		loadRecentActivity(activityTable);

		// =========================
		// PAGES
		// =========================

		addPage(createDashboardPage("Welcome, " + user.getFName(), "System administration overview",
				cards, "Recent System Activity", activityTable), "dashboard");
		addPage(new UserManagementPanel(user), "users");
		addPage(new AdminAppointmentsPanel(user), "appointments");
		addPage(new AdminReportsPanel(user), "reports");

		// =========================
		// SIDEBAR (logout is handled by BaseDashboard)
		// =========================

		addMenuItem("Dashboard", "dashboard", () -> loadRecentActivity(activityTable));
		addMenuItem("User Management", "users");
		addMenuItem("Appointments", "appointments");
		addMenuItem("Reports", "reports");

		showCard("dashboard");
	}

	private void loadRecentActivity(JTable table) {

		activityTableModel.setRowCount(0);

		try {
			List<Activity> activities = controller.getRecentActivity();

			for (Activity activity : activities) {

				activityTableModel.addRow(new Object[] {
						activity.getUser(),
						activity.getActivity(),
						activity.getActivityDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
				});
			}

		} catch (SQLException e) {
			ErrorHandler.handleTableLoadError(e, table, activityTableModel);
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleTableLoadError(e, table, activityTableModel);
		}
	}
}
