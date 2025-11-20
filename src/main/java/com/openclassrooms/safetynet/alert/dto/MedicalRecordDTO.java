package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

/** Immutable DTO for MedicalRecord payloads. */
public record MedicalRecordDTO(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull Date birthdate,
        List<String> medications,
        List<String> allergies) {}
