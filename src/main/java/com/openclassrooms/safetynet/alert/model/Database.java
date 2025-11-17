package com.openclassrooms.safetynet.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** Simple container representing the JSON database structure used by the app. */
@Data
@AllArgsConstructor
public class Database {

    public List<Person> persons;
    public List<FireStation> firestations;
    public List<MedicalRecord> medicalrecords;
}
