package com.petcare.view.vetFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.petcare.controller.VetController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class VetAppointmentsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable appointmentsTable;
    private DefaultTableModel tableModel;

    private VetController controller;
    private List<Appointment> appointments;
    private User user;

    private String currentFilter = "Today";

    /**
     * Create the panel.
     */
    public VetAppointmentsPanel(User user) {

        this.user = user;
        controller = new VetController();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // HEADER

        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Appointments");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Manage your veterinary appointments");

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

        headerText.add(titleLabel);
        headerText.add(subtitleLabel);

        headerPanel.add(headerText, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // FILTER BUTTONS

        JPanel filterPanel = new JPanel();

        JButton todayButton = new JButton("Today");
        JButton upcomingButton = new JButton("Upcoming");
        JButton pastButton = new JButton("Past");
        JButton cancelledButton = new JButton("Cancelled");

        todayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        upcomingButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pastButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelledButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        todayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currentFilter = "Today";
                loadTodayAppointments(user);
            }
        });

        upcomingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currentFilter = "Upcoming";
                loadUpcomingAppointments(user);
            }
        });

        pastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currentFilter = "Past";
                loadPastAppointments(user);
            }
        });

        cancelledButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                currentFilter = "Cancelled";
                loadCancelledAppointments(user);
            }
        });

        filterPanel.add(todayButton);
        filterPanel.add(upcomingButton);
        filterPanel.add(pastButton);
        filterPanel.add(cancelledButton);

        // TABLE

        String[] columns = {"Time", "Pet", "Owner", "Reason", "Status", "Actions"};

        tableModel = new DefaultTableModel(columns, 0) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setRowHeight(35);

        // ACTIONS COLUMN

        appointmentsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                label.setText("<html><u>View</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);

                return label;
            }
        });

        // ACTIONS COLUMN CURSOR

        appointmentsTable.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {

                int row = appointmentsTable.rowAtPoint(e.getPoint());
                int column = appointmentsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 5) {
                    appointmentsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    appointmentsTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        // ACTIONS COLUMN CLICK

        appointmentsTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {

                int row = appointmentsTable.rowAtPoint(e.getPoint());
                int column = appointmentsTable.columnAtPoint(e.getPoint());

                if (row >= 0 && column == 5 && appointments != null) {

                    Appointment appointment = appointments.get(row);

                    VetAppointmentDialog dialog = new VetAppointmentDialog(user, appointment);

                    dialog.setVisible(true);

                    if(dialog.isChanged()) {
                        refreshAppointments();
                    }
                }
            }
        });

        // SCROLL PANE

        JScrollPane scrollPane = new JScrollPane(appointmentsTable);

        // CONTENT

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // LOAD DEFAULT FILTER

        loadTodayAppointments(user);
    }

    // REFRESH

    private void refreshAppointments() {

        if ("Today".equals(currentFilter)) {

            loadTodayAppointments(user);

        } else if ("Upcoming".equals(currentFilter)) {

            loadUpcomingAppointments(user);

        } else if ("Past".equals(currentFilter)) {

            loadPastAppointments(user);

        } else if ("Cancelled".equals(currentFilter)) {

            loadCancelledAppointments(user);
        }
    }

    // TODAY

    private void loadTodayAppointments(User user) {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetTodays(user);

            for (Appointment appointment : appointments) {

                String time = appointment.getAppointmentDate().toLocalDateTime().format(
                        DateTimeFormatter.ofPattern("h:mm a"));

                tableModel.addRow(new Object[] {
                        time,
                        appointment.getPetName(),
                        appointment.getOwnerName(),
                        appointment.getReason(),
                        appointment.getStatus(),
                        "View"
                });
            }

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    // UPCOMING

    private void loadUpcomingAppointments(User user) {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetUpcoming(user);

            for (Appointment appointment : appointments) {

                String time = appointment.getAppointmentDate().toLocalDateTime().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

                tableModel.addRow(new Object[] {
                        time,
                        appointment.getPetName(),
                        appointment.getOwnerName(),
                        appointment.getReason(),
                        appointment.getStatus(),
                        "View"
                });
            }

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    // PAST

    private void loadPastAppointments(User user) {

        tableModel.setRowCount(0);

        try {
            appointments = controller.getVetPast(user);

            for (Appointment appointment : appointments) {

                String time = appointment.getAppointmentDate().toLocalDateTime().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

                tableModel.addRow(new Object[] {
                        time,
                        appointment.getPetName(),
                        appointment.getOwnerName(),
                        appointment.getReason(),
                        appointment.getStatus(),
                        "View"
                });
            }

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }

    // CANCELLED

    private void loadCancelledAppointments(User user) {

        tableModel.setRowCount(0);

        try {
        	appointments = controller.getVetCancelled(user);

            for (Appointment appointment : appointments) {

                String time = appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a"));

                tableModel.addRow(new Object[] {
                        time,
                        appointment.getPetName(),
                        appointment.getOwnerName(),
                        appointment.getReason(),
                        appointment.getStatus(),
                        "View"
                });
            }

        } catch (SQLException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        } catch (DatabaseConfigException e) {
            ErrorHandler.handleTableLoadError(e, appointmentsTable, tableModel);
        }
    }
}
