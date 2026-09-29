package com.petcare.view.petOwnerFrames;

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

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.BaseDashboard;
import com.petcare.view.LoginFrame;

public class PetOwnerDashboard extends BaseDashboard {

	private static final long serialVersionUID = 1L;

	private PetOwnerController controller;

	public PetOwnerDashboard(User user) {
		
		super();

		controller = new PetOwnerController();

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
		// DASHBOARD CONTENT
		// =========================
		
		JPanel dashboardPanel = new JPanel(new BorderLayout(0, 20));
		
		// Header
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		
		JLabel welcomeLabel = new JLabel("Welcome back, " + user.getFName() + "!");
		welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
		
		JLabel subtitleLabel = new JLabel("Your PetCare overview");

		headerPanel.add(welcomeLabel);
		headerPanel.add(Box.createVerticalStrut(5));
		headerPanel.add(subtitleLabel);
		
		dashboardPanel.add(headerPanel, BorderLayout.NORTH);

		// =========================
		// MAIN CONTENT
		// =========================

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// Cards
		JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));

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

		cardsPanel.add(createCard("My Pets", petCount));
		cardsPanel.add(createCard("Upcoming Appointments", upcoming));
		cardsPanel.add(createCard("Completed Appointments", completed));

		mainPanel.add(cardsPanel);
		mainPanel.add(Box.createVerticalStrut(25));

		// Table title
		JLabel appointmentsTitle = new JLabel("Upcoming Appointments");
		appointmentsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		appointmentsTitle.setAlignmentX(CENTER_ALIGNMENT);

		mainPanel.add(appointmentsTitle);
		mainPanel.add(Box.createVerticalStrut(10));

		// Table
		String[] columns = {"Pet", "Veterinarian", "Date & Time", "Status"};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) { //anonymous class
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		JTable table = new JTable(tableModel);
		table.setRowHeight(35);

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

		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane);

		dashboardPanel.add(mainPanel, BorderLayout.CENTER);
		contentPanel.add(dashboardPanel, "dashboard");
		
		MyPetsPanel myPetsPanel = new MyPetsPanel(user);

		AppointmentsPanel appointmentsPanel = new AppointmentsPanel(user);

		MedicalPanel medicalPanel = new MedicalPanel(user);

		JPanel reportsPanel = new JPanel();
		reportsPanel.add(new JLabel("Reports"));
		
		contentPanel.add(myPetsPanel, "myPets");
		contentPanel.add(appointmentsPanel, "appointments");
		contentPanel.add(medicalPanel, "medical");
		contentPanel.add(reportsPanel, "reports");
		
		dashboardBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        CardLayout layout = (CardLayout) contentPanel.getLayout();
		        layout.show(contentPanel, "dashboard");
		    }
		});
		
		petsBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        CardLayout layout = (CardLayout) contentPanel.getLayout();
		        layout.show(contentPanel, "myPets");
		    }
		});
		
		appointmentsBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        CardLayout layout = (CardLayout) contentPanel.getLayout();
		        layout.show(contentPanel, "appointments");
		    }
		});
		
		medicalBtn.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	medicalPanel.refreshPets();
		    	CardLayout layout = (CardLayout) contentPanel.getLayout();
		        layout.show(contentPanel, "medical");
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
}
