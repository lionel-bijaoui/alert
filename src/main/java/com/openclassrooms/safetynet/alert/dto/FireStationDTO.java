package com.openclassrooms.safetynet.alert.dto;

import org.springframework.lang.NonNull;

public record FireStationDTO(@NonNull String address, int station) {}
