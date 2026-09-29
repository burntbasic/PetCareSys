package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class MyPetsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private PetOwnerController controller;
	private User user;
	private List<Pet> pets;
	
	private DefaultTableModel tableModel;
	private JTable petsTable;
	/**
	 * Create the panel.
	 */
	public MyPetsPanel(User user) {

		controller = new PetOwnerController();
		this.user = user;

		setLayout(new BorderLayout(15, 15));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// HEADER
		JPanel headerPanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("My Pets");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

		JLabel subtitleLabel = new JLabel("Manage your registered pets");

		JPanel headerText = new JPanel();
		headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

		headerText.add(titleLabel);
		headerText.add(subtitleLabel);

		headerPanel.add(headerText, BorderLayout.WEST);

		JButton addPetButton = new JButton("+ Add Pet");
		addPetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addPetButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {

		        AddPetDialog dialog = new AddPetDialog(user);
		        dialog.setVisible(true);

		        if (dialog.isChanged()) {
		            loadPets();
		        }
		    }
		});
		headerPanel.add(addPetButton, BorderLayout.EAST);

		add(headerPanel, BorderLayout.NORTH);

		// TABLE
		String[] columns = {"Name", "Species", "Gender", "Actions"};

		tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		petsTable = new JTable(tableModel);
		petsTable.setRowHeight(35);
		
		loadPets();

		DefaultTableCellRenderer editRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				label.setText("<html><u>Edit</u></html>");
				label.setHorizontalAlignment(JLabel.CENTER);

				return label;
			}
		};

		petsTable.getColumnModel().getColumn(3).setCellRenderer(editRenderer);
		petsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				int row = petsTable.rowAtPoint(e.getPoint());
				int column = petsTable.columnAtPoint(e.getPoint());

				if (column == 3 && row >= 0) {

					Pet pet = pets.get(row);

					EditPetDialog dialog = new EditPetDialog(user, pet);
					dialog.setVisible(true);

					if (dialog.isChanged()) {
						loadPets();
					}
				}
			}
		});
		petsTable.addMouseMotionListener(new MouseAdapter() { 
			public void mouseMoved(MouseEvent e) { 

				int row = petsTable.rowAtPoint(e.getPoint());
				int column = petsTable.columnAtPoint(e.getPoint());

				if (column == 3 && row >= 0 && row < pets.size()) { 
					petsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
				} else { 
					petsTable.setCursor(Cursor.getDefaultCursor()); 
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(petsTable);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void loadPets() {
		tableModel.setRowCount(0);

		try {
			pets = controller.getOwnerPets(user);

			for (Pet pet : pets) {
				tableModel.addRow(new Object[] {
						pet.getName(),
						pet.getSpecies(),
						pet.getGender(),
						"Edit"
				});
			}
		} catch (SQLException e) {
		    ErrorHandler.handleTableLoadError(e, petsTable, tableModel);
		} catch (DatabaseConfigException e) {
		    ErrorHandler.handleTableLoadError(e, petsTable, tableModel);
		}
	}
}
