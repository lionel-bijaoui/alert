package com.openclassrooms.safetynet.alert.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

/** Data Transfer Object for Person Information including medical details and age. */
public record PersonWithMedicalInfosDTO(
        @Nonnull String firstName,
        @Nonnull String lastName,
        @Nonnull String address,
        @Nullable String phone,
        @Nullable String email,
        @Nullable Integer age,
        List<String> medications,
        List<String> allergies) {}
