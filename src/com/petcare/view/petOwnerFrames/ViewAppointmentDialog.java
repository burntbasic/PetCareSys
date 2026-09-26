package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.util.ErrorHandler;

public class ViewAppointmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private PetOwnerController controller;
	private boolean changed = false;

	public boolean isChanged() {
		return changed;
	}

	public ViewAppointmentDialog(Appointment appointment) {

		controller = new PetOwnerController();

		setTitle("Appointment Details");
		setModal(true);
		setSize(new Dimension(450, 350));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(15, 15));

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
		detailsPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		addDetail(detailsPanel, gbc, 1, "Pet:",appointment.getPetName());
		addDetail(detailsPanel, gbc, 2, "Veterinarian:",appointment.getVetName());
		addDetail(detailsPanel, gbc, 3, "Date & Time:",appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")));
		addDetail(detailsPanel, gbc, 4, "Status:",appointment.getStatus());
		addDetail(detailsPanel, gbc, 5, "Reason:",appointment.getReason());

		add(detailsPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		//"Cancel appointment" button only for upcoming appointments
		if ("Scheduled".equals(appointment.getStatus())) {

			JButton cancelButton = new JButton("Cancel Appointment");
			cancelButton.setBackground(new Color(120, 45, 45));
			cancelButton.setForeground(Color.WHITE);
			cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(ViewAppointmentDialog.this, "Are you sure you want to cancel this appointment?", "Cancel Appointment", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						try {	
							controller.cancelAppointment(appointment);

							changed = true;

							JOptionPane.showMessageDialog(ViewAppointmentDialog.this, "Appointment cancelled successfully.", "Appointment Cancelled", JOptionPane.INFORMATION_MESSAGE);
							dispose();

						} catch (SQLException ex) {
							ErrorHandler.handleSQLException(ex);
						} catch (DatabaseConfigException ex) {
							ErrorHandler.handleDatabaseConfigException(ex);
						}
					}
				}
			});
			
			buttonPanel.add(cancelButton);
			buttonPanel.add(Box.createHorizontalStrut(205));
		}
		JButton closeButton = new JButton("Close");
		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		buttonPanel.add(closeButton);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addDetail(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {

		gbc.gridx = 0;
		gbc.gridy = row;

		JLabel nameLabel = new JLabel(label);
		nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		panel.add(nameLabel, gbc);

		gbc.gridx = 1;

		JLabel valueLabel = new JLabel(value);
		valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

		panel.add(valueLabel, gbc);
	}
}