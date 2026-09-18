package com.petcare;
import com.petcare.ui.LoginFrame;

import com.formdev.flatlaf.*;

public class PetCareSys {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlatDarkLaf.setup();
		new LoginFrame().setVisible(true);
	}

}
