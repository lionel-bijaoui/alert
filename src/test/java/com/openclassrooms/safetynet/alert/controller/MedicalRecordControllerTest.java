package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.mapper.MedicalRecordMapper;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

@WebMvcTest(MedicalRecordController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class MedicalRecordControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean MedicalRecordService medicalRecordService;

    @MockitoBean MedicalRecordMapper medicalRecordMapper;

    MedicalRecord entity;
    MedicalRecordDTO dto;

    @BeforeEach
    void setUp() {
        entity = new MedicalRecordTestBuilder().build();
        dto =
                new MedicalRecordDTO(
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getBirthdate(),
                        entity.getMedications(),
                        entity.getAllergies());
    }

    @Test
    void addMedicalRecord_shouldReturnCreatedMedicalRecord_whenCreated() throws Exception {
        when(medicalRecordMapper.toEntity(dto)).thenReturn(entity);
        when(medicalRecordService.addMedicalRecord(entity)).thenReturn(entity);
        when(medicalRecordMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(
                        post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void addMedicalRecord_shouldReturnConflict_whenRecordExists() throws Exception {
        when(medicalRecordMapper.toEntity(dto)).thenReturn(entity);
        when(medicalRecordService.addMedicalRecord(entity)).thenThrow(new ConflictException());

        mockMvc.perform(
                        post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void addMedicalRecord_shouldReturnBadRequest_whenValidationFails() throws Exception {
        MedicalRecordDTO invalidDto =
                new MedicalRecordDTO(
                        "", dto.lastName(), dto.birthdate(), dto.medications(), dto.allergies());

        mockMvc.perform(
                        post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"));
    }

    @Test
    void addMedicalRecord_shouldReturnBadRequest_whenBirthdateIsInFuture() throws Exception {
        MedicalRecordDTO invalidDto =
                new MedicalRecordDTO(
                        dto.firstName(),
                        dto.lastName(),
                        java.time.LocalDate.now().plusDays(1),
                        dto.medications(),
                        dto.allergies());

        mockMvc.perform(
                        post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("future")));
    }

    @Test
    void addMedicalRecord_shouldReturnBadRequest_whenMedicationFormatIsInvalid() throws Exception {
        MedicalRecordDTO invalidDto =
                new MedicalRecordDTO(
                        dto.firstName(),
                        dto.lastName(),
                        dto.birthdate(),
                        java.util.List.of("invalid-medication-format"),
                        dto.allergies());

        mockMvc.perform(
                        post("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("Medication")));
    }

    @Test
    void updateMedicalRecord_shouldReturnOk_whenUpdated() throws Exception {
        MedicalRecordDTO updatedDto =
                new MedicalRecordDTO(
                        dto.firstName(),
                        dto.lastName(),
                        dto.birthdate(),
                        new ArrayList<>(),
                        new ArrayList<>());
        MedicalRecord updatedEntity =
                new MedicalRecordTestBuilder()
                        .withFirstName(updatedDto.firstName())
                        .withLastName(updatedDto.lastName())
                        .withBirthdate(updatedDto.birthdate())
                        .withMedications(updatedDto.medications())
                        .withAllergies(updatedDto.allergies())
                        .build();

        when(medicalRecordMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(medicalRecordService.updateMedicalRecord(updatedEntity)).thenReturn(updatedEntity);
        when(medicalRecordMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(
                        put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateMedicalRecord_shouldReturnNotFound_whenNotExists() throws Exception {
        MedicalRecordDTO updatedDto =
                new MedicalRecordDTO(
                        dto.firstName(),
                        dto.lastName(),
                        dto.birthdate(),
                        new ArrayList<>(),
                        new ArrayList<>());
        MedicalRecord updatedEntity =
                new MedicalRecordTestBuilder()
                        .withFirstName(updatedDto.firstName())
                        .withLastName(updatedDto.lastName())
                        .withBirthdate(updatedDto.birthdate())
                        .withMedications(updatedDto.medications())
                        .withAllergies(updatedDto.allergies())
                        .build();

        when(medicalRecordMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(medicalRecordService.updateMedicalRecord(updatedEntity))
                .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
                        put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMedicalRecord_shouldReturnOk_whenDeleted() throws Exception {

        doNothing().when(medicalRecordService).deleteMedicalRecord(dto.firstName(), dto.lastName());

        mockMvc.perform(
                        delete("/medicalRecord")
                                .param("firstName", dto.firstName())
                                .param("lastName", dto.lastName()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMedicalRecord_shouldReturnNotFound_whenNotExists() throws Exception {
        doThrow(new ResourceNotFoundException("not found"))
                .when(medicalRecordService)
                .deleteMedicalRecord("Unknown", "Person");

        mockMvc.perform(
                        delete("/medicalRecord")
                                .param("firstName", "Unknown")
                                .param("lastName", "Person"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMedicalRecord_shouldReturnBadRequest_whenFirstNameParamIsMissing() throws Exception {
        mockMvc.perform(delete("/medicalRecord").param("lastName", dto.lastName()))
                .andExpect(status().isBadRequest());
    }
}
