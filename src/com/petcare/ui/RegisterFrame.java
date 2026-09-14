package com.petcare.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFName;
	private JTextField txtLName;
	private JTextField txtEmail;
	private JTextField txtPhone;
	private JLabel lblNewLabel_3;
	private JTextField textField_4;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	private JPasswordField pwField;
	private JLabel lblNewLabel_6;
	private JPasswordField confirmPwField;
	private JLabel lblBacktoLogin;

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 556, 764);
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
		lblNewLabel_2.setBounds(76, 111, 167, 41);
		contentPane.add(lblNewLabel_2);
		
		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(76, 148, 370, 32);
		contentPane.add(txtEmail);
		
		txtPhone = new JTextField();
		txtPhone.setColumns(10);
		txtPhone.setBounds(143, 227, 303, 32);
		contentPane.add(txtPhone);
		
		lblNewLabel_3 = new JLabel("Phone no.");
		lblNewLabel_3.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel_3.setBounds(76, 190, 167, 41);
		contentPane.add(lblNewLabel_3);
		
		JComboBox comboPhoneCountry = new JComboBox();
		comboPhoneCountry.setBounds(76, 229, 62, 26);
		contentPane.add(comboPhoneCountry);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(136, 345, 263, 32);
		contentPane.add(textField_4);
		
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
		
		JButton btnSubmit = new JButton("Submit");
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
