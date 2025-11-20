package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String zip,
        @NotBlank String phone,
        @NotBlank String email) {}
