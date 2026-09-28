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
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.petcare.model.Treatment;

public class ViewMedicalDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public ViewMedicalDialog(Treatment treatment) {

		setTitle("Medical Record");
		setSize(500, 450);
		setLayout(new BorderLayout(15, 15));
		setLocationRelativeTo(null);
		setModal(true);

		// DETAILS
		JPanel detailsPanel = new JPanel(new GridBagLayout());
		detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 5, 8, 5);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel titleLabel = new JLabel("Medical Record");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		detailsPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		addDetail(detailsPanel, gbc, 1, "Pet", treatment.getPetName());
		addDetail(detailsPanel, gbc, 2, "Owner", treatment.getOwnerName());
		addDetail(detailsPanel, gbc, 3, "Veterinarian", treatment.getVetName());
		addDetail(detailsPanel, gbc, 4, "Date", new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate()));
		addDetail(detailsPanel, gbc, 5, "Diagnosis", treatment.getDiagnosis());
		addDetail(detailsPanel, gbc, 6, "Treatment", treatment.getTreatmentDescription());
		addDetail(detailsPanel, gbc, 7, "Medication", treatment.getMedication());
		addDetail(detailsPanel, gbc, 8, "Status", treatment.getStatus());
		add(detailsPanel, BorderLayout.CENTER);

		// BUTTONS
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton closeButton = new JButton("Close");
		closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		buttonPanel.add(closeButton);

		add(buttonPanel, BorderLayout.SOUTH);

		// CLOSE
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	private void addDetail(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {

		JLabel labelComponent = new JLabel(label);
		labelComponent.setFont(new Font("SansSerif", Font.BOLD, 14));

		JLabel valueComponent = new JLabel(value == null ? "N/A" : value);

		valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 14));

		gbc.gridx = 0;
		gbc.gridy = row;

		panel.add(labelComponent, gbc);

		gbc.gridx = 1;

		panel.add(valueComponent, gbc);
	}
}
