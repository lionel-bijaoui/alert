package com.openclassrooms.safetynet.alert.dto;

import java.util.List;

public record PopulationByFireStationsDTO(
        List<Integer> fireStationNumbers, List<PersonWithMedicalInfosDTO> population) {}
