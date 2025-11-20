package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.util.Date;
import java.util.List;

/** Immutable DTO for MedicalRecord payloads. */
public record MedicalRecordDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull @PastOrPresent(message = "Birthdate cannot be in the future") Date birthdate,
        List<String> medications,
        List<String> allergies) {}
