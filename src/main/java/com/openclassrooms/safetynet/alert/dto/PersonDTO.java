package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String address,
        String city,
        String zip,
        String phone,
        String email) {}
