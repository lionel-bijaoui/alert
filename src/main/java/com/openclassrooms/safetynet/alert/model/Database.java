package com.openclassrooms.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Simple container representing the JSON database structure used by the app. */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Database {

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public List<Person> persons = new ArrayList<>();

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public List<FireStation> firestations = new ArrayList<>();

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public List<MedicalRecord> medicalrecords = new ArrayList<>();
}
