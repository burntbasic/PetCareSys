package com.petcare.view.vetFrames;

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

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.BaseDashboard;
import com.petcare.view.LoginFrame;

public class VetDashboard extends BaseDashboard {

    private static final long serialVersionUID = 1L;
    
    private VetController controller;

    public VetDashboard(User user) {
    	
    	super();
    	
    	controller = new VetController();

        setTitle("PetCare - Vet Dashboard");

        // =========================
        // SIDEBAR
        // =========================

        JButton dashboardBtn = createMenuButton("Dashboard");
        JButton appointmentsBtn = createMenuButton("Appointments");
        JButton patientsBtn = createMenuButton("Patients");
        JButton treatmentsBtn = createMenuButton("Treatments");
        JButton medicalBtn = createMenuButton("Medical Records");
        JButton reportsBtn = createMenuButton("Reports");

        sidebar.add(dashboardBtn);
        sidebar.add(appointmentsBtn);
        sidebar.add(patientsBtn);
        sidebar.add(treatmentsBtn);
        sidebar.add(medicalBtn);
        sidebar.add(reportsBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createMenuButton("Logout");

        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));

		// =========================
		// DASHBOARD CONTENT
		// =========================

		JPanel dashboardPanel = new JPanel(new BorderLayout(0 ,20));
		
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel welcomeLabel = new JLabel("Welcome, Dr. " + user.getLName());
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Today's veterinary overview");

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
        
        cardsPanel.add(createCard("Today's Appointments", today));
        cardsPanel.add(createCard("Pending Treatments", pending));
        cardsPanel.add(createCard("Patients Today", patient));

        mainPanel.add(cardsPanel);
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Table title
        JLabel appointmentsTitle = new JLabel("Today's Appointments");
        appointmentsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		appointmentsTitle.setAlignmentX(CENTER_ALIGNMENT);

        mainPanel.add(appointmentsTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        // Table
        String[] columns = {"Time", "Pet", "Owner", "Reason", "Status"};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) { //anonymous class
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
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
			
			tableModel.addRow(new Object[] {
			        "ERROR",
			        "Could not load appointments",
			        "",
			        "",
			        ""
			});
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
			
			tableModel.addRow(new Object[] {
			        "ERROR",
			        "Could not load appointments",
			        "",
			        "",
			        ""
			});
		}
		
		JTable table = new JTable(tableModel);
        table.setRowHeight(35);

        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(scrollPane);
        
        dashboardPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(dashboardPanel, "dashboard");
        
        VetAppointmentsPanel appointmentsPanel = new VetAppointmentsPanel(user);

        PatientsPanel patientsPanel = new PatientsPanel(user);

        TreatmentsPanel treatmentsPanel = new TreatmentsPanel(user);

        VetMedicalPanel medicalPanel = new VetMedicalPanel(user);


        JPanel reportsPanel = new JPanel();
        reportsPanel.add(new JLabel("Reports"));
        
        contentPanel.add(appointmentsPanel, "appointments");
        contentPanel.add(patientsPanel, "patients");
        contentPanel.add(treatmentsPanel, "treatments");
        contentPanel.add(medicalPanel, "medical");
        contentPanel.add(reportsPanel, "reports");
        
        dashboardBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) contentPanel.getLayout();
                layout.show(contentPanel, "dashboard");
            }
        });
        
        appointmentsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) contentPanel.getLayout();
                layout.show(contentPanel, "appointments");
            }
        });
        
        patientsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) contentPanel.getLayout();
                layout.show(contentPanel, "patients");
            }
        });
        
        treatmentsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout) contentPanel.getLayout();
                layout.show(contentPanel, "treatments");
            }
        });
        
        medicalBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	medicalPanel.refreshRecords();
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
