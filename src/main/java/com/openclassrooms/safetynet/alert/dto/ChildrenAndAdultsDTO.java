package com.openclassrooms.safetynet.alert.dto;

import java.util.List;

/** Data Transfer Object for children and adults information. */
public record ChildrenAndAdultsDTO(List<ChildDTO> children, List<PersonDTO> adults) {}
