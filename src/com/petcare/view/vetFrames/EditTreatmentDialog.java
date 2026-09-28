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
import com.petcare.model.Treatment;
import com.petcare.util.ErrorHandler;

public class EditTreatmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextField diagnosisField;
	private JTextField treatmentField;
	private JTextField medicationField;
	private JComboBox<String> statusComboBox;

	private VetController controller;
	private boolean changed = false;

	public EditTreatmentDialog(Treatment treatment) {

		controller = new VetController();

		setTitle("Edit Treatment");
		setSize(500, 400);
		setLayout(new BorderLayout(15, 15));
		setLocationRelativeTo(null);
		setModal(true);


		// FORM
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(
				BorderFactory.createEmptyBorder(20, 25, 10, 25));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(7, 5, 7, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel titleLabel = new JLabel("Edit Treatment");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		formPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		// PET
		gbc.gridx = 0;
		gbc.gridy = 1;

		JLabel petLabel = new JLabel("Pet");
		petLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		formPanel.add(petLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(new JLabel(treatment.getPetName()),gbc);

		// OWNER
		gbc.gridx = 0;
		gbc.gridy = 2;

		JLabel ownerLabel = new JLabel("Owner");
		ownerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		formPanel.add(ownerLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(new JLabel(treatment.getOwnerName()),gbc);

		// DIAGNOSIS
		gbc.gridx = 0;
		gbc.gridy = 3;

		JLabel diagnosisLabel = new JLabel("Diagnosis");
		diagnosisLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		formPanel.add(diagnosisLabel, gbc);

		gbc.gridx = 1;

		diagnosisField = new JTextField(treatment.getDiagnosis(), 20);

		formPanel.add(diagnosisField, gbc);

		// TREATMENT
		gbc.gridx = 0;
		gbc.gridy = 4;

		JLabel treatmentLabel = new JLabel("Treatment");
		treatmentLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		formPanel.add(treatmentLabel, gbc);

		gbc.gridx = 1;

		treatmentField = new JTextField(treatment.getTreatmentDescription(), 20);

		formPanel.add(treatmentField, gbc);

		// MEDICATION
		gbc.gridx = 0;
		gbc.gridy = 5;

		JLabel medicationLabel = new JLabel("Medication");
		medicationLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		formPanel.add(medicationLabel, gbc);

		gbc.gridx = 1;

		medicationField = new JTextField(treatment.getMedication(), 20);

		formPanel.add(medicationField, gbc);

		// STATUS
		gbc.gridx = 0;
		gbc.gridy = 6;

		JLabel statusLabel = new JLabel("Status");
		statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		formPanel.add(statusLabel, gbc);

		gbc.gridx = 1;

		String[] statuses = {"Pending", "Completed"};

		statusComboBox = new JComboBox<>(statuses);
		statusComboBox.setSelectedItem(treatment.getStatus());

		formPanel.add(statusComboBox, gbc);

		add(formPanel, BorderLayout.CENTER);

		// BUTTONS
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton cancelButton = new JButton("Cancel");
		JButton saveButton = new JButton("Save Changes");

		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);

		add(buttonPanel, BorderLayout.SOUTH);

		// CANCEL
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// SAVE
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String diagnosis = diagnosisField.getText().trim();
				String treatmentDescription = treatmentField.getText().trim();
				String medication = medicationField.getText().trim();
				String status = (String) statusComboBox.getSelectedItem();

				// UI VALIDATION
				if(diagnosis.isEmpty()) {
					JOptionPane.showMessageDialog(EditTreatmentDialog.this, "Diagnosis cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(treatmentDescription.isEmpty()) {
					JOptionPane.showMessageDialog(EditTreatmentDialog.this, "Treatment cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(medication.isEmpty()) {
					JOptionPane.showMessageDialog(EditTreatmentDialog.this, "Medication cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					controller.updateTreatment(treatment, diagnosis, treatmentDescription, medication, status);

					changed = true;

					JOptionPane.showMessageDialog(EditTreatmentDialog.this, "Treatment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
					dispose();

				} catch(IllegalArgumentException ex) {

					JOptionPane.showMessageDialog(EditTreatmentDialog.this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);

				} catch(SQLException ex) {
					ErrorHandler.handleSQLException(ex);
				} catch(DatabaseConfigException ex) {
					ErrorHandler.handleDatabaseConfigException(ex);
				}
			}
		});     
	}

	public boolean isChanged() {
		return changed;
	}
}
