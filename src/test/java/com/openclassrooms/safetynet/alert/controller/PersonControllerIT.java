package com.openclassrooms.safetynet.alert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PersonControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @Test
    void addPerson_shouldReturnCreated_whenPersonIsCreated() throws Exception {
        PersonDTO dto =
                new PersonDTO("New", "Person", "123 New St", "C", "00000", "000", "n@p.com");

        mockMvc.perform(
                        post("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(dto.firstName()))
                .andExpect(jsonPath("$.lastName").value(dto.lastName()));
    }

    @Test
    void updatePerson_shouldReturnUpdated_whenPersonIsUpdated() throws Exception {
        Person updatedPerson = new PersonTestBuilder().withAddress("123 Updated St").build();
        PersonDTO updated =
                new PersonDTO(
                        updatedPerson.getFirstName(),
                        updatedPerson.getLastName(),
                        updatedPerson.getAddress(),
                        updatedPerson.getCity(),
                        updatedPerson.getZip(),
                        updatedPerson.getPhone(),
                        updatedPerson.getEmail());

        mockMvc.perform(
                        put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(updated.address()));
    }

    @Test
    void deletePerson_shouldReturnOk_whenPersonIsDeleted() throws Exception {
        String first = "John";
        String last = "Doe";

        mockMvc.perform(delete("/person").param("firstName", first).param("lastName", last))
                .andExpect(status().isOk());
    }
}
