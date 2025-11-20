package com.openclassrooms.safetynet.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FireStationDTO(@NotBlank String address, @NotNull int station) {}
