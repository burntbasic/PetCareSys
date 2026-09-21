package com.petcare.model;

public class Pet {
	private int petId;
    private int ownerId;
    private String name;
    private String species;
    private String gender;

    public Pet(int petId, int ownerId, String name, String species, String gender) {
        this.petId = petId;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.gender = gender;
    }

    public int getPetId() {return petId;}
    public int getOwnerId() {return ownerId;}
    public String getName() {return name;}
    public String getSpecies() {return species;}
    public String getGender() {return gender;}

}
