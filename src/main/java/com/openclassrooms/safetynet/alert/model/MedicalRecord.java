package com.openclassrooms.safetynet.alert.model;

import lombok.Data;

import java.util.Date;

/**
 * Model representing a person's medical record.
 */
@Data
public class MedicalRecord {

    private String firstName;
    private String lastName;
    private Date birthdate;
    /**
     * Example: <pre>["aznol:350mg", "hydrapermazol:100mg"]</pre>
     */
    private String[] medications;
    /**
     * Example: <pre>["nillacilan"]</pre>
     */
    private String[] allergies;

}
