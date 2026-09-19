package com.petcare.ui.petOwnerFrames;
import com.petcare.database.AppointmentDB;
import com.petcare.database.PetDB;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.ui.BaseDashboard;
import com.petcare.ui.LoginFrame;
import com.petcare.util.ErrorHandler;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PetOwnerDashboard extends BaseDashboard {

	private static final long serialVersionUID = 1L;

	private AppointmentDB appointmentdb;
	private PetDB petdb;

	public PetOwnerDashboard(User user) {
		
		super();

		appointmentdb = new AppointmentDB();
		petdb = new PetDB();

		setTitle("PetCare - Pet Owner Dashboard");

		// =========================
		// SIDEBAR
		// =========================

		JButton dashboardBtn = createMenuButton("Dashboard");
		JButton petsBtn = createMenuButton("My Pets");
		JButton appointmentsBtn = createMenuButton("Appointments");
		JButton medicalBtn = createMenuButton("Medical Records");
		JButton reportsBtn = createMenuButton("Reports");

		sidebar.add(dashboardBtn);
		sidebar.add(petsBtn);
		sidebar.add(appointmentsBtn);
		sidebar.add(medicalBtn);
		sidebar.add(reportsBtn);

		sidebar.add(Box.createVerticalGlue());

		JButton logoutBtn = createMenuButton("Logout");
		sidebar.add(logoutBtn);
		sidebar.add(Box.createVerticalStrut(20));

		// =========================
		// MAIN CONTENT
		// =========================

		// Header
		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel welcomeLabel = new JLabel(
				"Welcome back, " + user.getFName() + "!"
				);

		welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("Your PetCare overview");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
		headerText.add(welcomeLabel);
		headerText.add(Box.createVerticalStrut(5));
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		// =========================
		// DASHBOARD CONTENT
		// =========================

		// Cards
		JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));

		String petCount = "N/A";
		String upcoming = "N/A";
		String completed = "N/A";

		try {
			petCount = String.valueOf(petdb.getPetCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			upcoming = String.valueOf(appointmentdb.getUpcomingCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		try {
			completed = String.valueOf(appointmentdb.getCompletedCount(user));
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		cardsPanel.add(createCard("My Pets", petCount));
		cardsPanel.add(createCard("Upcoming Appointments", upcoming));
		cardsPanel.add(createCard("Completed Appointments", completed));

		dashboardContent.add(cardsPanel);
		dashboardContent.add(Box.createVerticalStrut(25));

		// Table title
		JLabel appointmentsTitle = new JLabel("Upcoming Appointments");
		appointmentsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		appointmentsTitle.setAlignmentX(CENTER_ALIGNMENT);

		dashboardContent.add(appointmentsTitle);
		dashboardContent.add(Box.createVerticalStrut(10));

		// Table
		String[] columns = {
				"Pet", "Veterinarian", "Date & Time", "Status"
		};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) { //anonymous class
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		try {
			List<Appointment> appointments = appointmentdb.getUpcomingAppointments(user);

			for (Appointment appointment : appointments) {
				tableModel.addRow(new Object[] {
						appointment.getPetName(),
						appointment.getVetName(),
						appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
						appointment.getStatus()
				});
			}
		} catch (SQLException e) {
			ErrorHandler.handleSQLException(e);

			tableModel.addRow(new Object[] {
					"ERROR",
					"Could not load appointments",
					"",
					""
			});
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);

			tableModel.addRow(new Object[] {
					"ERROR",
					"Could not load appointments",
					"",
					""
			});
		}

		JTable table = new JTable(tableModel);

		table.setRowHeight(35);

		JScrollPane scrollPane = new JScrollPane(table);

		dashboardContent.add(scrollPane);

		// =========================
		// LOGOUT
		// =========================

		logoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new LoginFrame().setVisible(true);
			}});
	}
}
