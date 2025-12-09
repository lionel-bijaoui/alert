package com.openclassrooms.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Simple container representing the JSON database structure used by the app. */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Database {

    @JsonProperty("persons")
    private List<Person> persons = new ArrayList<>();

    @JsonProperty("firestations")
    private List<FireStation> fireStations = new ArrayList<>();

    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    public void setPersons(List<Person> persons) {
        this.persons = persons != null ? persons : new ArrayList<>();
    }

    public void setFireStations(List<FireStation> fireStations) {
        this.fireStations = fireStations != null ? fireStations : new ArrayList<>();
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords != null ? medicalRecords : new ArrayList<>();
    }
}
