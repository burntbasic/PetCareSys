/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.petcare.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Shared shell for every role dashboard: sidebar, logout, content area and
 * styling helpers. Subclasses add menu items and pages.
 *
 * @author hirunaka
 */
public class BaseDashboard extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color MENU_HOVER = new Color(0, 96, 86);

    private final Map<String, JButton> menuButtons = new HashMap<>();
    private JButton activeButton;

    /**
     * Creates new form BaseDashboard
     */
    public BaseDashboard() {
        initComponents();
        setLocationRelativeTo(null);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        contentPanel.setLayout(new CardLayout());
    }

    //Helper Methods

    protected JButton addMenuItem(String text, String card) {
        return addMenuItem(text, card, null);
    }

    protected JButton addMenuItem(String text, String card, Runnable beforeShow) {
        JButton button = createMenuButton(text);
        menuButtons.put(card, button);
        button.addActionListener(e -> {
            if (beforeShow != null) {
                beforeShow.run();
            }
            showCard(card);
        });
        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(4));
        return button;
    }

    protected void addPage(JComponent page, String card) {
        contentPanel.add(page, card);
    }

    protected void showCard(String card) {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, card);
        JButton previous = activeButton;
        activeButton = menuButtons.get(card);
        if (previous != null) {
            paintMenuButton(previous, null);
        }
        if (activeButton != null) {
            paintMenuButton(activeButton, UiStyle.ACCENT);
        }
    }

    protected JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(UiStyle.FONT, Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 10));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        paintMenuButton(button, null);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    paintMenuButton(button, MENU_HOVER);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    paintMenuButton(button, null);
                }
            }
        });
        return button;
    }

    private void paintMenuButton(JButton button, Color background) {
        boolean filled = background != null;
        button.setOpaque(filled);
        button.setContentAreaFilled(filled);
        if (filled) {
            button.setBackground(background);
        }
        button.repaint();
    }

    protected JPanel createCard(String title, String value) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyle.LINE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UiStyle.FONT, Font.PLAIN, 13));
        titleLabel.setForeground(UiStyle.TEXT_MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(UiStyle.FONT, Font.BOLD, 30));
        valueLabel.setForeground(UiStyle.ACCENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        return card;
    }

    protected JPanel createCardGrid(int rows, int cols, JPanel... cards) {
        JPanel grid = new JPanel(new java.awt.GridLayout(rows, cols, 16, 16));
        grid.setOpaque(false);
        for (JPanel card : cards) {
            grid.add(card);
        }
        return grid;
    }

    protected JPanel createPageHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(UiStyle.FONT, Font.BOLD, 26));
        titleLabel.setForeground(UiStyle.TEXT_DARK);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font(UiStyle.FONT, Font.PLAIN, 14));
        subtitleLabel.setForeground(UiStyle.TEXT_MUTED);
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        return header;
    }

    protected DefaultTableModel createReadOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) { // anonymous class
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected JPanel createDashboardPage(String title, String subtitle, JPanel cards,
            String tableTitle, JTable table) {
        UiStyle.styleTable(table);

        JPanel top = new JPanel(new BorderLayout(0, 20));
        top.setOpaque(false);
        top.add(createPageHeader(title, subtitle), BorderLayout.NORTH);
        top.add(cards, BorderLayout.CENTER);

        JLabel sectionLabel = new JLabel(tableTitle);
        sectionLabel.setFont(new Font(UiStyle.FONT, Font.BOLD, 18));
        sectionLabel.setForeground(UiStyle.TEXT_DARK);

        JScrollPane scrollPane = new JScrollPane(table);
        UiStyle.styleScrollPane(scrollPane);

        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setOpaque(false);
        section.add(sectionLabel, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        JPanel page = new JPanel(new BorderLayout(0, 24));
        page.setOpaque(false);
        page.add(top, BorderLayout.NORTH);
        page.add(section, BorderLayout.CENTER);
        return page;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebar = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        menuPanel = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        contentPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sidebar.setBackground(new java.awt.Color(0, 77, 64));

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(255, 255, 255));
        lblLogo.setText("PetCareSys");
        lblLogo.setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 8, 24, 8));

        menuPanel.setOpaque(false);

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        btnLogout.setBackground(new java.awt.Color(0, 121, 107));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(sidebar);
        sidebar.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(menuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addComponent(lblLogo)
                .addGap(8, 8, 8)
                .addComponent(menuPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        mainPanel.setBackground(new java.awt.Color(244, 247, 246));

        contentPanel.setOpaque(false);

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
                .addGap(32, 32, 32))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sidebar, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        dispose();
        new LoginFrame().setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogout;
    protected javax.swing.JPanel contentPanel;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JPanel mainPanel;
    protected javax.swing.JPanel menuPanel;
    private javax.swing.JPanel sidebar;
    // End of variables declaration//GEN-END:variables
}
