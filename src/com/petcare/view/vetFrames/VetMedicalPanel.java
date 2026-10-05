/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.petcare.view.vetFrames;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Treatment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class VetMedicalPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");

    private VetController controller;
    private List<Treatment> treatments = new ArrayList<>();
    // The treatments currently shown in the table, in row order
    private final List<Treatment> shown = new ArrayList<>();
    private User user;

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Pet", "Owner", "Date", "Diagnosis", "Status", "Actions"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form VetMedicalPanel
     */
    public VetMedicalPanel(User user) {
        this.user = user;
        controller = new VetController();

        initComponents();

        UiStyle.styleTable(medicalRecordsTable);
        UiStyle.styleScrollPane(scrollPane);
        initSearch();
        initTableBehaviour();

        refreshRecords();
    }

    /**
     * Filters the table as the user types.
     */
    private void initSearch() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

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
    }

    /**
     * "View" link column: renderer, click and hover cursor.
     */
    private void initTableBehaviour() {
        medicalRecordsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                label.setText("<html><u>View</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    label.setForeground(UiStyle.ACCENT);
                }

                return label;
            }
        });

        medicalRecordsTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                int row = medicalRecordsTable.rowAtPoint(e.getPoint());
                int column = medicalRecordsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 5) {
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

                // Row N of the table is always shown.get(N), even after searching
                if (row >= 0 && column == 5 && row < shown.size()) {

                    ViewMedicalDialog dialog = new ViewMedicalDialog(shown.get(row));
                    dialog.setVisible(true);
                }
            }
        });
    }

    /**
     * Loads the vet's medical records from the database. Returns false if
     * loading failed (the table then shows the error row).
     */
    private boolean loadMedicalRecords() {

        try {
            treatments = controller.getVetMedicalRecords(user);
            return true;

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, medicalRecordsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, medicalRecordsTable, tableModel);
        }
        return false;
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void filterMedicalRecords() {

        String search = txtSearch.getText().trim().toLowerCase();

        tableModel.setRowCount(0);
        shown.clear();

        for (Treatment treatment : treatments) {

            String date = treatment.getTreatmentDate().toLocalDateTime().format(DATE_FORMAT);

            if (lower(treatment.getPetName()).contains(search)
                    || lower(treatment.getOwnerName()).contains(search)
                    || date.toLowerCase().contains(search)
                    || lower(treatment.getDiagnosis()).contains(search)
                    || lower(treatment.getStatus()).contains(search)) {

                shown.add(treatment);

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

    /**
     * Reloads the records and re-applies the current search.
     */
    public void refreshRecords() {
        if (loadMedicalRecords()) {
            filterMedicalRecords();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        medicalRecordsTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Medical Records");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("View the medical history of your patients");

        lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(73, 80, 87));
        lblSearch.setText("Search");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(33, 37, 41));
        txtSearch.setToolTipText("Search by pet name or owner name");
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        medicalRecordsTable.setModel(tableModel);
        scrollPane.setViewportView(medicalRecordsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitle)
            .addComponent(lblSubtitle)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblSearch)
                .addGap(10, 10, 10)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(4, 4, 4)
                .addComponent(lblSubtitle)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable medicalRecordsTable;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
