package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class PatientsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTable patientsTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;

	private List<Pet> patients = new ArrayList<>();
	private VetController controller;
	private User user;

	/**
	 * Create the panel.
	 */
	public PatientsPanel(User user) {
		
		controller = new VetController();
		this.user = user;

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

		searchField = new JTextField();
		searchField.setToolTipText("Search by pet name");

		searchPanel.add(searchLabel, BorderLayout.WEST);
		searchPanel.add(searchField, BorderLayout.CENTER);
		searchField.getDocument().addDocumentListener(new DocumentListener() {

		    public void insertUpdate(DocumentEvent e) {
		        filterPatients();
		    }
		    public void removeUpdate(DocumentEvent e) {
		        filterPatients();
		    }
		    public void changedUpdate(DocumentEvent e) {
		        filterPatients();
		    }
		});

		String[] columns = {"Pet", "Species", "Owner", "Last Appointment", "Actions"};

		tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		patientsTable = new JTable(tableModel);
		patientsTable.setRowHeight(35);
		patientsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {

		    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		        label.setText("<html><u>View</u></html>");
		        label.setHorizontalAlignment(JLabel.CENTER);

		        return label;
		    }
		});
		patientsTable.addMouseMotionListener(new MouseMotionAdapter() {
		    public void mouseMoved(MouseEvent e) {

		        int column = patientsTable.columnAtPoint(e.getPoint());

		        if(column == 4) {
		            patientsTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
		        } else {
		            patientsTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		        }
		    }
		});
		patientsTable.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {

		        int row = patientsTable.rowAtPoint(e.getPoint());
		        int column = patientsTable.columnAtPoint(e.getPoint());

		        if(row >= 0 && column == 4) {

		            String petName = patientsTable.getValueAt(row, 0).toString();

		            for(Pet pet : patients) {

		                if(pet.getName().equals(petName)) {

		                    ViewPatientDialog dialog = new ViewPatientDialog(pet);
		                    dialog.setVisible(true);
		                    break;
		                }
		            }
		        }
		    }
		});

		JScrollPane scrollPane = new JScrollPane(patientsTable);

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(searchPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
		
		loadPatients();
	}
	
	private void loadPatients() {

	    tableModel.setRowCount(0);

	    try {
	        patients = controller.getVetPatients(user);

	        for(Pet pet : patients) {

	            String lastAppointment = pet.getLastAppointment().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

	            tableModel.addRow(new Object[] {
	                    pet.getName(),
	                    pet.getSpecies(),
	                    pet.getOwnerName(),
	                    lastAppointment,
	                    "View"
	            });
	        }
	    } catch(SQLException e) {
	        ErrorHandler.handleTableLoadError(e, patientsTable, tableModel);
	    } catch(DatabaseConfigException e) {
	        ErrorHandler.handleTableLoadError(e, patientsTable, tableModel);
	    }
	}
	
	private void filterPatients() {

		String search = searchField.getText().trim().toLowerCase();

		tableModel.setRowCount(0);

		for(Pet pet : patients) {

			String petName = pet.getName().toLowerCase();
			String species = pet.getSpecies().toLowerCase();
			String ownerName = pet.getOwnerName().toLowerCase();

			String lastAppointment = pet.getLastAppointment().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

			if(petName.contains(search) || species.contains(search) || ownerName.contains(search) || lastAppointment.toLowerCase().contains(search)) {

				tableModel.addRow(new Object[] {
						pet.getName(),
						pet.getSpecies(),
						pet.getOwnerName(),
						lastAppointment,
						"View"
				});
			}
		}
	}
}
