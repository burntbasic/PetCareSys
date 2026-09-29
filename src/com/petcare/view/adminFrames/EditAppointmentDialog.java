package com.petcare.view.adminFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarBorderProperties;

import com.petcare.controller.AdminController;
import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Appointment;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class EditAppointmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private AdminController controller;
	private PetOwnerController pController;
	private Appointment appointment;

	private DatePicker datePicker;
	private JComboBox<LocalTime> timeComboBox;

	private JTextField reasonField;
	private JComboBox<String> statusComboBox;

	private boolean changed = false;

	public EditAppointmentDialog(Appointment appointment, User user) {

		this.appointment = appointment;

		controller = new AdminController(user);
		pController = new PetOwnerController();

		setTitle("Edit Appointment");
		setSize(new Dimension(450, 450));
		setLocationRelativeTo(null);
		setModal(true);

		createForm();
	}

	private void createForm() {

		JPanel formPanel =
				new JPanel(new GridBagLayout());

		formPanel.setBorder(
				BorderFactory.createEmptyBorder(
						15, 20, 10, 20));

		GridBagConstraints gbc =
				new GridBagConstraints();

		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.anchor = GridBagConstraints.WEST;

		JLabel titleLabel =
				new JLabel("Edit Appointment");

		titleLabel.setFont(
				new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		formPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		// PET

		JLabel petLabel =
				new JLabel("Pet");

		JLabel petValueLabel =
				new JLabel(appointment.getPetName());

		gbc.gridx = 0;
		gbc.gridy = 1;

		formPanel.add(petLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(petValueLabel, gbc);

		// OWNER

		JLabel ownerLabel =
				new JLabel("Owner");

		JLabel ownerValueLabel =
				new JLabel(appointment.getOwnerName());

		gbc.gridx = 0;
		gbc.gridy = 2;

		formPanel.add(ownerLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(ownerValueLabel, gbc);

		// VETERINARIAN

		JLabel vetLabel =
				new JLabel("Veterinarian");

		JLabel vetValueLabel =
				new JLabel(appointment.getVetName());

		gbc.gridx = 0;
		gbc.gridy = 3;

		formPanel.add(vetLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(vetValueLabel, gbc);

		// DATE

		JLabel dateLabel =
				new JLabel("Date");

		gbc.gridx = 0;
		gbc.gridy = 4;

		formPanel.add(dateLabel, gbc);

		DatePickerSettings dateSettings = new DatePickerSettings();

		Color background = UIManager.getColor("TextField.background");
		Color foreground = UIManager.getColor("TextField.foreground");
		Color panelBackground = UIManager.getColor("Panel.background");
		Color borderColor = UIManager.getColor("Component.borderColor");

		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, background);
		dateSettings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, background);
		dateSettings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, background);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundVetoedDates, background);
		dateSettings.setColorBackgroundWeekdayLabels(panelBackground, false);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons, panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextTodayLabel, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextClearLabel, foreground);
		ArrayList<CalendarBorderProperties> borderProperties = new ArrayList<>();
		borderProperties.add(new CalendarBorderProperties(new Point(1, 1), new Point(5, 5), borderColor, 1));
		dateSettings.setBorderPropertiesList(borderProperties);
		dateSettings.setBorderCalendarPopup(BorderFactory.createLineBorder(borderColor));

		datePicker = new DatePicker(dateSettings);
		dateSettings.setDateRangeLimits(LocalDate.now(), null);
		datePicker.getComponentDateTextField().setBorder(UIManager.getBorder("TextField.border"));
		datePicker.addPropertyChangeListener("date", new PropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent evt) {
		        loadAvailableTimes();
		    }
		});
		gbc.gridx = 1;

		formPanel.add(datePicker, gbc);

		// TIME

		JLabel timeLabel =
				new JLabel("Time");

		gbc.gridx = 0;
		gbc.gridy = 5;

		formPanel.add(timeLabel, gbc);

		timeComboBox =
				new JComboBox<>();

		timeComboBox.setRenderer(
				new DefaultListCellRenderer() {

					public Component getListCellRendererComponent(
							JList<?> list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {

						super.getListCellRendererComponent(
								list,
								value,
								index,
								isSelected,
								cellHasFocus);

						if (value instanceof LocalTime) {

							LocalTime time =
									(LocalTime) value;

							setText(
									time.format(
											DateTimeFormatter.ofPattern(
													"h:mm a")));
						}

						return this;
					}
				});

		gbc.gridx = 1;

		formPanel.add(timeComboBox, gbc);

		// REASON

		JLabel reasonLabel =
				new JLabel("Reason");

		reasonField =
				new JTextField(15);

		reasonField.setText(
				appointment.getReason() == null
						? ""
						: appointment.getReason());

		gbc.gridx = 0;
		gbc.gridy = 6;

		formPanel.add(reasonLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(reasonField, gbc);

		// STATUS

		JLabel statusLabel =
				new JLabel("Status");

		String[] statuses = {
				"Scheduled",
				"Completed",
				"Cancelled"
		};

		statusComboBox =
				new JComboBox<>(statuses);

		statusComboBox.setSelectedItem(
				appointment.getStatus());

		gbc.gridx = 0;
		gbc.gridy = 7;

		formPanel.add(statusLabel, gbc);

		gbc.gridx = 1;

		formPanel.add(statusComboBox, gbc);

		add(
				formPanel,
				BorderLayout.CENTER);

		// INITIAL DATE AND TIME

		setInitialDateAndTime();

		// DATE CHANGE

		datePicker.addPropertyChangeListener(
				"date",
				new PropertyChangeListener() {

					public void propertyChange(
							PropertyChangeEvent evt) {

						loadAvailableTimes();
					}
				});

		// BUTTONS

		JPanel buttonPanel =
				new JPanel(new FlowLayout(
						FlowLayout.RIGHT));

		JButton cancelButton =
				new JButton("Cancel");

		JButton saveButton =
				new JButton("Save Changes");

		cancelButton.setCursor(
				Cursor.getPredefinedCursor(
						Cursor.HAND_CURSOR));

		saveButton.setCursor(
				Cursor.getPredefinedCursor(
						Cursor.HAND_CURSOR));

		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);

		add(
				buttonPanel,
				BorderLayout.SOUTH);

		// CANCEL

		cancelButton.addActionListener(
				new ActionListener() {

					public void actionPerformed(
							ActionEvent e) {

						dispose();
					}
				});

		// SAVE

		saveButton.addActionListener(
				new ActionListener() {

					public void actionPerformed(
							ActionEvent e) {

						updateAppointment();
					}
				});
	}

	private void setInitialDateAndTime() {

		LocalDateTime appointmentDateTime =
				appointment.getAppointmentDate()
						.toLocalDateTime();

		LocalDate date =
				appointmentDateTime.toLocalDate();

		LocalTime time =
				appointmentDateTime.toLocalTime();

		LocalDate today =
				LocalDate.now();

		if (date.isBefore(today)) {

			date = today;

			time = LocalTime.now()
					.plusMinutes(1)
					.withSecond(0)
					.withNano(0);
		}

		if (date.equals(today)
				&& !time.isAfter(LocalTime.now())) {

			time = LocalTime.now()
					.plusMinutes(1)
					.withSecond(0)
					.withNano(0);
		}

		datePicker.setDate(date);

		loadAvailableTimes();

		if (timeComboBox.getItemCount() == 0) {
			return;
		}

		boolean found = false;

		for (int i = 0;
				i < timeComboBox.getItemCount();
				i++) {

			LocalTime availableTime =
					timeComboBox.getItemAt(i);

			if (availableTime.equals(time)) {

				timeComboBox.setSelectedIndex(i);
				found = true;
				break;
			}
		}

		if (!found) {

			timeComboBox.setSelectedItem(
					findClosestTime(time));
		}
	}

	private void loadAvailableTimes() {

		timeComboBox.removeAllItems();

		LocalDate selectedDate =
				datePicker.getDate();

		if (selectedDate == null) {
			return;
		}

		try {

			List<LocalTime> availableTimes =
					pController.getAvailableTimes(
							appointment.getVetId(),
							selectedDate);

			LocalTime currentAppointmentTime =
					appointment.getAppointmentDate()
							.toLocalDateTime()
							.toLocalTime()
							.withSecond(0)
							.withNano(0);

			for (LocalTime time : availableTimes) {

				LocalTime cleanTime =
						time.withSecond(0)
								.withNano(0);

				if (selectedDate.equals(
						LocalDate.now())
						&& !cleanTime.isAfter(
								LocalTime.now())) {

					continue;
				}

				timeComboBox.addItem(cleanTime);
			}

			if (selectedDate.equals(
					appointment.getAppointmentDate()
							.toLocalDateTime()
							.toLocalDate())
					&& !timeAlreadyExists(
							currentAppointmentTime)) {

				if (!selectedDate.equals(LocalDate.now())
						|| currentAppointmentTime.isAfter(
								LocalTime.now())) {

					timeComboBox.addItem(
							currentAppointmentTime);
				}
			}

		} catch (SQLException e) {

			ErrorHandler.handleSQLException(e);

		} catch (DatabaseConfigException e) {

			ErrorHandler.handleDatabaseConfigException(e);
		}
	}

	private boolean timeAlreadyExists(LocalTime time) {

		for (int i = 0;
				i < timeComboBox.getItemCount();
				i++) {

			if (timeComboBox.getItemAt(i)
					.equals(time)) {

				return true;
			}
		}

		return false;
	}

	private LocalTime findClosestTime(LocalTime target) {

		if (timeComboBox.getItemCount() == 0) {
			return null;
		}

		LocalTime closest =
				timeComboBox.getItemAt(0);

		long closestDifference =
				Math.abs(
						closest.toSecondOfDay()
						- target.toSecondOfDay());

		for (int i = 1;
				i < timeComboBox.getItemCount();
				i++) {

			LocalTime time =
					timeComboBox.getItemAt(i);

			long difference =
					Math.abs(
							time.toSecondOfDay()
							- target.toSecondOfDay());

			if (difference < closestDifference) {

				closest = time;
				closestDifference = difference;
			}
		}

		return closest;
	}

	private void updateAppointment() {

		LocalDate date =
				datePicker.getDate();

		LocalTime time =
				(LocalTime) timeComboBox.getSelectedItem();

		String reason =
				reasonField.getText().trim();

		String status =
				(String) statusComboBox.getSelectedItem();

		if (date == null) {

			JOptionPane.showMessageDialog(
					this,
					"Please select an appointment date.",
					"Validation Error",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		if (time == null) {

			JOptionPane.showMessageDialog(
					this,
					"Please select an appointment time.",
					"Validation Error",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		LocalDateTime dateTime =
				LocalDateTime.of(date, time);

		if ("Scheduled".equals(status)
				&& dateTime.isBefore(
						LocalDateTime.now())) {

			JOptionPane.showMessageDialog(
					this,
					"A scheduled appointment cannot be set to a past date and time.",
					"Validation Error",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		Date appointmentDate =
				Date.from(
						dateTime.atZone(
								ZoneId.systemDefault())
								.toInstant());

		Timestamp timestamp =
				new Timestamp(
						appointmentDate.getTime());

		Appointment updatedAppointment =
				new Appointment(
						appointment.getAppointmentId(),
						appointment.getPetId(),
						appointment.getVetId(),
						appointment.getPetName(),
						appointment.getOwnerName(),
						appointment.getVetName(),
						timestamp,
						status,
						reason);

		try {

			boolean updated =
					controller.updateAppointment(
							updatedAppointment);

			if (updated) {

				changed = true;

				JOptionPane.showMessageDialog(
						this,
						"Appointment updated successfully.",
						"Appointment Updated",
						JOptionPane.INFORMATION_MESSAGE);

				dispose();
			}

		} catch (SQLException e) {

			ErrorHandler.handleSQLException(e);

		} catch (DatabaseConfigException e) {

			ErrorHandler.handleDatabaseConfigException(e);
		}
	}

	public boolean isChanged() {
		return changed;
	}
}