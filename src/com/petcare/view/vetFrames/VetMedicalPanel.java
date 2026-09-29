package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import com.petcare.model.Treatment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class VetMedicalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable medicalRecordsTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;

	private VetController controller;
	private List<Treatment> treatments = new ArrayList<>();
	private User user;

	public VetMedicalPanel(User user) {
		
		this.user = user;
		controller = new VetController();

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

		JLabel searchLabel = new JLabel("Search:");
		
		searchField = new JTextField();
		searchField.setToolTipText("Search by pet name or owner name");
		searchField.getDocument().addDocumentListener(new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				filterMedicalRecords();
			}
			public void removeUpdate(DocumentEvent e) {
				filterMedicalRecords();
			}
			public void changedUpdate(DocumentEvent e) {
				filterMedicalRecords();
			}
		});
		
		filterPanel.add(searchLabel, BorderLayout.WEST);
		filterPanel.add(searchField, BorderLayout.CENTER);

		String[] columns = {"Pet", "Owner", "Date", "Diagnosis", "Status", "Actions"};

		tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		medicalRecordsTable = new JTable(tableModel);
		medicalRecordsTable.setRowHeight(35);
		medicalRecordsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				label.setText("<html><u>View</u></html>");
				label.setHorizontalAlignment(JLabel.CENTER);

				return label;
			}
		});
		medicalRecordsTable.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {

				int row = medicalRecordsTable.rowAtPoint(e.getPoint());
				int column = medicalRecordsTable.columnAtPoint(e.getPoint());

				if(row >= 0 && column == 5) {
				    medicalRecordsTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
				} else {
				    medicalRecordsTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		medicalRecordsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				int row = medicalRecordsTable.rowAtPoint(e.getPoint());
				int column = medicalRecordsTable.columnAtPoint(e.getPoint());

				if(row >= 0 && column == 5) {

					String petName = medicalRecordsTable.getValueAt(row, 0).toString();
					String date = medicalRecordsTable.getValueAt(row, 2).toString();

					for(Treatment treatment : treatments) {

						String treatmentDate = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate());

						if(treatment.getPetName().equals(petName) && treatmentDate.equals(date)) {

							ViewMedicalDialog dialog = new ViewMedicalDialog(treatment);
							dialog.setVisible(true);
							break;
						}
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(medicalRecordsTable);

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);

		loadMedicalRecords();
	}

	private void loadMedicalRecords() {

		tableModel.setRowCount(0);

		try {
			treatments = controller.getVetMedicalRecords(user);

			for(Treatment treatment : treatments) {

				String date = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate());

				tableModel.addRow(new Object[] {
						treatment.getPetName(),
						treatment.getOwnerName(),
						date,
						treatment.getDiagnosis(),
						treatment.getStatus(),
						"View"
				});
			}

		} catch(SQLException e) {
			ErrorHandler.handleTableLoadError(e, medicalRecordsTable, tableModel);
		} catch(DatabaseConfigException e) {
			ErrorHandler.handleTableLoadError(e, medicalRecordsTable, tableModel);
		}
	}
	
	private void filterMedicalRecords() {

		String search = searchField.getText().trim().toLowerCase();

		tableModel.setRowCount(0);

		for(Treatment treatment : treatments) {

			String petName = treatment.getPetName().toLowerCase();
			String ownerName = treatment.getOwnerName().toLowerCase();
			String date = new SimpleDateFormat("dd MMM yyyy, h:mm a").format(treatment.getTreatmentDate()).toLowerCase();
			String diagnosis = treatment.getDiagnosis() == null ? "" : treatment.getDiagnosis().toLowerCase();
			String status = treatment.getStatus() == null ? "" : treatment.getStatus().toLowerCase();

			if(petName.contains(search) || ownerName.contains(search) || date.contains(search) || diagnosis.contains(search) || status.contains(search)) {

				tableModel.addRow(new Object[] {
						treatment.getPetName(),
						treatment.getOwnerName(),
						date,
						treatment.getDiagnosis(),
						treatment.getStatus(),
						"View"
				});
			}
		}
	}
	
	public void refreshRecords() {
	    loadMedicalRecords();
	    filterMedicalRecords();
	}
}
