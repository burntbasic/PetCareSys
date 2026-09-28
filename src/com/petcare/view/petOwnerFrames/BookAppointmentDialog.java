package com.petcare.view.petOwnerFrames;

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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.CalendarBorderProperties;
import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.util.ErrorHandler;

public class BookAppointmentDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JComboBox<LocalTime> timeComboBox;
	private DatePicker datePicker;
	JComboBox<User> vetComboBox;

	private PetOwnerController controller;
	
	private boolean changed = false;
	
	public boolean isChanged() {
	    return changed;
	}
	
	public BookAppointmentDialog(User user) {

		controller = new PetOwnerController();

		setTitle("Book Appointment");
		setModal(true);
		setSize(new Dimension(500, 500));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(15, 15));

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 5, 8, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		// TITLE

		JLabel titleLabel = new JLabel("Book Appointment");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;

		formPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;

		// PET

		JLabel petLabel = new JLabel("Pet:");

		gbc.gridx = 0;
		gbc.gridy = 1;

		formPanel.add(petLabel, gbc);

		JComboBox<Pet> petComboBox = new JComboBox<>();
		petComboBox.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(
						list, value, index, isSelected, cellHasFocus);

				if (value instanceof Pet) {
					Pet pet = (Pet) value;
					setText(pet.getName());
				}

				return this;
			}
		});

		try {
			List<Pet> pets = controller.getOwnerPets(user);

			for (Pet pet : pets) {
				petComboBox.addItem(pet);
			}

		} catch (SQLException e) {
			ErrorHandler.handleSQLException(e);
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
		}

		gbc.gridx = 1;
		gbc.weightx = 1;

		formPanel.add(petComboBox, gbc);

		// VETERINARIAN

		JLabel vetLabel = new JLabel("Veterinarian:");

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;

		formPanel.add(vetLabel, gbc);

		vetComboBox = new JComboBox<>();
		vetComboBox.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(
						list, value, index, isSelected, cellHasFocus);

				if (value instanceof User) {
					User vet = (User) value;
					setText(vet.getFName() + " " + vet.getLName());
				}

				return this;
			}
		});

		try {
			List<User> vets = controller.getVeterinarians();

			for (User vet : vets) {
				vetComboBox.addItem(vet);
			}

		} catch (SQLException e) {
			ErrorHandler.handleSQLException(e);
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
		}
		
		vetComboBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        loadAvailableTimes();
		    }
		});

		gbc.gridx = 1;
		gbc.weightx = 1;

		formPanel.add(vetComboBox, gbc);

		// DATE

		JLabel dateLabel = new JLabel("Date:");

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		formPanel.add(dateLabel, gbc);

		DatePickerSettings dateSettings = new DatePickerSettings();

		Color background = UIManager.getColor("TextField.background");
		Color foreground = UIManager.getColor("TextField.foreground");
		Color panelBackground = UIManager.getColor("Panel.background");
		//Color selectionBackground = UIManager.getColor("TextField.selectionBackground");
		Color borderColor = UIManager.getColor("Component.borderColor");

		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, background);
		dateSettings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, background);
		dateSettings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates,background);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates,foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarBackgroundVetoedDates, background);
		dateSettings.setColorBackgroundWeekdayLabels(panelBackground,false);
		dateSettings.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays, foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels,panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels,foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons,panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons,foreground);
		dateSettings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel,panelBackground);
		dateSettings.setColor(DatePickerSettings.DateArea.TextTodayLabel,foreground);
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
		gbc.weightx = 1;
		formPanel.add(datePicker, gbc);


		// TIME

		JLabel timeLabel = new JLabel("Time:");

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0;
		formPanel.add(timeLabel, gbc);

		timeComboBox = new JComboBox<>();
		timeComboBox.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
					JList<?> list,
					Object value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value instanceof LocalTime) {
					LocalTime time = (LocalTime) value;
					setText(time.format(DateTimeFormatter.ofPattern("h:mm a")));
				}
				return this;
			}
		});

		gbc.gridx = 1;
		gbc.weightx = 1;
		formPanel.add(timeComboBox, gbc);


		// REASON

		JLabel reasonLabel = new JLabel("Reason:");

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;

		formPanel.add(reasonLabel, gbc);

		JTextArea reasonArea = new JTextArea(5, 20);
		reasonArea.setLineWrap(true);
		reasonArea.setWrapStyleWord(true);

		JScrollPane reasonScrollPane = new JScrollPane(reasonArea);

		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;

		formPanel.add(reasonScrollPane, gbc);

		add(formPanel, BorderLayout.CENTER);

		// BUTTONS

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton bookButton = new JButton("Book Appointment");
		bookButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bookButton.addActionListener(new ActionListener() {

		    public void actionPerformed(ActionEvent e) {

		        Pet selectedPet = (Pet) petComboBox.getSelectedItem();
		        User selectedVet = (User) vetComboBox.getSelectedItem();
		        LocalDate selectedDate = datePicker.getDate();
		        LocalTime selectedTime = (LocalTime) timeComboBox.getSelectedItem();
		        String reason = reasonArea.getText();

		        try {
		            controller.bookAppointment(user, selectedPet, selectedVet, selectedDate,  selectedTime, reason);
		            
		            changed = true;

		            JOptionPane.showMessageDialog(BookAppointmentDialog.this, "Appointment booked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
		            dispose();

		        } catch (IllegalArgumentException ex) {
		            JOptionPane.showMessageDialog(BookAppointmentDialog.this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
		        } catch (SQLException ex) {
		            ErrorHandler.handleSQLException(ex);
		        } catch (DatabaseConfigException ex) {
		            ErrorHandler.handleDatabaseConfigException(ex);
		        }
		    }
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		buttonPanel.add(bookButton);
		buttonPanel.add(Box.createHorizontalStrut(25));
		buttonPanel.add(cancelButton);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void loadAvailableTimes() {

		timeComboBox.removeAllItems();

		User selectedVet = (User) vetComboBox.getSelectedItem();
		LocalDate selectedDate = datePicker.getDate();

		if (selectedVet == null || selectedDate == null) {
			return;
		}

		try {
			List<LocalTime> availableTimes = controller.getAvailableTimes(selectedVet.getId(), selectedDate);

			for (LocalTime time : availableTimes) {

			    if (selectedDate.equals(LocalDate.now()) && !time.isAfter(LocalTime.now())) {
			        continue;
			    }

			    timeComboBox.addItem(time);
			}

		} catch (SQLException e) {
			ErrorHandler.handleSQLException(e);
		} catch (DatabaseConfigException e) {
			ErrorHandler.handleDatabaseConfigException(e);
		}
	}
}