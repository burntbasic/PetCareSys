package com.petcare;

import com.formdev.flatlaf.*;
import com.petcare.view.LoginFrame;

public class PetCareSys {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlatLightLaf.setup();
		new LoginFrame().setVisible(true);
	}

}
