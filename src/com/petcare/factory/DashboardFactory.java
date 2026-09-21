package com.petcare.factory;

import javax.swing.JFrame;

import com.petcare.model.User;
import com.petcare.view.adminFrames.AdminDashboard;
import com.petcare.view.petOwnerFrames.PetOwnerDashboard;
import com.petcare.view.vetFrames.VetDashboard;

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