/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.petcare.view.petOwnerFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class AppointmentsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private PetOwnerController controller;
    private List<Appointment> appointments;
    private User user;
    private String currentFilter = "Upcoming";

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Pet", "Veterinarian", "Date & Time", "Status", "Actions"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form AppointmentsPanel
     */
    public AppointmentsPanel(User user) {
        this.user = user;
        controller = new PetOwnerController();

        initComponents();

        UiStyle.styleTable(appointmentsTable);
        UiStyle.styleScrollPane(scrollPane);
        initTableBehaviour();

        loadUpcomingAppointments();
        updateFilterButtons();
    }

    /**
     * "View" link column: renderer, click and hover cursor.
     */
    private void initTableBehaviour() {
        appointmentsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {

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

        appointmentsTable.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {

                int row = appointmentsTable.rowAtPoint(e.getPoint());
                int column = appointmentsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 4) {
                    appointmentsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    appointmentsTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        appointmentsTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                int row = appointmentsTable.rowAtPoint(e.getPoint());
                int column = appointmentsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 4 && appointments != null && row < appointments.size()) {

                    Appointment appointment = appointments.get(row);

                    ViewAppointmentDialog dialog = new ViewAppointmentDialog(user, appointment);
                    dialog.setVisible(true);

                    if (dialog.isChanged()) {
                        refreshAppointments();
                    }
                }
            }
        });
    }

    /**
     * Highlights the button of the filter that is currently shown.
     */
    private void updateFilterButtons() {
        styleFilterButton(btnUpcoming, "Upcoming".equals(currentFilter));
        styleFilterButton(btnPast, "Past".equals(currentFilter));
        styleFilterButton(btnCancelled, "Cancelled".equals(currentFilter));
    }

    private void styleFilterButton(JButton button, boolean active) {
        button.setBackground(active ? UiStyle.ACCENT : Color.WHITE);
        button.setForeground(active ? Color.WHITE : UiStyle.TEXT_DARK);
        button.setBorder(BorderFactory.createLineBorder(active ? UiStyle.ACCENT : new Color(206, 212, 218)));
    }

    private void refreshAppointments() {

        if ("Upcoming".equals(currentFilter)) {
            loadUpcomingAppointments();
        } else if ("Past".equals(currentFilter)) {
            loadPastAppointments();
        } else if ("Cancelled".equals(currentFilter)) {
            loadCancelledAppointments();
        }
    }

    private void loadUpcomingAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getOwnerUpcoming(user);
            fillTable();

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    private void loadPastAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getOwnerPast(user);
            fillTable();

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    private void loadCancelledAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getOwnerCancelled(user);
            fillTable();

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    /**
     * Adds a table row for every loaded appointment.
     */
    private void fillTable() {
        for (Appointment appointment : appointments) {

            tableModel.addRow(new Object[] {
                    appointment.getPetName(),
                    appointment.getVetName(),
                    appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")),
                    appointment.getStatus(),
                    "View"
            });
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
        btnBook = new javax.swing.JButton();
        btnUpcoming = new javax.swing.JButton();
        btnPast = new javax.swing.JButton();
        btnCancelled = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        appointmentsTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Appointments");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Manage your veterinary appointments");

        btnBook.setBackground(new java.awt.Color(0, 121, 107));
        btnBook.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBook.setForeground(new java.awt.Color(255, 255, 255));
        btnBook.setText("+ Book Appointment");
        btnBook.setBorderPainted(false);
        btnBook.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBook.setFocusPainted(false);
        btnBook.addActionListener(this::btnBookActionPerformed);

        btnUpcoming.setBackground(new java.awt.Color(0, 121, 107));
        btnUpcoming.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUpcoming.setForeground(new java.awt.Color(255, 255, 255));
        btnUpcoming.setText("Upcoming");
        btnUpcoming.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 121, 107)));
        btnUpcoming.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpcoming.setFocusPainted(false);
        btnUpcoming.addActionListener(this::btnUpcomingActionPerformed);

        btnPast.setBackground(new java.awt.Color(255, 255, 255));
        btnPast.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnPast.setForeground(new java.awt.Color(33, 37, 41));
        btnPast.setText("Past");
        btnPast.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));
        btnPast.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPast.setFocusPainted(false);
        btnPast.addActionListener(this::btnPastActionPerformed);

        btnCancelled.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelled.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCancelled.setForeground(new java.awt.Color(33, 37, 41));
        btnCancelled.setText("Cancelled");
        btnCancelled.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));
        btnCancelled.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelled.setFocusPainted(false);
        btnCancelled.addActionListener(this::btnCancelledActionPerformed);

        appointmentsTable.setModel(tableModel);
        scrollPane.setViewportView(appointmentsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSubtitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnUpcoming, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnPast, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnCancelled, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(btnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpcoming, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPast, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelled, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        BookAppointmentDialog dialog = new BookAppointmentDialog(user);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            refreshAppointments();
        }
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnUpcomingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpcomingActionPerformed
        currentFilter = "Upcoming";
        loadUpcomingAppointments();
        updateFilterButtons();
    }//GEN-LAST:event_btnUpcomingActionPerformed

    private void btnPastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPastActionPerformed
        currentFilter = "Past";
        loadPastAppointments();
        updateFilterButtons();
    }//GEN-LAST:event_btnPastActionPerformed

    private void btnCancelledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelledActionPerformed
        currentFilter = "Cancelled";
        loadCancelledAppointments();
        updateFilterButtons();
    }//GEN-LAST:event_btnCancelledActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable appointmentsTable;
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnCancelled;
    private javax.swing.JButton btnPast;
    private javax.swing.JButton btnUpcoming;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
