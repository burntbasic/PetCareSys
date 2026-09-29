package com.petcare.view.petOwnerFrames;

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

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class AppointmentsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable appointmentsTable;
	private DefaultTableModel tableModel;

	private PetOwnerController controller;
	private List<Appointment> appointments;
	private User user;
	private String currentFilter = "Upcoming";
	
	/**
	 * Create the panel.
	 */
	public AppointmentsPanel(User user) {
		this.user = user;
		controller = new PetOwnerController();

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

		JButton bookAppointmentButton = new JButton("+ Book Appointment");
		bookAppointmentButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bookAppointmentButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {

		        BookAppointmentDialog dialog = new BookAppointmentDialog(user);
		        dialog.setVisible(true);

		        if (dialog.isChanged()) {
		            refreshAppointments();
		        }
		    }
		});
		headerPanel.add(bookAppointmentButton, BorderLayout.EAST);

		add(headerPanel, BorderLayout.NORTH);

		// FILTER BUTTONS

		JPanel filterPanel = new JPanel();

		JButton upcomingButton = new JButton("Upcoming");
		upcomingButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		upcomingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentFilter = "Upcoming";
				loadUpcomingAppointments();
			}
		});

		JButton pastButton = new JButton("Past");
		pastButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pastButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        currentFilter = "Past";
		    	loadPastAppointments();
		    }
		});
		
		JButton cancelledButton = new JButton("Cancelled");
		cancelledButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cancelledButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        currentFilter = "Cancelled";
		    	loadCancelledAppointments();
		    }
		});

		filterPanel.add(upcomingButton);
		filterPanel.add(pastButton);
		filterPanel.add(cancelledButton);

		// TABLE

		String[] columns = {"Pet", "Veterinarian", "Date & Time", "Status", "Actions"};

		tableModel = new DefaultTableModel(columns, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		appointmentsTable = new JTable(tableModel);
		appointmentsTable.setRowHeight(35);
		appointmentsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                label.setText("<html><u>View</u></html>");
                label.setHorizontalAlignment(JLabel.CENTER);
                
                return label;
            }
        });
		appointmentsTable.addMouseMotionListener(new MouseMotionAdapter() {

		    public void mouseMoved(MouseEvent e) {

		        int row = appointmentsTable.rowAtPoint(e.getPoint());
		        int column = appointmentsTable.columnAtPoint(e.getPoint());

		        if (row >= 0 && column == 4) {
		            appointmentsTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		        } else {
		            appointmentsTable.setCursor(Cursor.getDefaultCursor());
		        }
		    }
		});
		appointmentsTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				int row = appointmentsTable.rowAtPoint(e.getPoint());
				int column = appointmentsTable.columnAtPoint(e.getPoint());

				if (row >= 0 && column == 4 && appointments != null) {

					Appointment appointment = appointments.get(row);
											
					ViewAppointmentDialog dialog = new ViewAppointmentDialog(user, appointment);
					dialog.setVisible(true);

					if(dialog.isChanged()) {
						refreshAppointments();
					}
				}
			}
		});

		loadUpcomingAppointments();

		JScrollPane scrollPane = new JScrollPane(appointmentsTable);

		// CONTENT

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

		contentPanel.add(filterPanel, BorderLayout.NORTH);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
	}
	
	private void refreshAppointments() {

	    if ("Upcoming".equals(currentFilter)) {
	        loadUpcomingAppointments();
	    } else if ("Past".equals(currentFilter)) {
	        loadPastAppointments();
	    } else if ("Cancelled".equals(currentFilter)) {
	        loadCancelledAppointments();
	    }
	}
	
	private void loadUpcomingAppointments() {

		tableModel.setRowCount(0);

		try {
			appointments = controller.getOwnerUpcoming(user);

			for (Appointment appointment : appointments) {

				tableModel.addRow(new Object[] {
						appointment.getPetName(),
						appointment.getVetName(),
						appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")),
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
	
	private void loadPastAppointments() {

	    tableModel.setRowCount(0);

	    try {
	        appointments = controller.getOwnerPast(user);

	        for (Appointment appointment : appointments) {

	            tableModel.addRow(new Object[] {
	                    appointment.getPetName(),
	                    appointment.getVetName(),
	                    appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")),
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
	
	private void loadCancelledAppointments() {

	    tableModel.setRowCount(0);

	    try {
	        appointments = controller.getOwnerCancelled(user);

	        for (Appointment appointment : appointments) {

	            tableModel.addRow(new Object[] {
	                    appointment.getPetName(),
	                    appointment.getVetName(),
	                    appointment.getAppointmentDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")),
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
