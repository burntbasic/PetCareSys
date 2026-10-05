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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

/**
 *
 * @author hirunaka
 */
public class AddAppointmentDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private DatePicker datePicker;

    private AdminController controller;

    private boolean changed = false;

    /**
     * Creates new form AddAppointmentDialog
     */
    public AddAppointmentDialog(User user) {
        controller = new AdminController(user);

        initComponents();

        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);

        initRenderers();
        initDatePicker();
        loadOwners();
        loadVets();

        // Added after the vets are loaded so filling the list doesn't trigger it early
        cmbVet.addActionListener(e -> loadAvailableTimes());

        // Added after the owners are loaded, so load the first owner's pets once by hand
        cmbOwner.addActionListener(e -> loadOwnerPets());
        loadOwnerPets();
    }

    private void initRenderers() {

        cmbOwner.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof User) {
                    User owner = (User) value;
                    setText(owner.getFName() + " " + owner.getLName());
                }
                return this;
            }
        });

        cmbPet.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Pet) {
                    Pet pet = (Pet) value;
                    setText(pet.getName());
                }
                return this;
            }
        });

        cmbVet.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof User) {
                    User vet = (User) value;
                    setText(vet.getFName() + " " + vet.getLName());
                }
                return this;
            }
        });

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
        datePicker.addPropertyChangeListener("date", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                loadAvailableTimes();
            }
        });

        datePanel.setLayout(new BorderLayout());
        datePanel.add(datePicker, BorderLayout.CENTER);
    }

    private void loadOwners() {
        try {
            List<User> owners = controller.getPetOwners();

            for (User owner : owners) {
                cmbOwner.addItem(owner);
            }
        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    /**
     * Shows only the selected owner's pets.
     */
    private void loadOwnerPets() {

        cmbPet.removeAllItems();

        User selectedOwner = (User) cmbOwner.getSelectedItem();

        if (selectedOwner == null) {
            return;
        }

        try {
            List<Pet> pets = controller.getOwnerPets(selectedOwner);

            for (Pet pet : pets) {
                cmbPet.addItem(pet);
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    private void loadVets() {
        try {
            List<User> vets = controller.getVeterinarians();

            for (User vet : vets) {
                cmbVet.addItem(vet);
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    private void loadAvailableTimes() {

        cmbTime.removeAllItems();

        User selectedVet = (User) cmbVet.getSelectedItem();
        LocalDate selectedDate = datePicker.getDate();

        if (selectedVet == null || selectedDate == null) {
            return;
        }

        try {
            List<LocalTime> availableTimes = controller.getAvailableTimes(selectedVet.getId(), selectedDate);

            for (LocalTime time : availableTimes) {

                if (selectedDate.equals(LocalDate.now()) && !time.isAfter(LocalTime.now())) {
                    continue;
                }

                cmbTime.addItem(time);
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
        lblOwner = new javax.swing.JLabel();
        cmbOwner = new javax.swing.JComboBox<>();
        lblPet = new javax.swing.JLabel();
        cmbPet = new javax.swing.JComboBox<>();
        lblVet = new javax.swing.JLabel();
        cmbVet = new javax.swing.JComboBox<>();
        lblDate = new javax.swing.JLabel();
        datePanel = new javax.swing.JPanel();
        lblTime = new javax.swing.JLabel();
        cmbTime = new javax.swing.JComboBox<>();
        lblReason = new javax.swing.JLabel();
        reasonScrollPane = new javax.swing.JScrollPane();
        txtReason = new javax.swing.JTextArea();
        btnCancel = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();

        setTitle("Add Appointment");
        setModal(true);
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Add Appointment");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Schedule an appointment for any pet owner");

        lblOwner.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblOwner.setForeground(new java.awt.Color(73, 80, 87));
        lblOwner.setText("Owner");

        cmbOwner.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblPet.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblPet.setForeground(new java.awt.Color(73, 80, 87));
        lblPet.setText("Pet");

        cmbPet.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        lblVet.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblVet.setForeground(new java.awt.Color(73, 80, 87));
        lblVet.setText("Veterinarian");

        cmbVet.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

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

        reasonScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));

        txtReason.setColumns(20);
        txtReason.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtReason.setForeground(new java.awt.Color(33, 37, 41));
        txtReason.setLineWrap(true);
        txtReason.setRows(5);
        txtReason.setWrapStyleWord(true);
        txtReason.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
        reasonScrollPane.setViewportView(txtReason);

        btnCancel.setBackground(new java.awt.Color(255, 255, 255));
        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(33, 37, 41));
        btnCancel.setText("Cancel");
        btnCancel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)));
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this::btnCancelActionPerformed);

        btnAdd.setBackground(new java.awt.Color(0, 121, 107));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Add Appointment");
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(this::btnAddActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSubtitle)
                    .addComponent(lblOwner)
                    .addComponent(cmbOwner, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblPet)
                    .addComponent(cmbPet, 0, 384, Short.MAX_VALUE)
                    .addComponent(lblVet)
                    .addComponent(cmbVet, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDate)
                    .addComponent(datePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTime)
                    .addComponent(cmbTime, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblReason)
                    .addComponent(reasonScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addComponent(lblOwner)
                .addGap(4, 4, 4)
                .addComponent(cmbOwner, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblPet)
                .addGap(4, 4, 4)
                .addComponent(cmbPet, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblVet)
                .addGap(4, 4, 4)
                .addComponent(cmbVet, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblDate)
                .addGap(4, 4, 4)
                .addComponent(datePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblTime)
                .addGap(4, 4, 4)
                .addComponent(cmbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblReason)
                .addGap(4, 4, 4)
                .addComponent(reasonScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        Pet selectedPet = (Pet) cmbPet.getSelectedItem();
        User selectedVet = (User) cmbVet.getSelectedItem();
        LocalDate selectedDate = datePicker.getDate();
        LocalTime selectedTime = (LocalTime) cmbTime.getSelectedItem();
        String reason = txtReason.getText().trim();

        if (selectedPet == null) {
            JOptionPane.showMessageDialog(this, "Please select a pet.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedVet == null) {
            JOptionPane.showMessageDialog(this, "Please select a veterinarian.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedTime == null) {
            JOptionPane.showMessageDialog(this, "Please select a time.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controller.addAppointment(selectedPet, selectedVet, selectedDate, selectedTime, reason);

            changed = true;

            JOptionPane.showMessageDialog(this, "Appointment added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ErrorHandler.handleSQLException(ex);
        } catch (DatabaseConfigException ex) {
            ErrorHandler.handleDatabaseConfigException(ex);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<com.petcare.model.User> cmbOwner;
    private javax.swing.JComboBox<com.petcare.model.Pet> cmbPet;
    private javax.swing.JComboBox<java.time.LocalTime> cmbTime;
    private javax.swing.JComboBox<com.petcare.model.User> cmbVet;
    private javax.swing.JPanel datePanel;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblOwner;
    private javax.swing.JLabel lblPet;
    private javax.swing.JLabel lblReason;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVet;
    private javax.swing.JScrollPane reasonScrollPane;
    private javax.swing.JTextArea txtReason;
    // End of variables declaration//GEN-END:variables
}
