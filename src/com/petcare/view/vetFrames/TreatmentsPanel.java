/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.petcare.view.vetFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
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
public class TreatmentsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private String currentFilter = "Pending";

    private VetController controller;
    private List<Treatment> treatments = new ArrayList<>();
    // The treatments currently shown in the table, in row order
    private final List<Treatment> shown = new ArrayList<>();
    private User user;

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Pet", "Owner", "Diagnosis", "Treatment", "Status", "Date & Time", "Actions"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form TreatmentsPanel
     */
    public TreatmentsPanel(User user) {
        this.user = user;
        controller = new VetController();

        initComponents();

        UiStyle.styleTable(treatmentsTable);
        UiStyle.styleScrollPane(scrollPane);
        initTableBehaviour();

        reloadTreatments();
        updateFilterButtons();
    }

    /**
     * "View" link column: renderer, click and hover cursor.
     */
    private void initTableBehaviour() {
        treatmentsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setText("<html><u>View</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    label.setForeground(UiStyle.ACCENT);
                }

                return label;
            }
        });

        treatmentsTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                int row = treatmentsTable.rowAtPoint(e.getPoint());
                int column = treatmentsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 6) {
                    treatmentsTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    treatmentsTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        treatmentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = treatmentsTable.rowAtPoint(e.getPoint());
                int column = treatmentsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 6 && row < shown.size()) {

                    ViewTreatmentDialog dialog = new ViewTreatmentDialog(user, shown.get(row));
                    dialog.setVisible(true);

                    if (dialog.isChanged()) {
                        reloadTreatments();
                    }
                }
            }
        });
    }

    /**
     * Highlights the button of the filter that is currently shown.
     */
    private void updateFilterButtons() {
        styleFilterButton(btnPending, "Pending".equals(currentFilter));
        styleFilterButton(btnCompleted, "Completed".equals(currentFilter));
    }

    private void styleFilterButton(JButton button, boolean active) {
        button.setBackground(active ? UiStyle.ACCENT : Color.WHITE);
        button.setForeground(active ? Color.WHITE : UiStyle.TEXT_DARK);
        button.setBorder(BorderFactory.createLineBorder(active ? UiStyle.ACCENT : new Color(206, 212, 218)));
    }

    /**
     * Loads the vet's treatments from the database. Returns false if loading
     * failed (the table then shows the error row instead of old data).
     */
    private boolean loadTreatments() {

        try {
            treatments = controller.getVetTreatments(user);
            return true;

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, treatmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, treatmentsTable, tableModel);
        }
        return false;
    }

    /**
     * Reloads from the database, then shows the current filter.
     */
    private void reloadTreatments() {
        if (loadTreatments()) {
            filterTreatments(currentFilter);
        }
    }

    private void filterTreatments(String status) {

        tableModel.setRowCount(0);
        shown.clear();

        for (Treatment treatment : treatments) {

            if (treatment.getStatus() != null && treatment.getStatus().equalsIgnoreCase(status)) {

                String date = treatment.getTreatmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

                shown.add(treatment);

                tableModel.addRow(new Object[] {
                        treatment.getPetName(),
                        treatment.getOwnerName(),
                        treatment.getDiagnosis(),
                        treatment.getTreatmentDescription(),
                        treatment.getStatus(),
                        date,
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
        btnAdd = new javax.swing.JButton();
        btnPending = new javax.swing.JButton();
        btnCompleted = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        treatmentsTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Treatments");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Manage your current and completed treatments");

        btnAdd.setBackground(new java.awt.Color(0, 121, 107));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("+ Add Treatment");
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(this::btnAddActionPerformed);

        btnPending.setBackground(new java.awt.Color(0, 121, 107));
        btnPending.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnPending.setForeground(new java.awt.Color(255, 255, 255));
        btnPending.setText("Pending");
        btnPending.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 121, 107)));
        btnPending.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPending.setFocusPainted(false);
        btnPending.addActionListener(this::btnPendingActionPerformed);

        btnCompleted.setBackground(new java.awt.Color(255, 255, 255));
        btnCompleted.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCompleted.setForeground(new java.awt.Color(33, 37, 41));
        btnCompleted.setText("Completed");
        btnCompleted.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));
        btnCompleted.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCompleted.setFocusPainted(false);
        btnCompleted.addActionListener(this::btnCompletedActionPerformed);

        treatmentsTable.setModel(tableModel);
        scrollPane.setViewportView(treatmentsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSubtitle))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnCompleted, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(4, 4, 4)
                        .addComponent(lblSubtitle))
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCompleted, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        AddTreatmentDialog dialog = new AddTreatmentDialog(user);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            reloadTreatments();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnPendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPendingActionPerformed
        currentFilter = "Pending";
        filterTreatments("Pending");
        updateFilterButtons();
    }//GEN-LAST:event_btnPendingActionPerformed

    private void btnCompletedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompletedActionPerformed
        currentFilter = "Completed";
        filterTreatments("Completed");
        updateFilterButtons();
    }//GEN-LAST:event_btnCompletedActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCompleted;
    private javax.swing.JButton btnPending;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable treatmentsTable;
    // End of variables declaration//GEN-END:variables
}
