package com.openclassrooms.safetynet.alert.utils;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordTestBuilder {
    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate birthdate = LocalDate.of(1984, 3, 18);
    private List<String> medications = List.of("aznol:350mg", "hydrapermazol:100mg");
    private List<String> allergies = List.of("nillacilan");

    public MedicalRecordTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public MedicalRecordTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public MedicalRecordTestBuilder withBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public MedicalRecordTestBuilder withMedications(List<String> medications) {
        this.medications = medications;
        return this;
    }

    public MedicalRecordTestBuilder withAllergies(List<String> allergies) {
        this.allergies = allergies;
        return this;
    }

    public MedicalRecord build() {
        return new MedicalRecord(firstName, lastName, birthdate, medications, allergies);
    }
}
