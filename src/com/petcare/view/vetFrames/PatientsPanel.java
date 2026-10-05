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
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class PatientsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private List<Pet> patients = new ArrayList<>();
    private VetController controller;
    private User user;

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Pet", "Species", "Owner", "Last Appointment", "Actions"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form PatientsPanel
     */
    public PatientsPanel(User user) {
        controller = new VetController();
        this.user = user;

        initComponents();

        UiStyle.styleTable(patientsTable);
        UiStyle.styleScrollPane(scrollPane);
        initSearch();
        initTableBehaviour();

        loadPatients();
    }

    /**
     * Filters the table as the user types.
     */
    private void initSearch() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

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
    }

    /**
     * "View" link column: renderer, click and hover cursor.
     */
    private void initTableBehaviour() {
        patientsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {

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

        patientsTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                int column = patientsTable.columnAtPoint(e.getPoint());

                if (column == 4) {
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

                if (row >= 0 && column == 4) {

                    // The table can be filtered, so find the pet by its name and owner
                    String petName = patientsTable.getValueAt(row, 0).toString();
                    String ownerName = patientsTable.getValueAt(row, 2).toString();

                    for (Pet pet : patients) {

                        if (pet.getName().equals(petName) && pet.getOwnerName().equals(ownerName)) {

                            ViewPatientDialog dialog = new ViewPatientDialog(pet);
                            dialog.setVisible(true);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void loadPatients() {

        tableModel.setRowCount(0);

        try {
            patients = controller.getVetPatients(user);

            for (Pet pet : patients) {

                String lastAppointment = pet.getLastAppointment().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

                tableModel.addRow(new Object[] {
                        pet.getName(),
                        pet.getSpecies(),
                        pet.getOwnerName(),
                        lastAppointment,
                        "View"
                });
            }
        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, patientsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, patientsTable, tableModel);
        }
    }

    private void filterPatients() {

        String search = txtSearch.getText().trim().toLowerCase();

        tableModel.setRowCount(0);

        for (Pet pet : patients) {

            String petName = pet.getName().toLowerCase();
            String species = pet.getSpecies().toLowerCase();
            String ownerName = pet.getOwnerName().toLowerCase();

            String lastAppointment = pet.getLastAppointment().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

            if (petName.contains(search) || species.contains(search) || ownerName.contains(search) || lastAppointment.toLowerCase().contains(search)) {

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
        patientsTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Patients");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("View and manage your patients");

        lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(73, 80, 87));
        lblSearch.setText("Search");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(33, 37, 41));
        txtSearch.setToolTipText("Search by pet name");
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        patientsTable.setModel(tableModel);
        scrollPane.setViewportView(patientsTable);

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
    private javax.swing.JTable patientsTable;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
