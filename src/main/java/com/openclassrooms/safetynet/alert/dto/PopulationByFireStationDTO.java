package com.openclassrooms.safetynet.alert.dto;

import java.util.List;

/**
 * Data Transfer Object for population information by fire station, including counts of adults and
 * children.
 */
public record PopulationByFireStationDTO(
        List<PersonSummaryDTO> population, int adultCount, int childCount) {}
