package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class AddPetDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private AdminController controller;

    private JComboBox<User> ownerComboBox;
    private JTextField nameField;
    private JComboBox<String> speciesComboBox;
    private JComboBox<String> genderComboBox;

    private boolean changed = false;

    public AddPetDialog(User user) {

        controller = new AdminController(user);

        setTitle("Add Pet");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setModal(true);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // OWNER

        JLabel ownerLabel = new JLabel("Owner:");

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(ownerLabel, gbc);

        ownerComboBox = new JComboBox<>();
        ownerComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof User) {
                    User owner = (User) value;
                    setText(owner.getFName() + " " + owner.getLName());
                }
                return this;
            }
        });

        try {
            List<User> owners = controller.getPetOwners();

            for (User owner : owners) {
                ownerComboBox.addItem(owner);
            }

        } catch (SQLException e) {
            ErrorHandler.handleSQLException(e);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(ownerComboBox, gbc);

        // PET NAME

        JLabel nameLabel = new JLabel("Pet Name:");

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);

        gbc.gridx = 1;

        formPanel.add(nameField, gbc);

        // SPECIES

        JLabel speciesLabel = new JLabel("Species:");

        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(speciesLabel, gbc);

        String[] species = {"Dog", "Cat", "Bird", "Rabbit", "Other"};

        speciesComboBox = new JComboBox<>(species);

        gbc.gridx = 1;

        formPanel.add(speciesComboBox, gbc);

        // GENDER

        JLabel genderLabel = new JLabel("Gender:");

        gbc.gridx = 0;
        gbc.gridy = 3;

        formPanel.add(genderLabel, gbc);

        String[] genders = {"Male", "Female"};

        genderComboBox = new JComboBox<>(genders);

        gbc.gridx = 1;

        formPanel.add(genderComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // BUTTONS

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add Pet");

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // CANCEL

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // ADD PET

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	User selectedOwner = (User) ownerComboBox.getSelectedItem();
                String name = nameField.getText().trim();
                String species = (String) speciesComboBox.getSelectedItem();
                String gender = (String) genderComboBox.getSelectedItem();

                if (selectedOwner == null) {
                    JOptionPane.showMessageDialog(AddPetDialog.this, "Please select a pet owner.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (name.isEmpty()) {

                    JOptionPane.showMessageDialog(AddPetDialog.this, "Pet name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    controller.addPet(selectedOwner, name, species, gender);
                    
                    changed = true;

                    JOptionPane.showMessageDialog(AddPetDialog.this, "Pet added successfully.", "Pet Added", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                } catch (SQLException ex) {
                    ErrorHandler.handleSQLException(ex);
                } catch (DatabaseConfigException ex) {
                    ErrorHandler.handleDatabaseConfigException(ex);
                }
            }
        });
    }

    public boolean isChanged() {

        return changed;
    }
}
