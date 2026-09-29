package com.petcare.view.adminFrames;

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

import com.petcare.model.Appointment;

public class ViewAppointmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public ViewAppointmentDialog(Appointment appointment) {

		setTitle("Appointment Details");
		setModal(true);
		setSize(new Dimension(450, 350));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(15, 15));

		JPanel detailsPanel = new JPanel(new GridBagLayout());

		detailsPanel.setBorder(
				BorderFactory.createEmptyBorder(
						20, 25, 10, 25));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(8, 5, 8, 5);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel titleLabel = new JLabel(
				"Appointment Details");

		titleLabel.setFont(
				new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		detailsPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		String date = appointment.getAppointmentDate()
				.toLocalDateTime()
				.format(DateTimeFormatter.ofPattern(
						"dd MMM yyyy, h:mm a"));

		addDetail(
				detailsPanel,
				gbc,
				1,
				"Pet:",
				appointment.getPetName());

		addDetail(
				detailsPanel,
				gbc,
				2,
				"Owner:",
				appointment.getOwnerName());

		addDetail(
				detailsPanel,
				gbc,
				3,
				"Veterinarian:",
				appointment.getVetName());

		addDetail(
				detailsPanel,
				gbc,
				4,
				"Date & Time:",
				date);

		addDetail(
				detailsPanel,
				gbc,
				5,
				"Status:",
				appointment.getStatus());

		addDetail(
				detailsPanel,
				gbc,
				6,
				"Reason:",
				appointment.getReason());

		add(detailsPanel, BorderLayout.CENTER);

		JPanel buttonPanel =
				new JPanel(new FlowLayout(
						FlowLayout.RIGHT));

		buttonPanel.add(
				Box.createHorizontalStrut(205));

		JButton closeButton =
				new JButton("Close");

		closeButton.setCursor(
				Cursor.getPredefinedCursor(
						Cursor.HAND_CURSOR));

		closeButton.addActionListener(
				new ActionListener() {

					public void actionPerformed(
							ActionEvent e) {

						dispose();
					}
				});

		buttonPanel.add(closeButton);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void addDetail(
			JPanel panel,
			GridBagConstraints gbc,
			int row,
			String label,
			String value) {

		gbc.gridx = 0;
		gbc.gridy = row;

		JLabel nameLabel =
				new JLabel(label);

		nameLabel.setFont(
				new Font("SansSerif", Font.BOLD, 14));

		panel.add(nameLabel, gbc);

		gbc.gridx = 1;

		JLabel valueLabel =
				new JLabel(value == null ? "" : value);

		valueLabel.setFont(
				new Font("SansSerif", Font.PLAIN, 14));

		panel.add(valueLabel, gbc);
	}
}