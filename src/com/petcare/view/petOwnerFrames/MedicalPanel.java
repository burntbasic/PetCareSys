/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.petcare.view.petOwnerFrames;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.model.Treatment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class MedicalPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private PetOwnerController controller;
    private User user;
    private List<Treatment> treatments;

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Pet", "Date", "Veterinarian", "Actions"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form MedicalPanel
     */
    public MedicalPanel(User user) {
        this.user = user;
        controller = new PetOwnerController();

        initComponents();

        UiStyle.styleTable(medicalRecordsTable);
        UiStyle.styleScrollPane(scrollPane);
        initPetRenderer();
        initTableBehaviour();

        loadPets();
    }

    /**
     * Shows the pet's name in the dropdown instead of the object itself.
     */
    private void initPetRenderer() {
        cmbPet.setRenderer(new DefaultListCellRenderer() {
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
    }

    /**
     * "View" link column: renderer, click and hover cursor.
     */
    private void initTableBehaviour() {
        medicalRecordsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                label.setText("<html><u>View</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (!isSelected) {
                    label.setForeground(UiStyle.ACCENT);
                }

                return label;
            }
        });

        medicalRecordsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = medicalRecordsTable.rowAtPoint(e.getPoint());
                int column = medicalRecordsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 3 && treatments != null && row < treatments.size()) {

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
    }

    private void loadPets() {

        try {
            List<Pet> pets = controller.getOwnerPets(user);

            for (Pet pet : pets) {
                cmbPet.addItem(pet);
            }

            if (!pets.isEmpty()) {
                cmbPet.setSelectedIndex(0);
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    public void refreshPets() {
        cmbPet.removeAllItems();
        tableModel.setRowCount(0);
        treatments = null;
        loadPets();
    }

    private void loadMedicalRecords(Pet pet) {

        try {
            treatments = controller.getPetMedicalRecords(user, pet.getPetId());

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
        lblPet = new javax.swing.JLabel();
        cmbPet = new javax.swing.JComboBox<>();
        scrollPane = new javax.swing.JScrollPane();
        medicalRecordsTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Medical Records");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("View the medical history of your pets");

        lblPet.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPet.setForeground(new java.awt.Color(73, 80, 87));
        lblPet.setText("Pet");

        cmbPet.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbPet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbPet.addActionListener(this::cmbPetActionPerformed);

        medicalRecordsTable.setModel(tableModel);
        scrollPane.setViewportView(medicalRecordsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitle)
            .addComponent(lblSubtitle)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblPet)
                .addGap(10, 10, 10)
                .addComponent(cmbPet, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(lblPet)
                    .addComponent(cmbPet, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbPetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPetActionPerformed
        Pet selectedPet = (Pet) cmbPet.getSelectedItem();

        if (selectedPet != null) {
            loadMedicalRecords(selectedPet);
        }
    }//GEN-LAST:event_cmbPetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<com.petcare.model.Pet> cmbPet;
    private javax.swing.JLabel lblPet;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable medicalRecordsTable;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
