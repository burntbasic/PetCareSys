package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class UserManagementPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private AdminController controller;
    private User loggedInUser;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private List<User> users = new ArrayList<>();

    public UserManagementPanel(User user) {

        loggedInUser = user;
        controller = new AdminController(user);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // HEADER

        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton addUserButton = new JButton("Add User");
        addUserButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                AddUserDialog dialog = new AddUserDialog(user);

                dialog.setVisible(true);

                if (dialog.isChanged()) {
                    loadUsers();
                }
            }
        });
        JButton addPetButton = new JButton("Add Pet");
        addPetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addPetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                AddPetDialog dialog = new AddPetDialog(user);

                dialog.setVisible(true);
            }
        });

        // SEARCH

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel searchLabel = new JLabel("Search:");

        searchField = new JTextField(20);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterUsers();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(addUserButton);
        searchPanel.add(addPetButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // TABLE

        String[] columns = {"ID", "Name", "Email", "Phone", "Username", "Role", "Edit", "Delete"};

        tableModel = new DefaultTableModel(columns, 0) {
        	public boolean isCellEditable(int row, int column) {

        	    if (column == 7 && getValueAt(row, column) == null) {
        	        return false;
        	    }

        	    return column == 6 || column == 7;
        	}
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);

        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("Edit"));
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor("Edit", true));
        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer("Delete"));
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor("Delete", false));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
        
        loadUsers();
    }

    private void loadUsers() {

        tableModel.setRowCount(0);

        try {
            users = controller.getAllUsers();

            displayUsers(users);

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, table, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, table, tableModel);
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

        String searchText = searchField.getText().trim().toLowerCase();

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

        int userId = (int) table.getValueAt(row, 0);

        User selectedUser = findUser(userId);

        if (selectedUser == null) {
            return;
        }

        EditUserDialog dialog = new EditUserDialog(selectedUser);

        dialog.setVisible(true);

        if (dialog.isChanged()) {
            loadUsers();
        }
    }

    private void deleteUser(int row) {

        int userId = (int) table.getValueAt(row, 0);

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

            if (e.getErrorCode() == 1451) { //MySQL Foreign Key restriction errorcode
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

    // BUTTON RENDERER

    private class ButtonRenderer extends JButton implements TableCellRenderer {

        private static final long serialVersionUID = 1L;

		public ButtonRenderer(String text) {
            setText(text);
            setFocusable(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		public Component getTableCellRendererComponent(
		        JTable table,
		        Object value,
		        boolean isSelected,
		        boolean hasFocus,
		        int row,
		        int column) {

		    if (value == null) {
		        setText("");
		        setEnabled(false);
		        setBorderPainted(false);
		        setContentAreaFilled(false);
		    } else {
		        setText(value.toString());
		        setEnabled(true);
		        setBorderPainted(true);
		        setContentAreaFilled(true);
		    }

		    return this;
		}
    }

    // BUTTON EDITOR

    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private static final long serialVersionUID = 1L;
		private JButton button;
        private String buttonType;
        private int row;

        public ButtonEditor(String text, boolean editButton) {

            buttonType = text;

            button = new JButton(text);
            button.setFocusable(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    fireEditingStopped();

                    if (editButton) {
                        editUser(row);
                    } else {
                        deleteUser(row);
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {

            this.row = row;

            if (value == null) {
                button.setText("");
                button.setEnabled(false);
            } else {
                button.setText(buttonType);
                button.setEnabled(true);
            }

            return button;
        }

        public Object getCellEditorValue() {
            return buttonType;
        }
    }
}
