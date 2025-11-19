package com.openclassrooms.safetynet.alert.mapper;

import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.model.FireStation;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FireStationMapper {

    FireStationDTO toDto(FireStation entity);

    FireStation toEntity(FireStationDTO dto);
}
