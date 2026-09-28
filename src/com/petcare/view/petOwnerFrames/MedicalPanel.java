package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultListCellRenderer;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.model.Pet;
import com.petcare.model.Treatment;

public class MedicalPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JComboBox<Pet> petComboBox;
	private DefaultTableModel tableModel;
	private JTable medicalRecordsTable;
	
	private PetOwnerController controller;
	private User user;
	private List<Treatment> treatments;

	/**
	 * Create the panel.
	 */
	public MedicalPanel(User user) {
		
		this.user = user;
		controller = new PetOwnerController();
		
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

		petComboBox = new JComboBox<>();
		petComboBox.setRenderer(new DefaultListCellRenderer() {
		    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		        
		        list.setCursor(new Cursor(Cursor.HAND_CURSOR));

		        if (value instanceof Pet) {
		            Pet pet = (Pet) value;
		            setText(pet.getName());
		        }

		        return this;
		    }
		});
		petComboBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {

		        Pet selectedPet = (Pet) petComboBox.getSelectedItem();

		        if (selectedPet != null) {
		            loadMedicalRecords(selectedPet);
		        }
		    }
		});

		petComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

		filterPanel.add(petLabel, BorderLayout.WEST);
		filterPanel.add(petComboBox, BorderLayout.CENTER);

		// TABLE

		String[] columns = {"Pet", "Date", "Veterinarian", "Actions"};

		tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		medicalRecordsTable = new JTable(tableModel);
		medicalRecordsTable.setRowHeight(35);

		medicalRecordsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
		    public Component getTableCellRendererComponent(
		            JTable table,
		            Object value,
		            boolean isSelected,
		            boolean hasFocus,
		            int row,
		            int column) {

		        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		        label.setText("<html><u>View</u></html>");
		        label.setHorizontalAlignment(JLabel.CENTER);
		        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

		        return label;
		    }
		});

		JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);
		
		loadPets();
		
		medicalRecordsTable.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {

		        int row = medicalRecordsTable.rowAtPoint(e.getPoint());
		        int column = medicalRecordsTable.columnAtPoint(e.getPoint());

		        if (row >= 0 && column == 3) {

		            Treatment treatment = treatments.get(row);

		            ViewTreatmentDialog dialog = new ViewTreatmentDialog(treatment);
		            dialog.setVisible(true);
		        }
		    }
		});
		
		medicalRecordsTable.addMouseMotionListener(new MouseMotionAdapter() {
		    public void mouseMoved(MouseEvent e) {

		        int column = medicalRecordsTable.columnAtPoint(e.getPoint());

		        if (column == 3) {
		            medicalRecordsTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
		        } else {
		            medicalRecordsTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		        }
		    }
		});

		// CONTENT

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}
	
	private void loadPets() {

	    try {
	        List<Pet> pets = controller.getOwnerPets(user);

	        for (Pet pet : pets) {
	            petComboBox.addItem(pet);
	        }

	        if (!pets.isEmpty()) {
	            petComboBox.setSelectedIndex(0);
	        }

	    } catch (SQLException e) {
	        ErrorHandler.handleSQLException(e);
	    } catch (DatabaseConfigException e) {
	        ErrorHandler.handleDatabaseConfigException(e);
	    }
	}
	
	public void refreshPets() {
	    petComboBox.removeAllItems();
	    tableModel.setRowCount(0);
	    treatments = null;
	    loadPets();
	}
	
	private void loadMedicalRecords(Pet pet) {

	    try {
	        treatments = controller.getPetMedicalRecords(user,pet.getPetId());

	        tableModel.setRowCount(0);

	        for (Treatment treatment : treatments) {

	            String date = treatment.getTreatmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

	            tableModel.addRow(new Object[] {
	                    treatment.getPetName(),
	                    date,
	                    treatment.getVetName(),
	                    "View"
	            });
	        }

	    } catch (SQLException e) {
	        ErrorHandler.handleTableLoadError(e, medicalRecordsTable, tableModel);
	    } catch (DatabaseConfigException e) {
	        ErrorHandler.handleTableLoadError(e, medicalRecordsTable, tableModel);
	    }
	}
	
}


