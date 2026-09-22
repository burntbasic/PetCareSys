package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.petcare.model.User;

public class VetMedicalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public VetMedicalPanel(User user) {
		setLayout(new BorderLayout(15, 15));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Medical Records");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("View the medical history of your patients");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

		headerText.add(titleLabel);
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);

		add(headerPanel, BorderLayout.NORTH);

		JPanel filterPanel = new JPanel(new BorderLayout(10, 10));

		JLabel petLabel = new JLabel("Patient:");

		String[] pets = {"All Patients"};

		JComboBox<String> petComboBox = new JComboBox<>(pets);

		JTextField searchField = new JTextField();
		searchField.setToolTipText("Search by pet name or owner name");

		filterPanel.add(petLabel, BorderLayout.WEST);
		filterPanel.add(petComboBox, BorderLayout.CENTER);
		filterPanel.add(searchField, BorderLayout.EAST);

		String[] columns = {"Pet", "Owner", "Date", "Diagnosis", "Status", "Actions"};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable medicalRecordsTable = new JTable(tableModel);
		medicalRecordsTable.setRowHeight(35);

		JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}

}
