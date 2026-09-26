package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.petcare.model.Treatment;

public class ViewTreatmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public ViewTreatmentDialog(Treatment treatment) {

		setTitle("Medical Record");
		setModal(true);
		setSize(new Dimension(500, 450));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(15, 15));

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
		gbc.anchor = GridBagConstraints.CENTER;

		detailsPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.WEST;

		String date = treatment.getTreatmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

		addDetail(detailsPanel, gbc, 1, "Pet:", treatment.getPetName());
		addDetail(detailsPanel, gbc, 2, "Veterinarian:", treatment.getVetName());
		addDetail(detailsPanel, gbc, 3, "Date & Time:", date);
		addDetail(detailsPanel, gbc, 4, "Status:", treatment.getStatus());
		addDetail(detailsPanel, gbc, 5, "Diagnosis:", treatment.getDiagnosis());
		addDetail(detailsPanel, gbc, 6, "Treatment:", treatment.getTreatmentDescription());
		addDetail(detailsPanel, gbc, 7, "Medication:", treatment.getMedication());

		add(detailsPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton closeButton = new JButton("Close");

		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		buttonPanel.add(Box.createHorizontalStrut(205));
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

		JLabel valueLabel = new JLabel(value == null ? "" : value);

		valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

		panel.add(valueLabel, gbc);
	}
}
