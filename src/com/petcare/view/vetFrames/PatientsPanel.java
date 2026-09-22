package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.petcare.model.User;

public class PatientsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public PatientsPanel(User user) {

		setLayout(new BorderLayout(15, 15));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Patients");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("View and manage your patients");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

		headerText.add(titleLabel);
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);

		add(headerPanel, BorderLayout.NORTH);

		JPanel searchPanel = new JPanel(new BorderLayout(10, 10));

		JLabel searchLabel = new JLabel("Search:");

		JTextField searchField = new JTextField();
		searchField.setToolTipText("Search by pet name");

		searchPanel.add(searchLabel, BorderLayout.WEST);
		searchPanel.add(searchField, BorderLayout.CENTER);

		String[] columns = {"Pet", "Species", "Owner", "Last Appointment", "Actions"};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable patientsTable = new JTable(tableModel);
		patientsTable.setRowHeight(35);

		JScrollPane scrollPane = new JScrollPane(patientsTable);

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(searchPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}

}
