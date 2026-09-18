package com.petcare.ui;

import com.petcare.database.UserDB;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.util.ErrorHandler;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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




public class RegisterFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFName;
	private JTextField txtLName;
	private JTextField txtEmail;
	private JTextField txtPhone;
	private JLabel lblNewLabel_3;
	private JTextField txtUsername;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JPasswordField pwField;
	private JLabel lblNewLabel_6;
	private JPasswordField confirmPwField;
	private JLabel lblBacktoLogin;
	
	private UserDB userdb;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterFrame frame = new RegisterFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RegisterFrame() {
		
		userdb = new UserDB();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 527, 764);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("First Name");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel.setBounds(76, 32, 167, 41);
		contentPane.add(lblNewLabel);
		
		txtFName = new JTextField();
		txtFName.setColumns(10);
		txtFName.setBounds(76, 69, 167, 32);
		contentPane.add(txtFName);
		
		JLabel lblNewLabel_1 = new JLabel("Last name");
		lblNewLabel_1.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_1.setBounds(279, 32, 167, 41);
		contentPane.add(lblNewLabel_1);
		
		txtLName = new JTextField();
		txtLName.setColumns(10);
		txtLName.setBounds(279, 69, 167, 32);
		contentPane.add(txtLName);
		
		JLabel lblNewLabel_2 = new JLabel("Email");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_2.setBounds(76, 113, 167, 41);
		contentPane.add(lblNewLabel_2);
		
		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(76, 148, 370, 32);
		contentPane.add(txtEmail);
		
		JLabel lblNewLabel_2_1 = new JLabel("+94");
		lblNewLabel_2_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewLabel_2_1.setBounds(76, 227, 46, 32);
		contentPane.add(lblNewLabel_2_1);
		
		txtPhone = new JTextField();
		txtPhone.setColumns(10);
		txtPhone.setBounds(102, 228, 344, 32);
		contentPane.add(txtPhone);
		
		lblNewLabel_3 = new JLabel("Phone no.");
		lblNewLabel_3.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_3.setBounds(76, 190, 167, 41);
		contentPane.add(lblNewLabel_3);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBounds(136, 345, 263, 32);
		contentPane.add(txtUsername);
		
		lblNewLabel_4 = new JLabel("Username");
		lblNewLabel_4.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_4.setBounds(136, 308, 263, 41);
		contentPane.add(lblNewLabel_4);
		
		lblNewLabel_5 = new JLabel("Password");
		lblNewLabel_5.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_5.setBounds(136, 389, 263, 41);
		contentPane.add(lblNewLabel_5);
		
		pwField = new JPasswordField();
		pwField.setBounds(136, 426, 263, 32);
		contentPane.add(pwField);
		
		lblNewLabel_6 = new JLabel("Confirm Password");
		lblNewLabel_6.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_6.setBounds(136, 470, 263, 41);
		contentPane.add(lblNewLabel_6);
		
		confirmPwField = new JPasswordField();
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
				//Implementation: Details to be captured and passed to UserDB
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
				
				//Validation
				boolean valid = true;
				
		        if (fName.isEmpty()) {
		            fNameError.setText("First name is required.");
		            fNameError.setVisible(true);
		            valid = false;
		        } else if (!fName.matches("[a-zA-Z]+")) {
		            fNameError.setText("First name must contain letters only.");
		            fNameError.setVisible(true);
		            valid = false;
		        }

		        if (lName.isEmpty()) {
		        	lNameError.setText("Last name is required.");
		            lNameError.setVisible(true);
		        	valid = false;
		        } else if (!lName.matches("[a-zA-Z]+")) {
		            lNameError.setText("Last name must contain letters only.");
		            lNameError.setVisible(true);
		            valid = false;
		        }
		        
		        if (email.isEmpty()) {
		            emailError.setText("Email is required.");
		            emailError.setVisible(true);
		            valid = false;
		        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
		            emailError.setText("Enter a valid email address.");
		            emailError.setVisible(true);
		            valid = false;
		        }
		        
		        if (phone.isEmpty()) {
		            phoneError.setText("Phone number is required.");
		            phoneError.setVisible(true);
		            valid = false;
		        } else if (phone.length() > 9) {
		            phoneError.setText("Maximum 9 digits allowed.");
		            phoneError.setVisible(true);
		            valid = false;
		        } else if (!phone.matches("7[0-9]{8}")) {
		            phoneError.setText("Enter a valid number (7XXXXXXXX).");
		            phoneError.setVisible(true);
		            valid = false;
		        }
		        
		        if (username.isEmpty()) {
		            usernameError.setText("Username is required.");
		            usernameError.setVisible(true);
		            valid = false;
		        }
		        
		        if (password.isEmpty()) {
		            pwError.setText("Password is required.");
		            pwError.setVisible(true);
		            valid = false;
		        }
		        
		        if (confirmPassword.isEmpty()) {
		            confirmPwError.setText("Please confirm your password.");
		            confirmPwError.setVisible(true);
		            valid = false;
		        } else if (!password.equals(confirmPassword)) {
		            confirmPwError.setText("Passwords do not match.");
		            confirmPwError.setVisible(true);
		            valid = false;
		        }

		        // Stop execution if invalid prior to database validation
		        if (!valid) {
		            return;
		        }
		        
		        //Formatting phone number
		        phone = "+94" + phone;
		        
		        //DB Validation
		        try {
		        	if(userdb.usernameExists(username)) {
		        		usernameError.setText("Username already exists");
			        	usernameError.setVisible(true);
			        	valid = false;
		        	}
		        
		        	if(userdb.emailExists(email)) {
		        		emailError.setText("Email already exists");
		        		emailError.setVisible(true);
		        		valid = false;
		        	}
		        
		        	if (!valid) {
		        		return;
		        	}
				
		        	boolean success = userdb.petOwnerRegister(fName, lName, email, phone, username, password);
				
		        	if (success) {
		        		JOptionPane.showMessageDialog(null, "Registration Successful");
		        		RegisterFrame.this.dispose();
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
		contentPane.add(btnSubmit);
		
		lblBacktoLogin = new JLabel("Back to Login");
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
				RegisterFrame.this.dispose();
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
