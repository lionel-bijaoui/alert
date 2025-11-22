package com.openclassrooms.safetynet.alert.mapper;

import com.openclassrooms.safetynet.alert.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;

import org.mapstruct.Mapper;

/** Mapper interface for converting between MedicalRecord entities and MedicalRecordDTOs. */
@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    MedicalRecordDTO toDto(MedicalRecord entity);

    MedicalRecord toEntity(MedicalRecordDTO dto);
}
