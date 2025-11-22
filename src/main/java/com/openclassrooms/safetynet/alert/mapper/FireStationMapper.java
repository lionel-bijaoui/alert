package com.openclassrooms.safetynet.alert.mapper;

import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.model.FireStation;

import org.mapstruct.Mapper;

/** Mapper interface for converting between FireStation entities and FireStationDTOs. */
@Mapper(componentModel = "spring")
public interface FireStationMapper {

    FireStationDTO toDto(FireStation entity);

    FireStation toEntity(FireStationDTO dto);
}
