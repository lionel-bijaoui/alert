package com.openclassrooms.safetynet.alert.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/** Model representing a person's medical record. */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MedicalRecord {

    private String firstName;
    private String lastName;
    private Date birthdate;

    /**
     * Example:
     *
     * <pre>["aznol:350mg", "hydrapermazol:100mg"]</pre>
     */
    private List<String> medications;

    /**
     * Example:
     *
     * <pre>["nillacilan"]</pre>
     */
    private List<String> allergies;
}
