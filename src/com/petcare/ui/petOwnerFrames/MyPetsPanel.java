package com.petcare.ui.petOwnerFrames;
import com.petcare.model.User;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyPetsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public MyPetsPanel(User user) {
		setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("My Pets");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitleLabel =
                new JLabel("Manage your registered pets");

        JPanel headerText = new JPanel();
        headerText.setLayout(new javax.swing.BoxLayout(
                headerText, javax.swing.BoxLayout.Y_AXIS));

        headerText.add(titleLabel);
        headerText.add(subtitleLabel);

        headerPanel.add(headerText, BorderLayout.WEST);

        JButton addPetButton = new JButton("+ Add Pet");
        headerPanel.add(addPetButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // TABLE
        String[] columns = {
                "Name",
                "Species",
                "Gender",
                "Actions"
        };

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        	public boolean isCellEditable(int row, int column) {
        		return false;
        		}
        	};

        JTable petsTable = new JTable(tableModel);
        petsTable.setRowHeight(35);

        JScrollPane scrollPane = new JScrollPane(petsTable);

        add(scrollPane, BorderLayout.CENTER);
	}

}
