package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petcare.model.User;

public class MedicalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public MedicalPanel(User user) {

		setLayout(new BorderLayout(15, 15));

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// HEADER

		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Medical Records");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("View the medical history of your pets");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

		headerText.add(titleLabel);
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);

		add(headerPanel, BorderLayout.NORTH);

		// FILTER

		JPanel filterPanel = new JPanel(new BorderLayout());

		JLabel petLabel = new JLabel("Pet:");

		String[] pets = {"All Pets"};

		JComboBox<String> petComboBox = new JComboBox<>(pets);
		//Pass pet model to combobox to get pet names

		filterPanel.add(petLabel, BorderLayout.WEST);
		filterPanel.add(petComboBox, BorderLayout.CENTER);

		// TABLE

		String[] columns = {"Pet", "Date", "Veterinarian", "Actions"};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable medicalRecordsTable = new JTable(tableModel);
		medicalRecordsTable.setRowHeight(35);

		JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);

		// CONTENT

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}
}


