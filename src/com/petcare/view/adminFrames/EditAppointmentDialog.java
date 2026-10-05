/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarBorderProperties;
import com.petcare.controller.AdminController;
import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

/**
 *
 * @author hirunaka
 */
public class EditAppointmentDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private AdminController controller;
    private PetOwnerController pController;
    private Appointment appointment;

    private DatePicker datePicker;

    private boolean changed = false;

    /**
     * Creates new form EditAppointmentDialog
     */
    public EditAppointmentDialog(Appointment appointment, User user) {
        this.appointment = appointment;

        controller = new AdminController(user);
        pController = new PetOwnerController();

        initComponents();

        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);

        // Fill in the appointment's current details
        lblPetValue.setText(appointment.getPetName());
        lblOwnerValue.setText(appointment.getOwnerName());
        lblVetValue.setText(appointment.getVetName());
        txtReason.setText(appointment.getReason() == null ? "" : appointment.getReason());
        cmbStatus.setSelectedItem(appointment.getStatus());

        initTimeRenderer();
        initDatePicker();

        // INITIAL DATE AND TIME
        setInitialDateAndTime();

        // Added after the initial date is set, so it only reacts to the user's changes
        datePicker.addPropertyChangeListener("date", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                loadAvailableTimes();
            }
        });
    }

    private void initTimeRenderer() {
        cmbTime.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof LocalTime) {
                    LocalTime time = (LocalTime) value;
                    setText(time.format(DateTimeFormatter.ofPattern("h:mm a")));
                }
                return this;
            }
        });
    }

    private void initDatePicker() {

        DatePickerSettings dateSettings = new DatePickerSettings();

        Color background = UIManager.getColor("TextField.background");
        Color foreground = UIManager.getColor("TextField.foreground");
        Color panelBackground = UIManager.getColor("Panel.background");
        Color borderColor = UIManager.getColor("Component.borderColor");

        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, background);
        dateSettings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, background);
        dateSettings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, foreground);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, background);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, foreground);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundVetoedDates, background);
        dateSettings.setColorBackgroundWeekdayLabels(panelBackground, false);
        dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays, foreground);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, panelBackground);
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, foreground);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons, panelBackground);
        dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons, foreground);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, panelBackground);
        dateSettings.setColor(DatePickerSettings.DateArea.TextTodayLabel, foreground);
        dateSettings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, panelBackground);
        dateSettings.setColor(DatePickerSettings.DateArea.TextClearLabel, foreground);
        ArrayList<CalendarBorderProperties> borderProperties = new ArrayList<>();
        borderProperties.add(new CalendarBorderProperties(new Point(1, 1), new Point(5, 5), borderColor, 1));
        dateSettings.setBorderPropertiesList(borderProperties);
        dateSettings.setBorderCalendarPopup(BorderFactory.createLineBorder(borderColor));

        datePicker = new DatePicker(dateSettings);
        dateSettings.setDateRangeLimits(LocalDate.now(), null);
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        datePanel.setLayout(new BorderLayout());
        datePanel.add(datePicker, BorderLayout.CENTER);
    }

    private void setInitialDateAndTime() {

        LocalDateTime appointmentDateTime = appointment.getAppointmentDate().toLocalDateTime();

        LocalDate date = appointmentDateTime.toLocalDate();
        LocalTime time = appointmentDateTime.toLocalTime();
        LocalDate today = LocalDate.now();

        if (date.isBefore(today)) {
            date = today;
            time = LocalTime.now().plusMinutes(1).withSecond(0).withNano(0);
        }

        if (date.equals(today) && !time.isAfter(LocalTime.now())) {
            time = LocalTime.now().plusMinutes(1).withSecond(0).withNano(0);
        }

        datePicker.setDate(date);
        loadAvailableTimes();

        if (cmbTime.getItemCount() == 0) {
            return;
        }

        boolean found = false;

        for (int i = 0; i < cmbTime.getItemCount(); i++) {

            LocalTime availableTime = cmbTime.getItemAt(i);

            if (availableTime.equals(time)) {
                cmbTime.setSelectedIndex(i);
                found = true;
                break;
            }
        }

        if (!found) {
            cmbTime.setSelectedItem(findClosestTime(time));
        }
    }

    private void loadAvailableTimes() {

        cmbTime.removeAllItems();

        LocalDate selectedDate = datePicker.getDate();
        if (selectedDate == null) {
            return;
        }

        try {

            List<LocalTime> availableTimes = pController.getAvailableTimes(appointment.getVetId(), selectedDate);

            LocalTime currentAppointmentTime = appointment.getAppointmentDate().toLocalDateTime().toLocalTime().withSecond(0).withNano(0);

            for (LocalTime time : availableTimes) {

                LocalTime cleanTime = time.withSecond(0).withNano(0);

                if (selectedDate.equals(LocalDate.now()) && !cleanTime.isAfter(LocalTime.now())) {
                    continue;
                }

                cmbTime.addItem(cleanTime);
            }

            if (selectedDate.equals(appointment.getAppointmentDate().toLocalDateTime().toLocalDate()) && !timeAlreadyExists(currentAppointmentTime)) {
                if (!selectedDate.equals(LocalDate.now()) || currentAppointmentTime.isAfter(LocalTime.now())) {

                    cmbTime.addItem(currentAppointmentTime);
                }
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    private boolean timeAlreadyExists(LocalTime time) {

        for (int i = 0; i < cmbTime.getItemCount(); i++) {
            if (cmbTime.getItemAt(i).equals(time)) {
                return true;
            }
        }
        return false;
    }

    private LocalTime findClosestTime(LocalTime target) {

        if (cmbTime.getItemCount() == 0) {
            return null;
        }

        LocalTime closest = cmbTime.getItemAt(0);

        long closestDifference = Math.abs(closest.toSecondOfDay() - target.toSecondOfDay());

        for (int i = 1; i < cmbTime.getItemCount(); i++) {

            LocalTime time = cmbTime.getItemAt(i);

            long difference = Math.abs(time.toSecondOfDay() - target.toSecondOfDay());

            if (difference < closestDifference) {
                closest = time;
                closestDifference = difference;
            }
        }
        return closest;
    }

    private void updateAppointment() {

        LocalDate date = datePicker.getDate();
        LocalTime time = (LocalTime) cmbTime.getSelectedItem();
        String reason = txtReason.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();

        if (date == null) {
            JOptionPane.showMessageDialog(this, "Please select an appointment date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (time == null) {

            JOptionPane.showMessageDialog(this, "Please select an appointment time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        if ("Scheduled".equals(status) && dateTime.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this, "A scheduled appointment cannot be set to a past date and time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date appointmentDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        Timestamp timestamp = new Timestamp(appointmentDate.getTime());

        Appointment updatedAppointment = new Appointment(
                appointment.getAppointmentId(),
                appointment.getPetId(),
                appointment.getVetId(),
                appointment.getPetName(),
                appointment.getOwnerName(),
                appointment.getVetName(),
                timestamp,
                status,
                reason);

        try {

            boolean updated = controller.updateAppointment(updatedAppointment);

            if (updated) {
                changed = true;

                JOptionPane.showMessageDialog(this, "Appointment updated successfully.", "Appointment Updated", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    public boolean isChanged() {
        return changed;
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
        lblPetKey = new javax.swing.JLabel();
        lblPetValue = new javax.swing.JLabel();
        lblOwnerKey = new javax.swing.JLabel();
        lblOwnerValue = new javax.swing.JLabel();
        lblVetKey = new javax.swing.JLabel();
        lblVetValue = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        datePanel = new javax.swing.JPanel();
        lblTime = new javax.swing.JLabel();
        cmbTime = new javax.swing.JComboBox<>();
        lblReason = new javax.swing.JLabel();
        txtReason = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setTitle("Edit Appointment");
        setModal(true);
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Edit Appointment");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Reschedule or update this appointment");

        lblPetKey.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPetKey.setForeground(new java.awt.Color(108, 117, 125));
        lblPetKey.setText("Pet");

        lblPetValue.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPetValue.setForeground(new java.awt.Color(33, 37, 41));
        lblPetValue.setText("Pet name");

        lblOwnerKey.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblOwnerKey.setForeground(new java.awt.Color(108, 117, 125));
        lblOwnerKey.setText("Owner");

        lblOwnerValue.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblOwnerValue.setForeground(new java.awt.Color(33, 37, 41));
        lblOwnerValue.setText("Owner name");

        lblVetKey.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblVetKey.setForeground(new java.awt.Color(108, 117, 125));
        lblVetKey.setText("Veterinarian");

        lblVetValue.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblVetValue.setForeground(new java.awt.Color(33, 37, 41));
        lblVetValue.setText("Vet name");

        lblDate.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblDate.setForeground(new java.awt.Color(73, 80, 87));
        lblDate.setText("Date");

        datePanel.setOpaque(false);

        javax.swing.GroupLayout datePanelLayout = new javax.swing.GroupLayout(datePanel);
        datePanel.setLayout(datePanelLayout);
        datePanelLayout.setHorizontalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        datePanelLayout.setVerticalGroup(
            datePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        lblTime.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTime.setForeground(new java.awt.Color(73, 80, 87));
        lblTime.setText("Time");

        cmbTime.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblReason.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblReason.setForeground(new java.awt.Color(73, 80, 87));
        lblReason.setText("Reason");

        txtReason.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtReason.setForeground(new java.awt.Color(33, 37, 41));
        txtReason.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(73, 80, 87));
        lblStatus.setText("Status");

        cmbStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Scheduled", "Completed", "Cancelled" }));

        btnCancel.setBackground(new java.awt.Color(255, 255, 255));
        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(33, 37, 41));
        btnCancel.setText("Cancel");
        btnCancel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this::btnCancelActionPerformed);

        btnSave.setBackground(new java.awt.Color(0, 121, 107));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save Changes");
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(this::btnSaveActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSubtitle)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPetKey, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblOwnerKey)
                            .addComponent(lblVetKey))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPetValue, 0, 264, Short.MAX_VALUE)
                            .addComponent(lblOwnerValue, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblVetValue, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDate)
                            .addComponent(datePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTime)
                            .addComponent(cmbTime, 0, 180, Short.MAX_VALUE)))
                    .addComponent(lblReason)
                    .addComponent(txtReason, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblStatus)
                    .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitle)
                .addGap(2, 2, 2)
                .addComponent(lblSubtitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPetKey)
                    .addComponent(lblPetValue))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOwnerKey)
                    .addComponent(lblOwnerValue))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVetKey)
                    .addComponent(lblVetValue))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDate)
                    .addComponent(lblTime))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(lblReason)
                .addGap(4, 4, 4)
                .addComponent(txtReason, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblStatus)
                .addGap(4, 4, 4)
                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        updateAppointment();
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<java.time.LocalTime> cmbTime;
    private javax.swing.JPanel datePanel;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblOwnerKey;
    private javax.swing.JLabel lblOwnerValue;
    private javax.swing.JLabel lblPetKey;
    private javax.swing.JLabel lblPetValue;
    private javax.swing.JLabel lblReason;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVetKey;
    private javax.swing.JLabel lblVetValue;
    private javax.swing.JTextField txtReason;
    // End of variables declaration//GEN-END:variables
}
