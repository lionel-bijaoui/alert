package com.openclassrooms.safetynet.alert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class MedicalRecordControllerIT extends IntegrationTestBase {

    static final String DATE = "1989-10-15";
    static final String FIRST_NAME = "John";
    static final String LAST_NAME = "Doe";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void addMedicalRecord_shouldReturnCreated_whenRecordIsCreated() throws Exception {
        MedicalRecordDTO dto =
                new MedicalRecordDTO(
                        "Alice",
                        "Walker",
                        LocalDate.parse(DATE),
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
                        FIRST_NAME,
                        LAST_NAME,
                        LocalDate.parse(DATE),
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
        mockMvc.perform(
                        delete("/medicalRecord")
                                .param("firstName", FIRST_NAME)
                                .param("lastName", LAST_NAME))
                .andExpect(status().isOk());
    }
}
