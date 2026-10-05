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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class VetAppointmentsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final String TIME_ONLY = "h:mm a";
    private static final String DATE_AND_TIME = "dd MMM yyyy, h:mm a";

    private VetController controller;
    private List<Appointment> appointments;
    private User user;

    private String currentFilter = "Today";

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Date & Time", "Pet", "Owner", "Reason", "Status", "Actions"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form VetAppointmentsPanel
     */
    public VetAppointmentsPanel(User user) {
        this.user = user;
        controller = new VetController();

        initComponents();

        UiStyle.styleTable(appointmentsTable);
        UiStyle.styleScrollPane(scrollPane);
        initTableBehaviour();

        // LOAD DEFAULT FILTER
        loadTodayAppointments();
        updateFilterButtons();
    }

    /**
     * "View" link column: renderer, click and hover cursor.
     */
    private void initTableBehaviour() {
        appointmentsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {

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

                if (row >= 0 && column == 5) {
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

                if (row >= 0 && column == 5 && appointments != null && row < appointments.size()) {

                    Appointment appointment = appointments.get(row);

                    VetAppointmentDialog dialog = new VetAppointmentDialog(user, appointment);
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
        styleFilterButton(btnToday, "Today".equals(currentFilter));
        styleFilterButton(btnUpcoming, "Upcoming".equals(currentFilter));
        styleFilterButton(btnPast, "Past".equals(currentFilter));
        styleFilterButton(btnCancelled, "Cancelled".equals(currentFilter));
    }

    private void styleFilterButton(JButton button, boolean active) {
        button.setBackground(active ? UiStyle.ACCENT : Color.WHITE);
        button.setForeground(active ? Color.WHITE : UiStyle.TEXT_DARK);
        button.setBorder(BorderFactory.createLineBorder(active ? UiStyle.ACCENT : new Color(206, 212, 218)));
    }

    // REFRESH

    private void refreshAppointments() {

        if ("Today".equals(currentFilter)) {
            loadTodayAppointments();
        } else if ("Upcoming".equals(currentFilter)) {
            loadUpcomingAppointments();
        } else if ("Past".equals(currentFilter)) {
            loadPastAppointments();
        } else if ("Cancelled".equals(currentFilter)) {
            loadCancelledAppointments();
        }
    }

    // TODAY

    private void loadTodayAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetTodays(user);
            fillTable(TIME_ONLY);

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    // UPCOMING

    private void loadUpcomingAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetUpcoming(user);
            fillTable(DATE_AND_TIME);

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    // PAST

    private void loadPastAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetPast(user);
            fillTable(DATE_AND_TIME);

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    // CANCELLED

    private void loadCancelledAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetCancelled(user);
            fillTable(DATE_AND_TIME);

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    /**
     * Adds a table row for every loaded appointment, using the given time format.
     */
    private void fillTable(String timePattern) {
        for (Appointment appointment : appointments) {

            String time = appointment.getAppointmentDate().toLocalDateTime().format(
                    DateTimeFormatter.ofPattern(timePattern));

            tableModel.addRow(new Object[] {
                    time,
                    appointment.getPetName(),
                    appointment.getOwnerName(),
                    appointment.getReason(),
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
        btnToday = new javax.swing.JButton();
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

        btnToday.setBackground(new java.awt.Color(0, 121, 107));
        btnToday.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnToday.setForeground(new java.awt.Color(255, 255, 255));
        btnToday.setText("Today");
        btnToday.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 121, 107)));
        btnToday.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnToday.setFocusPainted(false);
        btnToday.addActionListener(this::btnTodayActionPerformed);

        btnUpcoming.setBackground(new java.awt.Color(255, 255, 255));
        btnUpcoming.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUpcoming.setForeground(new java.awt.Color(33, 37, 41));
        btnUpcoming.setText("Upcoming");
        btnUpcoming.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));
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
            .addComponent(lblTitle)
            .addComponent(lblSubtitle)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnToday, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
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
                .addComponent(lblTitle)
                .addGap(4, 4, 4)
                .addComponent(lblSubtitle)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnToday, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpcoming, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPast, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelled, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodayActionPerformed
        currentFilter = "Today";
        loadTodayAppointments();
        updateFilterButtons();
    }//GEN-LAST:event_btnTodayActionPerformed

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
    private javax.swing.JButton btnCancelled;
    private javax.swing.JButton btnPast;
    private javax.swing.JButton btnToday;
    private javax.swing.JButton btnUpcoming;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
