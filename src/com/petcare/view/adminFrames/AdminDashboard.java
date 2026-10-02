package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Activity;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.BaseDashboard;
import com.petcare.view.LoginFrame;

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
		// SIDEBAR
		// =========================

		JButton dashboardBtn = createMenuButton("Dashboard");
		JButton usersBtn = createMenuButton("User Management");
		JButton appointmentsBtn = createMenuButton("Appointments");
		JButton reportsBtn = createMenuButton("Reports");
		sidebar.add(dashboardBtn);
		sidebar.add(usersBtn);
		sidebar.add(appointmentsBtn);
		sidebar.add(reportsBtn);

		sidebar.add(Box.createVerticalGlue());

		JButton logoutBtn = createMenuButton("Logout");

		sidebar.add(logoutBtn);
		sidebar.add(Box.createVerticalStrut(20));

		// =========================
		// HEADER
		// =========================

		JPanel dashboardPanel = new JPanel(new BorderLayout(0 ,20));

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

		JLabel welcomeLabel = new JLabel("Welcome, " + user.getFName());
		welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("System administration overview");

		headerPanel.add(welcomeLabel);
		headerPanel.add(Box.createVerticalStrut(5));
		headerPanel.add(subtitleLabel);

		dashboardPanel.add(headerPanel, BorderLayout.NORTH);

		// =========================
		// MAIN CONTENT
		// =========================

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// =========================
		// CARDS
		// =========================

		JPanel cardsPanel = new JPanel(new GridLayout(2,2,15,15));

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

		cardsPanel.add(createCard("Pet Owners", petOwnerCount));
		cardsPanel.add(createCard("Veterinarians", vetCount));
		cardsPanel.add(createCard("Total Pets", petTotCount));
		cardsPanel.add(createCard("Appointments",upcomingAppointmentsCount));

		mainPanel.add(cardsPanel);
		mainPanel.add(Box.createVerticalStrut(25));

		// =========================
		// ACTIVITY TITLE
		// =========================

		JLabel activityTitle = new JLabel("Recent System Activity");
		activityTitle.setFont(new Font("SansSerif",Font.BOLD,20));
		activityTitle.setAlignmentX(CENTER_ALIGNMENT);

		mainPanel.add(activityTitle);
		mainPanel.add(Box.createVerticalStrut(10));

		// =========================
		// ACTIVITY TABLE
		// =========================

		String[] columns = {"User","Activity","Date"};

		activityTableModel = new DefaultTableModel(columns, 0) { //anonymous class
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		activityTable = new JTable(activityTableModel);
		activityTable.setRowHeight(35);
		
		loadRecentActivity(activityTable);

		JScrollPane scrollPane = new JScrollPane(activityTable);

		mainPanel.add(scrollPane);

		dashboardPanel.add(mainPanel, BorderLayout.CENTER);
		contentPanel.add(dashboardPanel, "dashboard");

		UserManagementPanel usersPanel = new UserManagementPanel(user);

		AdminAppointmentsPanel appointmentsPanel = new AdminAppointmentsPanel(user);
		
		AdminReportsPanel reportsPanel = new AdminReportsPanel(user);

		contentPanel.add(usersPanel, "users");
		contentPanel.add(appointmentsPanel, "appointments");
		contentPanel.add(reportsPanel, "reports");

		dashboardBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadRecentActivity(activityTable);
				CardLayout layout = (CardLayout) contentPanel.getLayout();
				layout.show(contentPanel, "dashboard");
			}
		});

		usersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout layout = (CardLayout) contentPanel.getLayout();
				layout.show(contentPanel, "users");
			}
		});

		appointmentsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout layout = (CardLayout) contentPanel.getLayout();
				layout.show(contentPanel, "appointments");
			}
		});

		reportsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout layout = (CardLayout) contentPanel.getLayout();
				layout.show(contentPanel, "reports");
			}
		});

		// =========================
		// LOGOUT
		// =========================

		logoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new LoginFrame().setVisible(true);
			}});
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
