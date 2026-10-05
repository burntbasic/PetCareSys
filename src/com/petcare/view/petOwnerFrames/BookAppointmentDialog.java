/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.petcare.view.petOwnerFrames;

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
import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

/**
 *
 * @author hirunaka
 */
public class BookAppointmentDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    // The date picker is a library component, so it is created in code and
    // placed inside the form's "datePanel"
    private DatePicker datePicker;

    private PetOwnerController controller;
    private User user;

    private boolean changed = false;

    /**
     * Creates new form BookAppointmentDialog
     */
    public BookAppointmentDialog(User user) {
        this.user = user;
        controller = new PetOwnerController();

        initComponents();

        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);

        initRenderers();
        initDatePicker();
        loadPets();
        loadVets();

        // Added after the vets are loaded so filling the list doesn't trigger it early
        cmbVet.addActionListener(e -> loadAvailableTimes());
    }

    private void initRenderers() {

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

    private void loadPets() {
        try {
            List<Pet> pets = controller.getOwnerPets(user);

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
        btnBook = new javax.swing.JButton();

        setTitle("Book Appointment");
        setModal(true);
        setResizable(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("Book Appointment");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Choose a pet, veterinarian and time");

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

        btnBook.setBackground(new java.awt.Color(0, 121, 107));
        btnBook.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBook.setForeground(new java.awt.Color(255, 255, 255));
        btnBook.setText("Book Appointment");
        btnBook.setBorderPainted(false);
        btnBook.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBook.setFocusPainted(false);
        btnBook.addActionListener(this::btnBookActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSubtitle)
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
                        .addComponent(btnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(btnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        Pet selectedPet = (Pet) cmbPet.getSelectedItem();
        User selectedVet = (User) cmbVet.getSelectedItem();
        LocalDate selectedDate = datePicker.getDate();
        LocalTime selectedTime = (LocalTime) cmbTime.getSelectedItem();
        String reason = txtReason.getText();

        try {
            controller.bookAppointment(user, selectedPet, selectedVet, selectedDate, selectedTime, reason);

            changed = true;

            JOptionPane.showMessageDialog(this, "Appointment booked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ErrorHandler.handleSQLException(ex);
        } catch (DatabaseConfigException ex) {
            ErrorHandler.handleDatabaseConfigException(ex);
        }
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<com.petcare.model.Pet> cmbPet;
    private javax.swing.JComboBox<java.time.LocalTime> cmbTime;
    private javax.swing.JComboBox<com.petcare.model.User> cmbVet;
    private javax.swing.JPanel datePanel;
    private javax.swing.JLabel lblDate;
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
