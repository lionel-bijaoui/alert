package com.openclassrooms.safetynet.alert.dto;

import java.util.List;

public record PopulationByFireStationDTO(
        List<PersonSummaryDTO> population, int adultCount, int childCount) {}
