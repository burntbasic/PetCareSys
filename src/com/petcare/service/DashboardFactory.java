package com.petcare.service;
import com.petcare.model.User;
import com.petcare.ui.adminFrames.AdminDashboard;
import com.petcare.ui.petOwnerFrames.PetOwnerDashboard;
import com.petcare.ui.vetFrames.VetDashboard;

import javax.swing.JFrame;

public class DashboardFactory {
	public static JFrame createDashboard(User user) {
		//static used to reference method without object creation
		if(user.getRole().equals("PET_OWNER")) {
			return new PetOwnerDashboard(user);
		}
		else if(user.getRole().equals("VET")) {
			return new VetDashboard(user);
		}
		else if(user.getRole().equals("ADMIN")) {
			return new AdminDashboard(user);
		}
		throw new IllegalArgumentException("Unknown user role");
		// This exception should not occur because the user's role is restricted by the database ENUM.
		// It is included as a safeguard in the odd case an unexpected role is encountered.
	}
}