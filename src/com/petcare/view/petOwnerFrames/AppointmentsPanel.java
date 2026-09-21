package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petcare.model.User;

public class AppointmentsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public AppointmentsPanel(User user) {

		setLayout(new BorderLayout(15, 15));

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// HEADER

		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Appointments");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("Manage your veterinary appointments");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

		headerText.add(titleLabel);
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);

		JButton bookAppointmentButton = new JButton("+ Book Appointment");
		headerPanel.add(bookAppointmentButton, BorderLayout.EAST);

		add(headerPanel, BorderLayout.NORTH);

		// FILTER BUTTONS

		JPanel filterPanel = new JPanel();

		JButton upcomingButton = new JButton("Upcoming");
		JButton pastButton = new JButton("Past");
		JButton cancelledButton = new JButton("Cancelled");

		filterPanel.add(upcomingButton);
		filterPanel.add(pastButton);
		filterPanel.add(cancelledButton);

		// TABLE

		String[] columns = {"Pet", "Veterinarian", "Date & Time", "Status", "Actions"};
		//"Actions" column to be removed for past and cancelled appointments

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable appointmentsTable = new JTable(tableModel);
		appointmentsTable.setRowHeight(35);

		JScrollPane scrollPane = new JScrollPane(appointmentsTable);

		// CONTENT

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}
}
