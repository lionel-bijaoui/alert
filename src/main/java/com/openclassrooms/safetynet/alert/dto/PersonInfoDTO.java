package com.openclassrooms.safetynet.alert.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public record PersonInfoDTO(
        @Nonnull String firstName,
        @Nonnull String lastName,
        @Nonnull String address,
        @Nonnull String email,
        @Nullable Integer age,
        List<String> medications,
        List<String> allergies) {}
