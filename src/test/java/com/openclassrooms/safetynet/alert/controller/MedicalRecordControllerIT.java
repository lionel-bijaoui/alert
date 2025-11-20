package com.openclassrooms.safetynet.alert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.MedicalRecordDTO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class MedicalRecordControllerIT {

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
    void addMedicalRecord_shouldReturnCreated_whenRecordIsCreated() throws Exception {
        MedicalRecordDTO dto =
                new MedicalRecordDTO(
                        "Alice",
                        "Walker",
                        new SimpleDateFormat("dd/MM/yyyy").parse("15/10/1989"),
                        new ArrayList<>(),
                        new ArrayList<>());

        mockMvc.perform(
                        post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(dto.firstName()))
                .andExpect(jsonPath("$.lastName").value(dto.lastName()));
    }

    @Test
    void updateMedicalRecord_shouldReturnUpdated_whenRecordIsUpdated() throws Exception {
        MedicalRecordDTO dto =
                new MedicalRecordDTO(
                        "John",
                        "Doe",
                        new SimpleDateFormat("dd/MM/yyyy").parse("15/10/1989"),
                        new ArrayList<>(),
                        new ArrayList<>());

        mockMvc.perform(
                        put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(dto.firstName()))
                .andExpect(jsonPath("$.lastName").value(dto.lastName()));
    }

    @Test
    void deleteMedicalRecord_shouldReturnOk_whenRecordIsDeleted() throws Exception {
        String firstName = "John";
        String lastName = "Doe";

        mockMvc.perform(
                        delete("/medicalRecord")
                                .param("firstName", firstName)
                                .param("lastName", lastName))
                .andExpect(status().isOk());
    }
}
