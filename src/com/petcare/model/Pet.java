package com.petcare.model;

import java.sql.Timestamp;

public class Pet {
	private int petId;
	private int ownerId;
	private String name;
	private String species;
	private String gender;

	private String ownerName;
	private Timestamp lastAppointment;

	public Pet(int petId, int ownerId, String name, String species, String gender) {
		this.petId = petId;
		this.ownerId = ownerId;
		this.name = name;
		this.species = species;
		this.gender = gender;
	}

	//Constructor used to pass Pet object for Patient panel (Vet UI)
	public Pet(int petId, String name, String species, String ownerName, Timestamp lastAppointment) {
		this.petId = petId;
		this.name = name;
		this.species = species;
		this.ownerName = ownerName;
		this.lastAppointment = lastAppointment;
	}

	public int getPetId() {return petId;}
	public int getOwnerId() {return ownerId;}
	public String getName() {return name;}
	public String getSpecies() {return species;}
	public String getGender() {return gender;}
	public String getOwnerName() {return ownerName;};
	public Timestamp getLastAppointment() {return lastAppointment;};

}
