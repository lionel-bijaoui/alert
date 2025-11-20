package com.openclassrooms.safetynet.alert.mapper;

import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.model.Person;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonDTO toDto(Person entity);

    Person toEntity(PersonDTO dto);
}
