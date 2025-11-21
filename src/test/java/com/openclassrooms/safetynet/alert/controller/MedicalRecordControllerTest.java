package com.openclassrooms.safetynet.alert.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.mapper.MedicalRecordMapper;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

@WebMvcTest(MedicalRecordController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class MedicalRecordControllerTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean MedicalRecordService medicalRecordService;

    @MockitoBean MedicalRecordMapper medicalRecordMapper;

    MedicalRecordDTO dto;
    MedicalRecord entity;

    @BeforeEach
    void setUp() {
        dto =
                new MedicalRecordDTO(
                        FIRST_NAME,
                        LAST_NAME,
                        LocalDate.parse("1989-10-15"),
                        new ArrayList<>(),
                        new ArrayList<>());
        entity =
                new MedicalRecord(
                        dto.firstName(),
                        dto.lastName(),
                        dto.birthdate(),
                        dto.medications(),
                        dto.allergies());
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

        verify(medicalRecordMapper, times(1)).toEntity(dto);
        verify(medicalRecordService, times(1)).addMedicalRecord(entity);
        verify(medicalRecordMapper, times(1)).toDto(entity);
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

        verify(medicalRecordMapper, times(1)).toEntity(dto);
        verify(medicalRecordService, times(1)).addMedicalRecord(entity);
        verify(medicalRecordMapper, never()).toDto(any());
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
                new MedicalRecord(
                        updatedDto.firstName(),
                        updatedDto.lastName(),
                        updatedDto.birthdate(),
                        updatedDto.medications(),
                        updatedDto.allergies());

        when(medicalRecordMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(medicalRecordService.updateMedicalRecord(updatedEntity)).thenReturn(updatedEntity);
        when(medicalRecordMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(
                        put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());

        verify(medicalRecordMapper, times(1)).toEntity(updatedDto);
        verify(medicalRecordService, times(1)).updateMedicalRecord(updatedEntity);
        verify(medicalRecordMapper, times(1)).toDto(updatedEntity);
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
                new MedicalRecord(
                        updatedDto.firstName(),
                        updatedDto.lastName(),
                        updatedDto.birthdate(),
                        updatedDto.medications(),
                        updatedDto.allergies());

        when(medicalRecordMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(medicalRecordService.updateMedicalRecord(updatedEntity))
                .thenThrow(new ResourceNotFoundException());

        mockMvc.perform(
                        put("/medicalRecord")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());

        verify(medicalRecordMapper, times(1)).toEntity(updatedDto);
        verify(medicalRecordService, times(1)).updateMedicalRecord(updatedEntity);
        verify(medicalRecordMapper, never()).toDto(any());
    }

    @Test
    void deleteMedicalRecord_shouldReturnOk_whenDeleted() throws Exception {
        doNothing().when(medicalRecordService).deleteMedicalRecord(FIRST_NAME, LAST_NAME);

        mockMvc.perform(
                        delete("/medicalRecord")
                                .param("firstName", FIRST_NAME)
                                .param("lastName", LAST_NAME))
                .andExpect(status().isOk());

        verify(medicalRecordService, times(1)).deleteMedicalRecord(FIRST_NAME, LAST_NAME);
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

        verify(medicalRecordService, times(1)).deleteMedicalRecord("Unknown", "Person");
    }
}
