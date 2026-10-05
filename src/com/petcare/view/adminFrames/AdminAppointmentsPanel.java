/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.petcare.view.adminFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class AdminAppointmentsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");

    // Action link columns
    private static final int VIEW_COLUMN = 7;
    private static final int EDIT_COLUMN = 8;
    private static final int CANCEL_COLUMN = 9;
    private static final int DELETE_COLUMN = 10;

    private AdminController controller;
    private User user;

    private List<Appointment> appointments;

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"ID", "Pet", "Owner", "Veterinarian", "Date & Time", "Reason", "Status", "View", "Edit", "Cancel", "Delete"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form AdminAppointmentsPanel
     */
    public AdminAppointmentsPanel(User user) {
        controller = new AdminController(user);
        this.user = user;

        initComponents();

        UiStyle.styleTable(appointmentsTable);
        UiStyle.styleScrollPane(scrollPane);
        initSearch();
        initTableBehaviour();

        loadAppointments();
    }

    /**
     * Filters the table as the user types.
     */
    private void initSearch() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                filterAppointments();
            }

            public void removeUpdate(DocumentEvent e) {
                filterAppointments();
            }

            public void changedUpdate(DocumentEvent e) {
                filterAppointments();
            }
        });
    }

    /**
     * Column widths, the View/Edit/Cancel/Delete links, click and hover cursor.
     */
    private void initTableBehaviour() {

        int[] widths = {45, 90, 110, 110, 150, 150, 80, 50, 50, 60, 60};
        for (int i = 0; i < widths.length; i++) {
            appointmentsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        appointmentsTable.getColumnModel().getColumn(VIEW_COLUMN).setCellRenderer(linkRenderer("View", UiStyle.ACCENT));
        appointmentsTable.getColumnModel().getColumn(EDIT_COLUMN).setCellRenderer(linkRenderer("Edit", UiStyle.ACCENT));
        appointmentsTable.getColumnModel().getColumn(CANCEL_COLUMN).setCellRenderer(linkRenderer("Cancel", new Color(217, 119, 6)));
        appointmentsTable.getColumnModel().getColumn(DELETE_COLUMN).setCellRenderer(linkRenderer("Delete", UiStyle.ERROR));

        // Disable sorting by the action links
        TableRowSorter<?> sorter = (TableRowSorter<?>) appointmentsTable.getRowSorter();
        for (int column = VIEW_COLUMN; column <= DELETE_COLUMN; column++) {
            sorter.setSortable(column, false);
        }

        appointmentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 1) {
                    return;
                }

                int row = appointmentsTable.rowAtPoint(e.getPoint());
                int column = appointmentsTable.columnAtPoint(e.getPoint());

                if (!isActionCell(row, column)) {
                    return;
                }

                if (column == VIEW_COLUMN) {
                    viewAppointment(row);
                } else if (column == EDIT_COLUMN) {
                    editAppointment(row);
                } else if (column == CANCEL_COLUMN) {
                    cancelAppointment(row);
                } else {
                    deleteAppointment(row);
                }
            }
        });

        appointmentsTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                int row = appointmentsTable.rowAtPoint(e.getPoint());
                int column = appointmentsTable.columnAtPoint(e.getPoint());

                if (isActionCell(row, column)) {
                    appointmentsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    appointmentsTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    /**
     * True for an action link that has a value (e.g. Cancel is empty unless Scheduled).
     */
    private boolean isActionCell(int row, int column) {
        return row >= 0 && column >= VIEW_COLUMN && column <= DELETE_COLUMN
                && appointmentsTable.getValueAt(row, column) != null;
    }

    private DefaultTableCellRenderer linkRenderer(String text, Color color) {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // A missing value means no action for this row
                label.setText(value == null ? "" : "<html><u>" + text + "</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    label.setForeground(color);
                }

                return label;
            }
        };
    }

    private void loadAppointments() {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getAllAppointments();

            for (Appointment appointment : appointments) {
                addAppointmentRow(appointment);
            }

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    private void addAppointmentRow(Appointment appointment) {

        String date = appointment.getAppointmentDate().toLocalDateTime().format(DATE_FORMAT);

        tableModel.addRow(new Object[] {
                appointment.getAppointmentId(),
                appointment.getPetName(),
                appointment.getOwnerName(),
                appointment.getVetName(),
                date,
                appointment.getReason(),
                appointment.getStatus(),
                "View",
                "Edit",
                "Scheduled".equals(appointment.getStatus()) ? "Cancel" : null,
                "Delete"
        });
    }

    private void filterAppointments() {

        if (appointments == null) {
            return;
        }

        String search = txtSearch.getText().trim().toLowerCase();

        tableModel.setRowCount(0);

        for (Appointment appointment : appointments) {

            String date = appointment.getAppointmentDate().toLocalDateTime().format(DATE_FORMAT);

            String data = appointment.getAppointmentId()
                    + " "
                    + appointment.getPetName()
                    + " "
                    + appointment.getOwnerName()
                    + " "
                    + appointment.getVetName()
                    + " "
                    + date
                    + " "
                    + appointment.getReason()
                    + " "
                    + appointment.getStatus();

            if (data.toLowerCase().contains(search)) {
                addAppointmentRow(appointment);
            }
        }
    }

    private void viewAppointment(int row) {

        Appointment appointment = getAppointmentFromRow(row);

        if (appointment == null) {
            return;
        }

        ViewAppointmentDialog dialog = new ViewAppointmentDialog(appointment);
        dialog.setVisible(true);
    }

    private void editAppointment(int row) {

        Appointment appointment = getAppointmentFromRow(row);

        if (appointment == null) {
            return;
        }

        EditAppointmentDialog dialog = new EditAppointmentDialog(appointment, user);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            loadAppointments();
        }
    }

    private void cancelAppointment(int row) {

        Appointment appointment = getAppointmentFromRow(row);

        if (appointment == null) {
            return;
        }

        if (!"Scheduled".equals(appointment.getStatus())) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Cancel Appointment", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.cancelAppointment(appointment);

            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.", "Appointment Cancelled", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    private void deleteAppointment(int row) {

        Appointment appointment = getAppointmentFromRow(row);

        if (appointment == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete this appointment?", "Delete Appointment", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controller.deleteAppointment(appointment.getAppointmentId());

            JOptionPane.showMessageDialog(this, "Appointment deleted successfully.", "Appointment Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this, "This appointment cannot be deleted because it has an associated medical treatment.", "Cannot Delete Appointment", JOptionPane.WARNING_MESSAGE);
            } else {
                ErrorHandler.handleSQLException(e);
            }
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    /**
     * The table can be sorted and filtered, so the row is looked up by its ID.
     */
    private Appointment getAppointmentFromRow(int row) {

        if (appointments == null) {
            return null;
        }

        int modelRow = appointmentsTable.convertRowIndexToModel(row);
        int appointmentId = (int) tableModel.getValueAt(modelRow, 0);

        for (Appointment appointment : appointments) {

            if (appointment.getAppointmentId() == appointmentId) {
                return appointment;
            }
        }

        return null;
    }

    public void refreshAppointments() {
        loadAppointments();
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
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        appointmentsTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Appointments");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Manage every appointment in the system");

        btnAdd.setBackground(new java.awt.Color(0, 121, 107));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("+ Add Appointment");
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(this::btnAddActionPerformed);

        lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(73, 80, 87));
        lblSearch.setText("Search");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(33, 37, 41));
        txtSearch.setToolTipText("Search appointments");
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        appointmentsTable.setAutoCreateRowSorter(true);
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
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblSearch)
                .addGap(10, 10, 10)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        AddAppointmentDialog dialog = new AddAppointmentDialog(user);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            loadAppointments();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable appointmentsTable;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
