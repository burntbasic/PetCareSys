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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class AddTreatmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> appointmentComboBox;
	private List<Appointment> appointments;
	private JLabel petValue;
	private JLabel ownerValue;
	private JTextField diagnosisField;
	private JTextField treatmentField;
	private JTextField medicationField;
	private JComboBox<String> statusComboBox;

	private VetController controller;
	private User user;

	private boolean changed = false;

	public AddTreatmentDialog(User user) {

		this.user = user;
		controller = new VetController();

		setTitle("Add Treatment");
		setSize(500, 450);
		setLayout(new BorderLayout(15, 15));
		setLocationRelativeTo(null);
		setModal(true);

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(7, 5, 7, 5);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel titleLabel = new JLabel("Add Treatment");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		formPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		JLabel appointmentLabel = new JLabel("Appointment");
		appointmentComboBox = new JComboBox<>();

		JLabel petLabel = new JLabel("Pet");
		petValue = new JLabel("Select an appointment");

		JLabel ownerLabel = new JLabel("Owner");
		ownerValue = new JLabel("Select an appointment");

		JLabel diagnosisLabel = new JLabel("Diagnosis");
		diagnosisField = new JTextField(20);

		JLabel treatmentLabel = new JLabel("Treatment");
		treatmentField = new JTextField(20);

		JLabel medicationLabel = new JLabel("Medication");
		medicationField = new JTextField(20);

		JLabel statusLabel = new JLabel("Status");
		statusComboBox = new JComboBox<>(new String[] {"Pending", "Completed"});

		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(appointmentLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(appointmentComboBox, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(petLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(petValue, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(ownerLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(ownerValue, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		formPanel.add(diagnosisLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(diagnosisField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		formPanel.add(treatmentLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(treatmentField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		formPanel.add(medicationLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(medicationField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 7;
		formPanel.add(statusLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(statusComboBox, gbc);

		add(formPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton cancelButton = new JButton("Cancel");
		JButton addButton = new JButton("Add Treatment");

		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		add(buttonPanel, BorderLayout.SOUTH);

		appointmentComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int index = appointmentComboBox.getSelectedIndex();

				if(index >= 0) {

					Appointment appointment = appointments.get(index);

					petValue.setText(appointment.getPetName());
					ownerValue.setText(appointment.getOwnerName());
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTreatment();
			}
		});
		loadAppointments();
	}

	private void loadAppointments() {

		try {
			appointments = controller.getVetCompletedForTreatment(user);

			for(Appointment appointment : appointments) {

				String date = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(appointment.getAppointmentDate());

				appointmentComboBox.addItem(appointment.getPetName() + " - " + date);
			}

			if(appointments.isEmpty()) {

				JOptionPane.showMessageDialog(AddTreatmentDialog.this, "There are no completed appointments available.", "No Appointments", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			appointmentComboBox.setSelectedIndex(0);

		} catch(SQLException e) {
			ErrorHandler.handleSQLException(e);
		} catch(DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
		}
	}

	private void addTreatment() {

		int index = appointmentComboBox.getSelectedIndex();

		if(index < 0) {

			JOptionPane.showMessageDialog(AddTreatmentDialog.this, "Please select an appointment.", "Validation Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Appointment appointment = appointments.get(index);

		String diagnosis = diagnosisField.getText().trim();
		String treatmentDescription = treatmentField.getText().trim();
		String medication = medicationField.getText().trim();
		String status = (String) statusComboBox.getSelectedItem();

		if(diagnosis.isEmpty()) {

			JOptionPane.showMessageDialog(AddTreatmentDialog.this, "Diagnosis cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(treatmentDescription.isEmpty()) {

			JOptionPane.showMessageDialog(AddTreatmentDialog.this, "Treatment cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if(medication.isEmpty()) {

			JOptionPane.showMessageDialog(AddTreatmentDialog.this, "Medication cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			controller.addTreatment(user, appointment.getAppointmentId(), diagnosis, treatmentDescription, medication, status);

			changed = true;

			JOptionPane.showMessageDialog(AddTreatmentDialog.this, "Treatment added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
			dispose();

		} catch(IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(AddTreatmentDialog.this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
		} catch(SQLException ex) {
			ErrorHandler.handleSQLException(ex);
		} catch(DatabaseConfigException ex) {
			ErrorHandler.handleDatabaseConfigException(ex);
		}
	}

	public boolean isChanged() {
		return changed;
	}
}
