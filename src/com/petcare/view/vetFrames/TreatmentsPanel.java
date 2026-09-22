package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.petcare.model.User;

public class TreatmentsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public TreatmentsPanel(User user) {
		setLayout(new BorderLayout(15, 15));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Treatments");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("Manage your current and completed treatments");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

		headerText.add(titleLabel);
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);

		JButton addTreatmentButton = new JButton("+ Add Treatment");
		headerPanel.add(addTreatmentButton, BorderLayout.EAST);

		add(headerPanel, BorderLayout.NORTH);

		JPanel filterPanel = new JPanel();

		JButton pendingButton = new JButton("Pending");
		JButton completedButton = new JButton("Completed");

		filterPanel.add(pendingButton);
		filterPanel.add(completedButton);

		String[] columns = {"Pet", "Owner", "Diagnosis", "Treatment", "Status", "Date", "Actions"};

		DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable treatmentsTable = new JTable(tableModel);
		treatmentsTable.setRowHeight(35);

		JScrollPane scrollPane = new JScrollPane(treatmentsTable);

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}
}
