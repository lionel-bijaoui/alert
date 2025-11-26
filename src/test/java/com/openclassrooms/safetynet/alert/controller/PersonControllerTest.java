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
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
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

    Person entity;
    PersonDTO dto;

    @BeforeEach
    void setUp() {
        entity = new PersonTestBuilder().build();
        dto =
                new PersonDTO(
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getAddress(),
                        entity.getCity(),
                        entity.getZip(),
                        entity.getPhone(),
                        entity.getEmail());
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
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson_whenPersonIsUpdated() throws Exception {
        Person updatedEntity = new PersonTestBuilder().withAddress("New Address").build();
        PersonDTO updatedDto =
                new PersonDTO(
                        updatedEntity.getFirstName(),
                        updatedEntity.getLastName(),
                        updatedEntity.getAddress(),
                        updatedEntity.getCity(),
                        updatedEntity.getZip(),
                        updatedEntity.getPhone(),
                        updatedEntity.getEmail());

        when(personMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(personService.updatePerson(updatedEntity)).thenReturn(updatedEntity);
        when(personMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(
                        put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePerson_shouldReturnNotFound_whenPersonDoesNotExist() throws Exception {
        Person updatedEntity =
                new PersonTestBuilder()
                        .withFirstName("Nobody")
                        .withLastName("Here")
                        .withAddress("Nowhere")
                        .build();
        PersonDTO updatedDto =
                new PersonDTO(
                        updatedEntity.getFirstName(),
                        updatedEntity.getLastName(),
                        updatedEntity.getAddress(),
                        updatedEntity.getCity(),
                        updatedEntity.getZip(),
                        updatedEntity.getPhone(),
                        updatedEntity.getEmail());

        when(personMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(personService.updatePerson(updatedEntity))
                .thenThrow(new ResourceNotFoundException("Person not found"));

        mockMvc.perform(
                        put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePerson_shouldReturnOk_whenPersonIsDeleted() throws Exception {
        doNothing().when(personService).deletePerson(dto.firstName(), dto.lastName());

        mockMvc.perform(
                        delete("/person")
                                .param("firstName", dto.firstName())
                                .param("lastName", dto.lastName()))
                .andExpect(status().isOk());
    }

    @Test
    void deletePerson_shouldReturnNotFound_whenPersonDoesNotExist() throws Exception {
        String firstName = "No";
        String lastName = "Body";
        doThrow(new ResourceNotFoundException("Person not found"))
                .when(personService)
                .deletePerson(firstName, lastName);

        mockMvc.perform(delete("/person").param("firstName", firstName).param("lastName", lastName))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).deletePerson(firstName, lastName);
    }
}
