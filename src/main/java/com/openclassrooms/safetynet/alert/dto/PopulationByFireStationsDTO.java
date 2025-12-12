package com.openclassrooms.safetynet.alert.dto;

import java.util.List;

/** Data Transfer Object for population information by fire stations. */
public record PopulationByFireStationsDTO(
        List<Integer> fireStationNumbers, List<PersonWithMedicalInfosDTO> population) {}
