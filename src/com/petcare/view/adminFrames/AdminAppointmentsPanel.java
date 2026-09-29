package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.petcare.controller.AdminController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class AdminAppointmentsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AdminController controller;

	private JTable appointmentsTable;
	private DefaultTableModel tableModel;
	private JTextField searchField;

	private List<Appointment> appointments;

	public AdminAppointmentsPanel(User user) {

		controller = new AdminController(user);

		setLayout(new BorderLayout(10, 10));
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JPanel headerPanel = new JPanel(new BorderLayout());

	    JLabel titleLabel = new JLabel("Appointments");
	    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

	    headerPanel.add(titleLabel, BorderLayout.WEST);

	    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

	    JLabel searchLabel = new JLabel("Search:");

	    searchField = new JTextField(20);
	    searchField.setToolTipText("Search appointments");

	    JButton addButton = new JButton("Add Appointment");

	    addButton.setCursor(
	            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    addButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {

	            AddAppointmentDialog dialog = new AddAppointmentDialog(user);

	            dialog.setVisible(true);

	            if (dialog.isChanged()) {
	                loadAppointments();
	            }
	        }
	    });

	    searchPanel.add(searchLabel);
	    searchPanel.add(searchField);
	    searchPanel.add(addButton);

	    headerPanel.add(searchPanel, BorderLayout.EAST);

	    add(headerPanel, BorderLayout.NORTH);

	    searchField.getDocument().addDocumentListener(
	            new DocumentListener() {

	                public void insertUpdate(DocumentEvent e) {
	                    filterAppointments();
	                }

	                public void removeUpdate(DocumentEvent e) {
	                    filterAppointments();
	                }

	                public void changedUpdate(DocumentEvent e) {
	                    filterAppointments();
	                }
	            });
		
	    String[] columns = {
				"ID",
				"Pet",
				"Owner",
				"Veterinarian",
				"Date & Time",
				"Reason",
				"Status",
				"View",
				"Edit",
				"Cancel",
				"Delete"
		};

		tableModel = new DefaultTableModel(columns, 0) {

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		appointmentsTable = new JTable(tableModel);

		appointmentsTable.setRowHeight(35);
		appointmentsTable.setAutoCreateRowSorter(true);

		add(
				new JScrollPane(appointmentsTable),
				BorderLayout.CENTER);

		setupButtonColumn(7);
		setupButtonColumn(8);
		setupButtonColumn(9);
		setupButtonColumn(10);

		appointmentsTable.addMouseListener(
				new MouseAdapter() {

					public void mouseClicked(MouseEvent e) {

						if (e.getClickCount() != 1) {
							return;
						}

						int row = appointmentsTable.rowAtPoint(
								e.getPoint());

						int column = appointmentsTable.columnAtPoint(
								e.getPoint());

						if (row < 0) {
							return;
						}

						if (column == 7) {

							viewAppointment(row);

						} else if (column == 8) {

							editAppointment(row, user);

						} else if (column == 9) {

							cancelAppointment(row);

						} else if (column == 10) {

							deleteAppointment(row);
						}
					}
				});
		appointmentsTable.addMouseMotionListener(new MouseMotionAdapter() {
		    public void mouseMoved(MouseEvent e) {

		        int row = appointmentsTable.rowAtPoint(e.getPoint());
		        int column = appointmentsTable.columnAtPoint(e.getPoint());

		        if (row >= 0 && column >= 7 && column <= 10) {

		            Object value = appointmentsTable.getValueAt(row, column);

		            if (value != null) {
		                appointmentsTable.setCursor(
		                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		                return;
		            }
		        }

		        appointmentsTable.setCursor(
		                Cursor.getDefaultCursor());
		    }
		});
		loadAppointments();
	}

	private void setupButtonColumn(int column) {

		appointmentsTable.getColumnModel()
				.getColumn(column)
				.setCellRenderer(new ButtonRenderer());
	}

	private void loadAppointments() {

		tableModel.setRowCount(0);

		try {

			appointments = controller.getAllAppointments();

			for (Appointment appointment : appointments) {
				addAppointmentRow(appointment);
			}

		} catch (SQLException e) {

			ErrorHandler.handleTableLoadError(
					e,
					appointmentsTable,
					tableModel);

		} catch (DatabaseConfigException e) {

			ErrorHandler.handleTableLoadError(
					e,
					appointmentsTable,
					tableModel);
		}
	}

	private void addAppointmentRow(Appointment appointment) {

		String date = appointment.getAppointmentDate()
				.toLocalDateTime()
				.format(DateTimeFormatter.ofPattern(
						"dd MMM yyyy, h:mm a"));

		tableModel.addRow(new Object[] {
				appointment.getAppointmentId(),
				appointment.getPetName(),
				appointment.getOwnerName(),
				appointment.getVetName(),
				date,
				appointment.getReason(),
				appointment.getStatus(),
				"View",
				"Edit",
				"Scheduled".equals(appointment.getStatus())
						? "Cancel"
						: null,
				"Delete"
		});
	}

	private void filterAppointments() {

		if (appointments == null) {
			return;
		}

		String search = searchField.getText()
				.trim()
				.toLowerCase();

		tableModel.setRowCount(0);

		for (Appointment appointment : appointments) {

			String date = appointment.getAppointmentDate()
					.toLocalDateTime()
					.format(DateTimeFormatter.ofPattern(
							"dd MMM yyyy, h:mm a"));

			String data =
					appointment.getAppointmentId()
					+ " "
					+ appointment.getPetName()
					+ " "
					+ appointment.getOwnerName()
					+ " "
					+ appointment.getVetName()
					+ " "
					+ date
					+ " "
					+ appointment.getReason()
					+ " "
					+ appointment.getStatus();

			if (data.toLowerCase().contains(search)) {
				addAppointmentRow(appointment);
			}
		}
	}

	private void viewAppointment(int row) {

		Appointment appointment = getAppointmentFromRow(row);

		if (appointment == null) {
			return;
		}

		ViewAppointmentDialog dialog =
				new ViewAppointmentDialog(appointment);

		dialog.setVisible(true);
	}

	private void editAppointment(int row, User user) {

		Appointment appointment = getAppointmentFromRow(row);

		if (appointment == null) {
			return;
		}

		EditAppointmentDialog dialog = new EditAppointmentDialog(appointment, user);

		dialog.setVisible(true);

		if (dialog.isChanged()) {
			loadAppointments();
		}
	}

	private void cancelAppointment(int row) {

		Appointment appointment = getAppointmentFromRow(row);

		if (appointment == null) {
			return;
		}

		if (!"Scheduled".equals(appointment.getStatus())) {
			return;
		}

		int result = JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to cancel this appointment?",
				"Cancel Appointment",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			controller.cancelAppointment(appointment);

			JOptionPane.showMessageDialog(
					this,
					"Appointment cancelled successfully.",
					"Appointment Cancelled",
					JOptionPane.INFORMATION_MESSAGE);

			loadAppointments();

		} catch (SQLException e) {

			ErrorHandler.handleSQLException(e);

		} catch (DatabaseConfigException e) {

			ErrorHandler.handleDatabaseConfigException(e);
		}
	}

	private void deleteAppointment(int row) {

		Appointment appointment = getAppointmentFromRow(row);

		if (appointment == null) {
			return;
		}

		int result = JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to permanently delete this appointment?",
				"Delete Appointment",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (result != JOptionPane.YES_OPTION) {
			return;
		}

		try {

			controller.deleteAppointment(
					appointment.getAppointmentId());

			JOptionPane.showMessageDialog(
					this,
					"Appointment deleted successfully.",
					"Appointment Deleted",
					JOptionPane.INFORMATION_MESSAGE);

			loadAppointments();

		} catch (SQLException e) {

			if (e.getErrorCode() == 1451) {

				JOptionPane.showMessageDialog(
						this,
						"This appointment cannot be deleted because it has an associated medical treatment.",
						"Cannot Delete Appointment",
						JOptionPane.WARNING_MESSAGE);

			} else {

				ErrorHandler.handleSQLException(e);
			}

		} catch (DatabaseConfigException e) {

			ErrorHandler.handleDatabaseConfigException(e);
		}
	}

	private Appointment getAppointmentFromRow(int row) {

		int modelRow =
				appointmentsTable.convertRowIndexToModel(row);

		int appointmentId =
				(int) tableModel.getValueAt(modelRow, 0);

		for (Appointment appointment : appointments) {

			if (appointment.getAppointmentId() == appointmentId) {
				return appointment;
			}
		}

		return null;
	}

	public void refreshAppointments() {
		loadAppointments();
	}

	private class ButtonRenderer extends JButton
			implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {

			setText(value == null ? "" : value.toString());

			if (value == null) {

				setEnabled(false);
				setCursor(Cursor.getDefaultCursor());

			} else {

				setEnabled(true);
				setCursor(
						Cursor.getPredefinedCursor(
								Cursor.HAND_CURSOR));
			}

			return this;
		}
	}
}
