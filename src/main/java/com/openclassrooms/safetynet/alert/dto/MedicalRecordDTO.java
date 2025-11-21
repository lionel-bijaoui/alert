package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

/** Immutable DTO for MedicalRecord payloads. */
public record MedicalRecordDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @PastOrPresent(message = "Birthdate cannot be in the future") LocalDate birthdate,
        List<
                        @Pattern(
                                regexp = "^[\\w\\s]+:\\d+(\\.\\d+)?\\w+$",
                                message =
                                        "Medication must be in format 'name:dosageunit' (e.g., 'aznol:350mg', 'insulin:5units')")
                        String>
                medications,
        List<@NotBlank String> allergies) {}
