package com.openclassrooms.safetynet.alert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PersonControllerIT {

    @Autowired JsonFileDataStore store;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        store.load();
    }

    @AfterEach
    void cleanup() throws Exception {
        Path current = Path.of(store.current());
        if (Files.exists(current)) {
            Files.delete(current);
        }
    }

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
        // Use the person present in test fixture initial.json
        PersonDTO updated =
                new PersonDTO(
                        "John",
                        "Doe",
                        "123 Updated St",
                        "Culver",
                        "97451",
                        "841-874-6512",
                        "jaboyd@email.com");

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
