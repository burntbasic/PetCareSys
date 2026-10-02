package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.petcare.controller.AdminController;
import com.petcare.controller.RegisterController;
import com.petcare.controller.RegisterFieldEnum;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class AddUserDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private AdminController controller;
	private RegisterController registerController;

	private JTextField fNameField;
	private JTextField lNameField;
	private JTextField emailField;
	private JTextField phoneField;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JComboBox<String> roleComboBox;

	private boolean changed = false;

	public AddUserDialog(User user) {

		controller = new AdminController(user);
		registerController = new RegisterController();

		setTitle("Add User");
		setSize(400, 470);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel formPanel = new JPanel(new GridBagLayout());

		formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel titleLabel = new JLabel("Add User");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		formPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		JLabel fNameLabel = new JLabel("First Name");
		fNameField = new JTextField(15);

		JLabel lNameLabel = new JLabel("Last Name");
		lNameField = new JTextField(15);

		JLabel emailLabel = new JLabel("Email");
		emailField = new JTextField(15);

		JLabel phoneLabel = new JLabel("Phone");
		phoneField = new JTextField(15);

		JLabel usernameLabel = new JLabel("Username");
		usernameField = new JTextField(15);

		JLabel passwordLabel = new JLabel("Password");
		passwordField = new JPasswordField(15);

		JLabel confirmPasswordLabel = new JLabel("Confirm Password");
		confirmPasswordField = new JPasswordField(15);

		JLabel roleLabel = new JLabel("Role");

		String[] roles = {"PET_OWNER", "VET", "ADMIN"};

		roleComboBox = new JComboBox<>(roles);

		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(fNameLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(fNameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(lNameLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(lNameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(emailLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(emailField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		formPanel.add(phoneLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(phoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		formPanel.add(usernameLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(usernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		formPanel.add(passwordLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(passwordField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 7;
		formPanel.add(confirmPasswordLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(confirmPasswordField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 8;
		formPanel.add(roleLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(roleComboBox, gbc);

		add(formPanel, BorderLayout.CENTER);

		// BUTTONS

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JButton addButton = new JButton("Add User");
		addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);

		add(buttonPanel, BorderLayout.SOUTH);

		// CANCEL

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// ADD USER

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fName = fNameField.getText().trim();
				String lName = lNameField.getText().trim();
				String email = emailField.getText().trim();
				String phone = phoneField.getText().trim();
				String username = usernameField.getText().trim();
				String password = new String(passwordField.getPassword());
				String confirmPassword = new String(confirmPasswordField.getPassword());
				String role = (String) roleComboBox.getSelectedItem();
				String error;

				error = registerController.validateField(RegisterFieldEnum.FIRST_NAME, fName);
				if (error != null) {
					showValidationError(error);
					return;
				}

				error = registerController.validateField(RegisterFieldEnum.LAST_NAME, lName);
				if (error != null) {
					showValidationError(error);
					return;
				}

				error = registerController.validateField(RegisterFieldEnum.EMAIL, email);
				if (error != null) {
					showValidationError(error);
					return;
				}

				error = registerController.validateField(RegisterFieldEnum.PHONE, phone);
				if (error != null) {
					showValidationError(error);
					return;
				}

				error = registerController.validateField(RegisterFieldEnum.USERNAME, username);
				if (error != null) {
					showValidationError(error);
					return;
				}

				error = registerController.validateField(RegisterFieldEnum.PASSWORD, password);
				if (error != null) {
					showValidationError(error);
					return;
				}

				error = registerController.validateConfirmPassword(password, confirmPassword);
				if (error != null) {
					showValidationError(error);
					return;
				}

				try {
					if (controller.usernameExists(username)) {
						JOptionPane.showMessageDialog(AddUserDialog.this, "Username already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (controller.emailExists(email)) {
						JOptionPane.showMessageDialog(AddUserDialog.this, "Email already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					boolean added = controller.addUser(fName, lName, email, phone, username, password, role);

					if (added) {

						changed = true;

						JOptionPane.showMessageDialog(AddUserDialog.this, "User added successfully.", "User Added", JOptionPane.INFORMATION_MESSAGE);
						dispose();
					}

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

	private void showValidationError(String message) {
		JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
	}
}
