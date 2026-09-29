package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class VetAppointmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private VetController controller;
	private Appointment appointment;

	private boolean changed = false;

	public VetAppointmentDialog(User user, Appointment appointment) {

		this.appointment = appointment;
		controller = new VetController();

		setTitle("Appointment Details");
		setSize(450, 350);
		setLayout(new BorderLayout(15, 15));
		setLocationRelativeTo(null);
		setModal(true);

		JPanel detailsPanel = new JPanel(new GridBagLayout());
		detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(8, 5, 8, 5);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel titleLabel = new JLabel("Appointment Details");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;

		detailsPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.WEST;

		String date = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(appointment.getAppointmentDate());

		addDetail(detailsPanel, gbc, 1, "Pet", appointment.getPetName());
		addDetail(detailsPanel, gbc, 2, "Owner", appointment.getOwnerName());
		addDetail(detailsPanel, gbc, 3, "Date & Time", date);
		addDetail(detailsPanel, gbc, 4, "Reason", appointment.getReason());
		addDetail(detailsPanel, gbc, 5, "Status", appointment.getStatus());

		add(detailsPanel, BorderLayout.CENTER);

		// BUTTONS

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton closeButton = new JButton("Close");
		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        dispose();
		    }
		});

		buttonPanel.add(closeButton);

		if("Scheduled".equals(appointment.getStatus())) {

			JButton completeButton = new JButton("Complete Appointment");
			completeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			completeButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			        completeAppointment(user);
			    }
			});

			buttonPanel.add(completeButton);
		}

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addDetail(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {

		gbc.gridx = 0;
		gbc.gridy = row;

		JLabel nameLabel = new JLabel(label);
	    nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

	    panel.add(nameLabel, gbc);

	    gbc.gridx = 1;

	    JLabel valueLabel = new JLabel(value == null ? "" : value);
	    valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

	    panel.add(valueLabel, gbc);
	}

	private void completeAppointment(User user) {

		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to mark this appointment as completed?", "Complete Appointment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if(result != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			controller.completeAppointment(user, appointment);

			changed = true;

			JOptionPane.showMessageDialog(this, "Appointment completed successfully.", "Appointment Completed", JOptionPane.INFORMATION_MESSAGE);
			dispose();

		} catch(SQLException e) {
			ErrorHandler.handleSQLException(e);
		} catch(DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
		}
	}

	public boolean isChanged() {
		return changed;
	}
}
