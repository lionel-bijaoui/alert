package com.openclassrooms.safetynet.alert.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.PersonService;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PersonController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class PersonControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean PersonService personService;

    @MockitoBean PersonMapper personMapper;

    PersonDTO dto;
    Person entity;

    @BeforeEach
    void setUp() {
        dto =
                new PersonDTO(
                        "John",
                        "Boyd",
                        "1509 Culver St",
                        "Culver",
                        "97451",
                        "841-874-6512",
                        "john.boyd@email.com");
        entity =
                new Person(
                        dto.firstName(),
                        dto.lastName(),
                        dto.address(),
                        dto.city(),
                        dto.zip(),
                        dto.phone(),
                        dto.email());
    }

    @Test
    void addPerson_shouldReturnCreatedPerson_whenPersonIsCreated() throws Exception {
        when(personMapper.toEntity(dto)).thenReturn(entity);
        when(personService.addPerson(entity)).thenReturn(entity);
        when(personMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(
                        post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(dto.firstName()))
                .andExpect(jsonPath("$.lastName").value(dto.lastName()));

        verify(personMapper, times(1)).toEntity(dto);
        verify(personService, times(1)).addPerson(entity);
        verify(personMapper, times(1)).toDto(entity);
    }

    @Test
    void addPerson_shouldReturnConflict_whenPersonAlreadyExists() throws Exception {
        when(personMapper.toEntity(dto)).thenReturn(entity);
        when(personService.addPerson(entity))
                .thenThrow(new ConflictException("Person already exists"));

        mockMvc.perform(
                        post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

        verify(personMapper, times(1)).toEntity(dto);
        verify(personService, times(1)).addPerson(entity);
        verify(personMapper, never()).toDto(any());
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson_whenPersonIsUpdated() throws Exception {
        PersonDTO updatedDto =
                new PersonDTO(
                        dto.firstName(),
                        dto.lastName(),
                        "New Address",
                        dto.city(),
                        dto.zip(),
                        dto.phone(),
                        dto.email());
        Person updatedEntity =
                new Person(
                        updatedDto.firstName(),
                        updatedDto.lastName(),
                        updatedDto.address(),
                        updatedDto.city(),
                        updatedDto.zip(),
                        updatedDto.phone(),
                        updatedDto.email());

        when(personMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(personService.updatePerson(updatedEntity)).thenReturn(updatedEntity);
        when(personMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(
                        put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());

        verify(personMapper, times(1)).toEntity(updatedDto);
        verify(personService, times(1)).updatePerson(updatedEntity);
        verify(personMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    void updatePerson_shouldReturnNotFound_whenPersonDoesNotExist() throws Exception {
        PersonDTO updatedDto =
                new PersonDTO("No", "Body", "Addr", "City", "00000", "000", "e@x.com");
        Person updatedEntity =
                new Person(
                        updatedDto.firstName(),
                        updatedDto.lastName(),
                        updatedDto.address(),
                        updatedDto.city(),
                        updatedDto.zip(),
                        updatedDto.phone(),
                        updatedDto.email());

        when(personMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(personService.updatePerson(updatedEntity))
                .thenThrow(new ResourceNotFoundException("Person not found"));

        mockMvc.perform(
                        put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());

        verify(personMapper, times(1)).toEntity(updatedDto);
        verify(personService, times(1)).updatePerson(updatedEntity);
        verify(personMapper, never()).toDto(any());
    }

    @Test
    void deletePerson_shouldReturnOk_whenPersonIsDeleted() throws Exception {
        doNothing().when(personService).deletePerson(dto.firstName(), dto.lastName());

        mockMvc.perform(
                        delete("/person")
                                .param("firstName", dto.firstName())
                                .param("lastName", dto.lastName()))
                .andExpect(status().isOk());

        verify(personService, times(1)).deletePerson(dto.firstName(), dto.lastName());
    }

    @Test
    void deletePerson_shouldReturnNotFound_whenPersonDoesNotExist() throws Exception {
        String first = "No";
        String last = "Body";
        doThrow(new ResourceNotFoundException("Person not found"))
                .when(personService)
                .deletePerson(first, last);

        mockMvc.perform(delete("/person").param("firstName", first).param("lastName", last))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).deletePerson(first, last);
    }
}
