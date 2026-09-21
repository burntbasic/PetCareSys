package com.petcare.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

import com.petcare.controller.LoginController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.factory.DashboardFactory;
import com.petcare.model.*;
import com.petcare.util.ErrorHandler;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField pwField;

	//Class-level variable declared for availability throughout the entire class (not just the constructor)
	private LoginController controller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
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
	public LoginFrame() {

		controller = new LoginController();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 503);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel.setBounds(114, 62, 167, 41);
		contentPane.add(lblNewLabel);

		txtUsername = new JTextField();
		txtUsername.setBounds(114, 99, 335, 32);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPassword.setBounds(114, 153, 167, 41);
		contentPane.add(lblPassword);

		JLabel uname_pwError = new JLabel("Username or Password is incorrect.");
		uname_pwError.setHorizontalAlignment(SwingConstants.CENTER);
		uname_pwError.setBounds(114, 229, 335, 17);
		uname_pwError.setVisible(false);
		uname_pwError.setForeground(Color.RED);
		uname_pwError.setFont(new Font("Dialog", Font.PLAIN, 10));
		contentPane.add(uname_pwError);

		JButton btnLogin = new JButton("Login");
		btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLogin.setEnabled(false);
		btnLogin.setFont(new Font("Dialog", Font.BOLD, 14));
		btnLogin.setBounds(215, 285, 131, 41);
		contentPane.add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = txtUsername.getText().trim();
				String password = new String(pwField.getPassword());

				//Clearing any previous error message
				uname_pwError.setVisible(false);

				try {
					User user = controller.loginValidation(username, password);

					if (user!=null) {
						JFrame dashboard = DashboardFactory.createDashboard(user);
						dispose();
						dashboard.setVisible(true);
					} else  {
						uname_pwError.setVisible(true);
					}
				} catch (SQLException ex) {
					ErrorHandler.handleSQLException(ex);
				} catch (DatabaseConfigException ex) {
					ErrorHandler.handleDatabaseConfigException(ex);
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Application Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		pwField = new JPasswordField();
		pwField.setBounds(114, 193, 335, 32);
		contentPane.add(pwField);
		pwField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLogin.doClick();
			}

		});

		DocumentListener listener = new DocumentListener() {

			private void checkFields() {
				boolean filled = !txtUsername.getText().trim().isEmpty() && pwField.getPassword().length > 0;

				btnLogin.setEnabled(filled);
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
		
		txtUsername.getDocument().addDocumentListener(listener);
		pwField.getDocument().addDocumentListener(listener);
		
	
		JLabel lblNewUser = new JLabel("New user? Register Now!");
		lblNewUser.setForeground(new Color(0, 102, 204));
		lblNewUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblNewUser.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				lblNewUser.setText("<html><u>New user? Register Now!</u></html>");
			}

			public void mouseExited(MouseEvent e) {
				lblNewUser.setText("New user? Register Now!");

			}

			public void mouseClicked(MouseEvent e) {
				dispose();
				new RegisterFrame().setVisible(true);
			}
		});
		lblNewUser.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewUser.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewUser.setBounds(198, 343, 167, 17);
		contentPane.add(lblNewUser);

	}
}
