package com.petcare.view.petOwnerFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.ComboPopup;

import com.petcare.controller.PetOwnerController;
import com.petcare.exception.DatabaseConfigException;
import com.petcare.model.Pet;
import com.petcare.util.ErrorHandler;

public class EditPetDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private PetOwnerController controller;
	private boolean changed;

	public boolean isChanged() {
		return changed;
	}

	public EditPetDialog(Pet pet) {

		controller = new PetOwnerController();
		changed = false;

		setTitle("Edit Pet");
		setSize(350, 250);
		setLocationRelativeTo(null);
		setModal(true);

		// FORM
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

		GridBagConstraints gbc = new GridBagConstraints(); 
		gbc.insets = new Insets(5, 5, 5, 5); 
		gbc.anchor = GridBagConstraints.WEST;

		JLabel nameLabel = new JLabel("Pet Name:");
		JTextField nameField = new JTextField(pet.getName(), 15);

		JLabel speciesLabel = new JLabel("Species:");

		String[] species = {"Dog", "Cat", "Squirrel", "Hamster", "Other"}; //other should open a new textfield
		JComboBox<String> speciesComboBox = new JComboBox<>(species);

		JLabel otherSpeciesLabel = new JLabel("Other Species:");
		otherSpeciesLabel.setVisible(false);

		JTextField otherSpeciesField = new JTextField(15);
		otherSpeciesField.setVisible(false);

		String petSpecies = pet.getSpecies();
		if (petSpecies.equals("Dog") || petSpecies.equals("Cat") || petSpecies.equals("Squirrel") || petSpecies.equals("Hamster")) { 
			speciesComboBox.setSelectedItem(petSpecies); 
		} else { 
			speciesComboBox.setSelectedItem("Other"); 
			otherSpeciesField.setText(petSpecies); 
			otherSpeciesLabel.setVisible(true); 
			otherSpeciesField.setVisible(true); 
		}

		JLabel genderLabel = new JLabel("Gender:");

		String[] genders = {"Male", "Female"};
		JComboBox<String> genderComboBox = new JComboBox<>(genders);
		genderComboBox.setSelectedItem(pet.getGender());

		gbc.gridx = 0;
		gbc.gridy = 0; 
		formPanel.add(nameLabel, gbc); 

		gbc.gridx = 1; 
		formPanel.add(nameField, gbc); 

		gbc.gridx = 0; 
		gbc.gridy = 1; 
		formPanel.add(speciesLabel, gbc); 

		gbc.gridx = 1; 
		formPanel.add(speciesComboBox, gbc); 

		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(otherSpeciesLabel, gbc);

		gbc.gridx = 1;
		formPanel.add(otherSpeciesField, gbc);

		gbc.gridx = 0; 
		gbc.gridy = 3; 
		formPanel.add(genderLabel, gbc); 

		gbc.gridx = 1; 
		formPanel.add(genderComboBox, gbc); 

		add(formPanel, BorderLayout.CENTER);

		speciesComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if ("Other".equals(speciesComboBox.getSelectedItem())) {
					otherSpeciesLabel.setVisible(true);
					otherSpeciesField.setVisible(true);
				} else {
					otherSpeciesLabel.setVisible(false);
					otherSpeciesField.setVisible(false);
				}

				formPanel.revalidate();
				formPanel.repaint();
			}
		});

		// BUTTONS
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton deleteButton = new JButton("Delete Pet");
		JButton cancelButton = new JButton("Cancel");
		JButton saveButton = new JButton("Save Changes");

		buttonPanel.add(deleteButton);
		buttonPanel.add(Box.createHorizontalStrut(45));
		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);

		deleteButton.setBackground(new Color(120, 45, 45));
		deleteButton.setForeground(Color.WHITE);

		deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		setComboBoxHandCursor(speciesComboBox); 
		setComboBoxHandCursor(genderComboBox);

		add(buttonPanel, BorderLayout.SOUTH);

		// DELETE
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int result = JOptionPane.showConfirmDialog(EditPetDialog.this, "Are you sure you want to delete " + pet.getName() + "?", "Delete Pet", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

				if (result == JOptionPane.YES_OPTION) {

					try { 
						controller.deletePet(pet);

						changed = true;

						JOptionPane.showMessageDialog( EditPetDialog.this, "Pet deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
						dispose();

					} catch (SQLIntegrityConstraintViolationException ex) {

						JOptionPane.showMessageDialog(EditPetDialog.this, "This pet cannot be deleted because it has existing appointments.", "Cannot Delete Pet", JOptionPane.WARNING_MESSAGE);
						//both completed and scheduled appointments are treated this way
						//best option: soft delete?(isActive = 0)
					} catch (SQLException ex) { 
						ErrorHandler.handleSQLException(ex);
					} catch (DatabaseConfigException ex) { 
						ErrorHandler.handleDatabaseConfigException(ex);
					}
				}
			}
		});

		// CANCEL
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// SAVE
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = nameField.getText().trim();
				String species = (String) speciesComboBox.getSelectedItem();
				String otherSpecies = otherSpeciesField.getText().trim();
				String gender = (String) genderComboBox.getSelectedItem();

				try {
					controller.updatePet(pet, name, species, otherSpecies, gender);

					changed = true;

					JOptionPane.showMessageDialog(EditPetDialog.this, "Pet updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
					dispose();

				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(EditPetDialog.this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException ex) {
					ErrorHandler.handleSQLException(ex);
				} catch (DatabaseConfigException ex) {
					ErrorHandler.handleDatabaseConfigException(ex);
				}
			}
		});
	}

	private void setComboBoxHandCursor(JComboBox<String> comboBox) {

	    comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    comboBox.addPopupMenuListener(new PopupMenuListener() {

	        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
	            Object popup = comboBox.getUI().getAccessibleChild(comboBox, 0);

	            if (popup instanceof ComboPopup) {
	                JList<?> list = ((ComboPopup) popup).getList();
	                list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	            }
	        }
	        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
	        public void popupMenuCanceled(PopupMenuEvent e) {}
	    });
	}
}
