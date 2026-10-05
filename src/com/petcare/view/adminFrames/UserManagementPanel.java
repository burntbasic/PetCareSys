/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.petcare.view.adminFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;
import com.petcare.view.UiStyle;

/**
 *
 * @author hirunaka
 */
public class UserManagementPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final int EDIT_COLUMN = 6;
    private static final int DELETE_COLUMN = 7;

    private AdminController controller;
    private User loggedInUser;

    private List<User> users = new ArrayList<>();

    // Created before initComponents() because the form's table uses it as its model
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"ID", "Name", "Email", "Phone", "Username", "Role", "Edit", "Delete"}, 0) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form UserManagementPanel
     */
    public UserManagementPanel(User user) {
        loggedInUser = user;
        controller = new AdminController(user);

        initComponents();

        UiStyle.styleTable(usersTable);
        UiStyle.styleScrollPane(scrollPane);
        initSearch();
        initTableBehaviour();

        loadUsers();
    }

    /**
     * Filters the table as the user types.
     */
    private void initSearch() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                filterUsers();
            }

            public void removeUpdate(DocumentEvent e) {
                filterUsers();
            }

            public void changedUpdate(DocumentEvent e) {
                filterUsers();
            }
        });
    }

    /**
     * Column widths, the "Edit" and "Delete" links, click and hover cursor.
     */
    private void initTableBehaviour() {

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        usersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        usersTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        usersTable.getColumnModel().getColumn(EDIT_COLUMN).setCellRenderer(linkRenderer("Edit", UiStyle.ACCENT));
        usersTable.getColumnModel().getColumn(EDIT_COLUMN).setPreferredWidth(70);
        usersTable.getColumnModel().getColumn(DELETE_COLUMN).setCellRenderer(linkRenderer("Delete", UiStyle.ERROR));
        usersTable.getColumnModel().getColumn(DELETE_COLUMN).setPreferredWidth(80);

        usersTable.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {

                int row = usersTable.rowAtPoint(e.getPoint());
                int column = usersTable.columnAtPoint(e.getPoint());

                if (isActionCell(row, column)) {
                    usersTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    usersTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        usersTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = usersTable.rowAtPoint(e.getPoint());
                int column = usersTable.columnAtPoint(e.getPoint());

                if (!isActionCell(row, column)) {
                    return;
                }

                if (column == EDIT_COLUMN) {
                    editUser(row);
                } else {
                    deleteUser(row);
                }
            }
        });
    }

    /**
     * True for an Edit link, or a Delete link (the logged in user has none).
     */
    private boolean isActionCell(int row, int column) {
        if (row < 0) {
            return false;
        }
        if (column == EDIT_COLUMN) {
            return true;
        }
        return column == DELETE_COLUMN && usersTable.getValueAt(row, column) != null;
    }

    private DefaultTableCellRenderer linkRenderer(String text, Color color) {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // A missing value means no action for this row (e.g. you can't delete yourself)
                label.setText(value == null ? "" : "<html><u>" + text + "</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    label.setForeground(color);
                }

                return label;
            }
        };
    }

    private void loadUsers() {

        tableModel.setRowCount(0);

        try {
            users = controller.getAllUsers();
            displayUsers(users);

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, usersTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, usersTable, tableModel);
        }
    }

    private void displayUsers(List<User> userList) {

        tableModel.setRowCount(0);

        for (User user : userList) {

            Object deleteButton = null;

            if (user.getId() != loggedInUser.getId()) {
                deleteButton = "Delete";
            }

            tableModel.addRow(new Object[] {
                    user.getId(),
                    user.getFName() + " " + user.getLName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getUsername(),
                    user.getRole(),
                    "Edit",
                    deleteButton
            });
        }
    }

    private void filterUsers() {

        String searchText = txtSearch.getText().trim().toLowerCase();

        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {

            String name = user.getFName() + " " + user.getLName();

            if (name.toLowerCase().contains(searchText)
                    || user.getEmail().toLowerCase().contains(searchText)
                    || user.getUsername().toLowerCase().contains(searchText)
                    || user.getRole().toLowerCase().contains(searchText)) {
                filteredUsers.add(user);
            }
        }
        displayUsers(filteredUsers);
    }

    private void editUser(int row) {

        int userId = (int) usersTable.getValueAt(row, 0);

        User selectedUser = findUser(userId);

        if (selectedUser == null) {
            return;
        }

        EditUserDialog dialog = new EditUserDialog(selectedUser, loggedInUser);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            loadUsers();
        }
    }

    private void deleteUser(int row) {

        int userId = (int) usersTable.getValueAt(row, 0);

        if (userId == loggedInUser.getId()) {
            return;
        }

        User selectedUser = findUser(userId);

        if (selectedUser == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selectedUser.getFName() + " " + selectedUser.getLName() + "?", "Delete User", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = controller.deleteUser(userId);

            if (deleted) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.", "User Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            }
        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) { // MySQL Foreign Key restriction errorcode
                JOptionPane.showMessageDialog(this, "This user cannot be deleted because they have " + "associated records.", "Cannot Delete User", JOptionPane.WARNING_MESSAGE);
            } else {
                ErrorHandler.handleSQLException(e);
            }
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleDatabaseConfigException(e);
        }
    }

    private User findUser(int userId) {

        for (User user : users) {

            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    public void refreshUsers() {
        loadUsers();
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
        btnAddPet = new javax.swing.JButton();
        btnAddUser = new javax.swing.JButton();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();

        setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(33, 37, 41));
        lblTitle.setText("User Management");

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(108, 117, 125));
        lblSubtitle.setText("Manage the people who use PetCare");

        btnAddPet.setBackground(new java.awt.Color(255, 255, 255));
        btnAddPet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddPet.setForeground(new java.awt.Color(0, 121, 107));
        btnAddPet.setText("Add Pet");
        btnAddPet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 121, 107)));
        btnAddPet.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddPet.setFocusPainted(false);
        btnAddPet.addActionListener(this::btnAddPetActionPerformed);

        btnAddUser.setBackground(new java.awt.Color(0, 121, 107));
        btnAddUser.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAddUser.setForeground(new java.awt.Color(255, 255, 255));
        btnAddUser.setText("+ Add User");
        btnAddUser.setBorderPainted(false);
        btnAddUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddUser.setFocusPainted(false);
        btnAddUser.addActionListener(this::btnAddUserActionPerformed);

        lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSearch.setForeground(new java.awt.Color(73, 80, 87));
        lblSearch.setText("Search");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(33, 37, 41));
        txtSearch.setToolTipText("Search by name, email, username or role");
        txtSearch.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(206, 212, 218)), javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        usersTable.setModel(tableModel);
        scrollPane.setViewportView(usersTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(lblSubtitle))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAddPet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnAddUser, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblSearch)
                .addGap(10, 10, 10)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(4, 4, 4)
                        .addComponent(lblSubtitle))
                    .addComponent(btnAddPet, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
        AddUserDialog dialog = new AddUserDialog(loggedInUser);
        dialog.setVisible(true);

        if (dialog.isChanged()) {
            loadUsers();
        }
    }//GEN-LAST:event_btnAddUserActionPerformed

    private void btnAddPetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPetActionPerformed
        AddPetDialog dialog = new AddPetDialog(loggedInUser);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnAddPetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddPet;
    private javax.swing.JButton btnAddUser;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables
}
