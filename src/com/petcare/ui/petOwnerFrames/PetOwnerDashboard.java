package com.petcare.ui.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.petcare.database.AppointmentDB;
import com.petcare.database.PetDB;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.ui.LoginFrame;
import com.petcare.util.ErrorHandler;

public class PetOwnerDashboard extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Color sidebarColor = new Color(30, 32, 40);
    private final Color cardColor = new Color(42, 44, 54);
    
    //Helper methods
    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 10)
        );
        button.setMargin(new Insets(0, 0, 0, 0));

        return button;
    }

    private JPanel createCard(String title, String value) {

        JPanel card = new JPanel();
        card.setBackground(cardColor);
        card.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }
    
    private AppointmentDB appointmentdb;
    private PetDB petdb;

    public PetOwnerDashboard(User user) {
    	
    	appointmentdb = new AppointmentDB();
        petdb = new PetDB();

        setTitle("PetCare - Pet Owner Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main layout
        getContentPane().setLayout(new BorderLayout());

        // =========================
        // SIDEBAR
        // =========================

        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("PetCareSys");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        logo.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(logo);

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

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

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

        // =========================
        // DASHBOARD CONTENT
        // =========================

        JPanel dashboardContent = new JPanel();
        dashboardContent.setLayout(new BoxLayout(dashboardContent, BoxLayout.Y_AXIS));

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
                "Pet", "Veterinarian", "Date", "Status"
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
        				appointment.getAppointmentDate(),
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

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(dashboardContent, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

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
