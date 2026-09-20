package com.petcare.ui.adminFrames;
import com.petcare.database.AppointmentDB;
import com.petcare.database.PetDB;
import com.petcare.database.UserDB;
import com.petcare.database.ActivityLogDB;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Activity;
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

public class AdminDashboard extends BaseDashboard {

	private static final long serialVersionUID = 1L;
	
	private UserDB userdb;
	private PetDB petdb;
	private AppointmentDB appointmentdb;
	private ActivityLogDB activitydb;

	public AdminDashboard(User user) {

		super();
		
		userdb = new UserDB();
		petdb = new PetDB();
		appointmentdb = new AppointmentDB();
		activitydb = new ActivityLogDB();
		
		setTitle("PetCare - Admin Dashboard");

		// =========================
		// SIDEBAR
		// =========================

		JButton dashboardBtn = createMenuButton("Dashboard");
		JButton usersBtn = createMenuButton("User Management");
		JButton appointmentsBtn = createMenuButton("Appointments");
		JButton reportsBtn = createMenuButton("Reports");
		JButton systemBtn = createMenuButton("System Management");

		sidebar.add(dashboardBtn);
		sidebar.add(usersBtn);
		sidebar.add(appointmentsBtn);
		sidebar.add(reportsBtn);
		sidebar.add(systemBtn);

		sidebar.add(Box.createVerticalGlue());

		JButton logoutBtn = createMenuButton("Logout");

		sidebar.add(logoutBtn);
		sidebar.add(Box.createVerticalStrut(20));

		// =========================
		// HEADER
		// =========================

		JPanel headerPanel = new JPanel(new BorderLayout());
		JLabel welcomeLabel = new JLabel("Welcome, " + user.getFName());

		welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("System administration overview");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText,BoxLayout.Y_AXIS));
		headerText.add(welcomeLabel);
		headerText.add(Box.createVerticalStrut(5));
		headerText.add(subtitleLabel);

		headerPanel.add(headerText,BorderLayout.WEST);
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		// =========================
		// CARDS
		// =========================

		JPanel cardsPanel = new JPanel(new GridLayout(2,2,15,15));
		
		String petOwnerCount = "N/A";
		String vetCount = "N/A";
		String petTotCount = "N/A";
		String upcomingAppointmentsCount = "N/A";
		
		try {
			petOwnerCount = String.valueOf(userdb.getOwnerCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}
		
		try { 
			vetCount = String.valueOf(userdb.getVetCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}
		
		try {
			petTotCount = String.valueOf(petdb.getTotPetCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}
		
		try {
			upcomingAppointmentsCount = String.valueOf(appointmentdb.getTotUpcomingCount());
		} catch (SQLException | DatabaseConfigException e) {
			e.printStackTrace();
		}

		cardsPanel.add(createCard("Pet Owners", petOwnerCount));
		cardsPanel.add(createCard("Veterinarians", vetCount));
		cardsPanel.add(createCard("Total Pets", petTotCount));
		cardsPanel.add(createCard("Appointments",upcomingAppointmentsCount));

		dashboardContent.add(cardsPanel);
		dashboardContent.add(Box.createVerticalStrut(25));

		// =========================
		// ACTIVITY TITLE
		// =========================

		JLabel activityTitle = new JLabel("Recent System Activity");
		activityTitle.setFont(new Font("SansSerif",Font.BOLD,20));
		activityTitle.setAlignmentX(CENTER_ALIGNMENT);

		dashboardContent.add(activityTitle);
		dashboardContent.add(Box.createVerticalStrut(10));

		// =========================
		// ACTIVITY TABLE
		// =========================

		String[] columns = {"User","Activity","Date"};
		
		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) { //anonymous class
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		try {
			List<Activity> activities = activitydb.getRecentActivity();

			for (Activity activity : activities) {
				tableModel.addRow(new Object[] {
						activity.getUser(),
						activity.getActivity(),
						activity.getActivityDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
				});
			}
		} catch (SQLException e) {
			ErrorHandler.handleSQLException(e);

			tableModel.addRow(new Object[] {
					"ERROR",
					"Could not load activity",
					"",
					""
			});
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);

			tableModel.addRow(new Object[] {
					"ERROR",
					"Could not load activity",
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