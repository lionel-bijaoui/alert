package com.openclassrooms.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/** Model representing a person's medical record. */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MedicalRecord {

    private String firstName;
    private String lastName;
    private @JsonFormat(pattern = "MM/dd/yyyy") LocalDate birthdate;

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
