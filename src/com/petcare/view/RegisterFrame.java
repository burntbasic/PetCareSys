package com.petcare.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.petcare.controller.RegisterController;
import com.petcare.controller.RegisterFieldEnum;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.util.ErrorHandler;

public class RegisterFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private RegisterController controller;

	//Helper method
	private boolean showValidationError(RegisterFieldEnum field, String value, JLabel errorLabel) {

		String error = controller.validateField(field, value);

		if (error != null) {
			errorLabel.setText(error);
			errorLabel.setVisible(true);
			return false;
		}

		return true;
	}

	public RegisterFrame() {

		controller = new RegisterController();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 527, 764);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("First Name");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel.setBounds(76, 32, 167, 41);
		contentPane.add(lblNewLabel);

		JTextField txtFName = new JTextField();
		txtFName.setColumns(10);
		txtFName.setBounds(76, 69, 167, 32);
		contentPane.add(txtFName);

		JLabel lblNewLabel_1 = new JLabel("Last name");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_1.setBounds(279, 32, 167, 41);
		contentPane.add(lblNewLabel_1);

		JTextField txtLName = new JTextField();
		txtLName.setColumns(10);
		txtLName.setBounds(279, 69, 167, 32);
		contentPane.add(txtLName);

		JLabel lblNewLabel_2 = new JLabel("Email");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_2.setBounds(76, 113, 167, 41);
		contentPane.add(lblNewLabel_2);

		JTextField txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(76, 148, 370, 32);
		contentPane.add(txtEmail);

		JLabel lblNewLabel_2_1 = new JLabel("+94");
		lblNewLabel_2_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewLabel_2_1.setBounds(76, 227, 46, 32);
		contentPane.add(lblNewLabel_2_1);

		JTextField txtPhone = new JTextField();
		txtPhone.setColumns(10);
		txtPhone.setBounds(102, 228, 344, 32);
		contentPane.add(txtPhone);

		JLabel lblNewLabel_3 = new JLabel("Phone no.");
		lblNewLabel_3.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_3.setBounds(76, 190, 167, 41);
		contentPane.add(lblNewLabel_3);

		JTextField txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBounds(136, 345, 263, 32);
		contentPane.add(txtUsername);

		JLabel lblNewLabel_4 = new JLabel("Username");
		lblNewLabel_4.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_4.setBounds(136, 308, 263, 41);
		contentPane.add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("Password");
		lblNewLabel_5.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_5.setBounds(136, 389, 263, 41);
		contentPane.add(lblNewLabel_5);

		JPasswordField pwField = new JPasswordField();
		pwField.setBounds(136, 426, 263, 32);
		contentPane.add(pwField);

		JLabel lblNewLabel_6 = new JLabel("Confirm Password");
		lblNewLabel_6.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_6.setBounds(136, 470, 263, 41);
		contentPane.add(lblNewLabel_6);

		JPasswordField confirmPwField = new JPasswordField();
		confirmPwField.setBounds(136, 507, 263, 32);
		contentPane.add(confirmPwField);

		JLabel fNameError = new JLabel("New label");
		fNameError.setVisible(false);
		fNameError.setForeground(Color.RED);
		fNameError.setFont(new Font("Dialog", Font.PLAIN, 10));
		fNameError.setBounds(76, 100, 185, 17);
		contentPane.add(fNameError);

		JLabel lNameError = new JLabel("New label");
		lNameError.setVisible(false);
		lNameError.setForeground(Color.RED);
		lNameError.setFont(new Font("Dialog", Font.PLAIN, 10));
		lNameError.setBounds(279, 99, 208, 17);
		contentPane.add(lNameError);

		JLabel emailError = new JLabel("New label");
		emailError.setVisible(false);
		emailError.setForeground(Color.RED);
		emailError.setFont(new Font("Dialog", Font.PLAIN, 10));
		emailError.setBounds(76, 179, 370, 17);
		contentPane.add(emailError);

		JLabel phoneError = new JLabel("New label");
		phoneError.setVisible(false);
		phoneError.setForeground(Color.RED);
		phoneError.setFont(new Font("Dialog", Font.PLAIN, 10));
		phoneError.setBounds(102, 262, 344, 17);
		contentPane.add(phoneError);

		JLabel usernameError = new JLabel("New label");
		usernameError.setVisible(false);
		usernameError.setForeground(Color.RED);
		usernameError.setFont(new Font("Dialog", Font.PLAIN, 10));
		usernameError.setBounds(136, 377, 263, 17);
		contentPane.add(usernameError);

		JLabel pwError = new JLabel("New label");
		pwError.setVisible(false);
		pwError.setForeground(Color.RED);
		pwError.setFont(new Font("Dialog", Font.PLAIN, 10));
		pwError.setBounds(136, 458, 263, 17);
		contentPane.add(pwError);

		JLabel confirmPwError = new JLabel("New label");
		confirmPwError.setVisible(false);
		confirmPwError.setForeground(Color.RED);
		confirmPwError.setFont(new Font("Dialog", Font.PLAIN, 10));
		confirmPwError.setBounds(136, 540, 263, 17);
		contentPane.add(confirmPwError);

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fName = txtFName.getText().trim();
				String lName = txtLName.getText().trim();
				String email = txtEmail.getText().trim();
				String phone = txtPhone.getText().trim();
				String username = txtUsername.getText().trim();

				String password = new String(pwField.getPassword());
				String confirmPassword = new String(confirmPwField.getPassword());

				//Clearing any previous error message
				fNameError.setVisible(false);
				lNameError.setVisible(false);
				emailError.setVisible(false);
				phoneError.setVisible(false);
				usernameError.setVisible(false);
				pwError.setVisible(false);
				confirmPwError.setVisible(false);

				// Validate individual fields
				boolean valid = true;

				valid = showValidationError(RegisterFieldEnum.FIRST_NAME, fName, fNameError) && valid;
				valid = showValidationError(RegisterFieldEnum.LAST_NAME, lName, lNameError) && valid;
				valid = showValidationError(RegisterFieldEnum.EMAIL, email, emailError) && valid;
				valid = showValidationError(RegisterFieldEnum.PHONE, phone, phoneError) && valid;
				valid = showValidationError(RegisterFieldEnum.USERNAME, username, usernameError) && valid;
				valid = showValidationError(RegisterFieldEnum.PASSWORD, password, pwError) && valid;

				// Validate confirm password
				String confirmError = controller.validateConfirmPassword(password, confirmPassword);

				if (confirmError != null) {
					confirmPwError.setText(confirmError);
					confirmPwError.setVisible(true);
					valid = false;
				}

				// Stop execution if invalid prior to database validation
				if (!valid) {
					return;
				}

				//DB Validation
				try {
					if(controller.usernameExists(username)) {
						usernameError.setText("Username already exists");
						usernameError.setVisible(true);
						valid = false;
					}

					if(controller.emailExists(email)) {
						emailError.setText("Email already exists");
						emailError.setVisible(true);
						valid = false;
					}

					if (!valid) {
						return;
					}

					boolean success = controller.petOwnerRegister(fName, lName, email, phone, username, password);

					if (success) {
						JOptionPane.showMessageDialog(null, "Registration Successful");
						dispose();
						new LoginFrame().setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(null, "Registration Failed", "Registration Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException ex) {
					ErrorHandler.handleSQLException(ex);
				} catch (DatabaseConfigException ex) {
					ErrorHandler.handleDatabaseConfigException(ex);
				}
			}});
		btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSubmit.setFont(new Font("Dialog", Font.BOLD, 14));
		btnSubmit.setBounds(195, 564, 131, 41);
		btnSubmit.setEnabled(false);
		contentPane.add(btnSubmit);

		confirmPwField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSubmit.doClick();
			}
		});

		DocumentListener listener = new DocumentListener() {

			private void checkFields() {

				boolean filled =
						!txtFName.getText().trim().isEmpty()
						&& !txtLName.getText().trim().isEmpty()
						&& !txtEmail.getText().trim().isEmpty()
						&& !txtPhone.getText().trim().isEmpty()
						&& !txtUsername.getText().trim().isEmpty()
						&& pwField.getPassword().length > 0
						&& confirmPwField.getPassword().length > 0;

						btnSubmit.setEnabled(filled);
			}

			public void insertUpdate(DocumentEvent e) {
				checkFields();
			}

			public void removeUpdate(DocumentEvent e) {
				checkFields();
			}

			public void changedUpdate(DocumentEvent e) {
				checkFields();
			}
		};

		txtFName.getDocument().addDocumentListener(listener);
		txtLName.getDocument().addDocumentListener(listener);
		txtEmail.getDocument().addDocumentListener(listener);
		txtPhone.getDocument().addDocumentListener(listener);
		txtUsername.getDocument().addDocumentListener(listener);
		pwField.getDocument().addDocumentListener(listener);
		confirmPwField.getDocument().addDocumentListener(listener);

		JLabel lblBacktoLogin = new JLabel("Back to Login");
		lblBacktoLogin.setForeground(new Color(0, 102, 204));
		lblBacktoLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblBacktoLogin.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				lblBacktoLogin.setText("<html><u>Back to Login</u></html>");
			}

			public void mouseExited(MouseEvent e) {
				lblBacktoLogin.setText("Back to Login");

			}

			public void mouseClicked(MouseEvent e) {
				dispose();
				new LoginFrame().setVisible(true);
			}
		});
		lblBacktoLogin.setVerticalAlignment(SwingConstants.BOTTOM);
		lblBacktoLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblBacktoLogin.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblBacktoLogin.setBounds(175, 617, 167, 17);
		contentPane.add(lblBacktoLogin);
	}
}
