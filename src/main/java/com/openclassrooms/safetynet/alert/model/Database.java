package com.openclassrooms.safetynet.alert.model;

import lombok.Data;

import java.util.List;

/**
 * Simple container representing the JSON database structure used by the app.
 */
@Data
public class Database {

    public List<Person> persons;
    public List<FireStation> firestations;
    public List<MedicalRecord> medicalrecords;

}
