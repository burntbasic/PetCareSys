package com.petcare.ui.vetFrames;
import com.petcare.database.AppointmentDB;
import com.petcare.database.PetDB;
import com.petcare.database.TreatmentDB;
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

public class VetDashboard extends BaseDashboard {

    private static final long serialVersionUID = 1L;
    
    private AppointmentDB appointmentdb;
    private PetDB petdb;
    private TreatmentDB treatmentdb;

    public VetDashboard(User user) {
    	
    	super();
    	
    	appointmentdb = new AppointmentDB();
    	treatmentdb = new TreatmentDB();
    	petdb = new PetDB();

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
        // MAIN CONTENT
        // =========================

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, Dr. " + user.getLName());

        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Today's veterinary overview");

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

        String today = "N/A";
        String pending = "N/A";
        String patient = "N/A";
        
        try {
        	today = String.valueOf(appointmentdb.getTodayCount(user));
        } catch (SQLException | DatabaseConfigException e) {
        	e.printStackTrace();        	
        }
        
        try {
        	pending = String.valueOf(treatmentdb.getPendingCount(user));
        } catch (SQLException | DatabaseConfigException e) {
        	e.printStackTrace();        	
        }
        
        try {
        	patient = String.valueOf(petdb.getPatientCount(user));
        } catch (SQLException | DatabaseConfigException e) {
        	e.printStackTrace();        	
        }
        
        cardsPanel.add(createCard("Today's Appointments", today));
        cardsPanel.add(createCard("Pending Treatments", pending));
        cardsPanel.add(createCard("Patients Today", patient));

        dashboardContent.add(cardsPanel);
        dashboardContent.add(Box.createVerticalStrut(25));

        // Table title
        JLabel appointmentsTitle = new JLabel("Today's Appointments");
        appointmentsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		appointmentsTitle.setAlignmentX(CENTER_ALIGNMENT);

        dashboardContent.add(appointmentsTitle);
        dashboardContent.add(Box.createVerticalStrut(10));

        // Table
        String[] columns = {
                "Time", "Pet", "Owner", "Reason", "Status"
        };

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) { //anonymous class
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		try {
			List<Appointment> appointments = appointmentdb.getTodaysAppointments(user);
			
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
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
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