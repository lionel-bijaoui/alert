package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Data Transfer Object for Fire Station information. */
public record FireStationDTO(@NotBlank String address, @NotNull int station) {}
