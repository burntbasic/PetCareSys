package com.petcare.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BaseDashboard extends JFrame{
	
    private static final long serialVersionUID = 1L;
    
	protected final Color cardColor = new Color(42, 44, 54);
	protected final Color sidebarColor = new Color(30, 32, 40);
    
    protected JPanel sidebar;
    protected JPanel mainPanel;
    protected JPanel contentPanel;
    
    //Helper methods
    protected JButton createMenuButton(String text) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    protected JPanel createCard(String title, String value) {

        JPanel card = new JPanel();
        card.setBackground(cardColor);
        card.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }
    
    public BaseDashboard() {
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        getContentPane().setLayout(new BorderLayout());

        // =========================
        // SIDEBAR
        // =========================

        sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("PetCareSys");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        logo.setAlignmentX(LEFT_ALIGNMENT);

        sidebar.add(logo);

        // =========================
        // MAIN CONTENT
        // =========================

        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        contentPanel = new JPanel(new CardLayout());

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
}