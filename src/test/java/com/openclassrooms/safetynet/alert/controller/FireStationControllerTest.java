package com.openclassrooms.safetynet.alert.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.mapper.FireStationMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FireStationController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class FireStationControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @MockitoBean FireStationService fireStationService;

    @MockitoBean FireStationMapper fireStationMapper;

    @MockitoBean PopulationService populationService;

    @MockitoBean MedicalRecordService medicalRecordService;

    FireStation entity;
    FireStationDTO dto;

    @BeforeEach
    void setUp() {
        entity = new FireStationTestBuilder().build();
        dto = new FireStationDTO(entity.getAddress(), entity.getStation());
    }

    @Test
    void addFireStation_shouldReturnCreatedFireStation_whenFireStationIsCreated() throws Exception {
        when(fireStationMapper.toEntity(dto)).thenReturn(entity);
        when(fireStationService.addFireStation(entity)).thenReturn(entity);
        when(fireStationMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(
                        post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value(dto.address()))
                .andExpect(jsonPath("$.station").value(dto.station()));
    }

    @Test
    void addFireStation_shouldReturnServerError_whenFireStationAlreadyExists() throws Exception {
        when(fireStationMapper.toEntity(dto)).thenReturn(entity);
        when(fireStationService.addFireStation(entity))
                .thenThrow(new ConflictException("FireStation already exists"));

        mockMvc.perform(
                        post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateFireStation_shouldReturnUpdatedFireStation_whenFireStationIsUpdated()
            throws Exception {
        FireStationDTO updatedDto = new FireStationDTO(dto.address(), 4);
        FireStation updatedEntity =
                new FireStationTestBuilder()
                        .withAddress(updatedDto.address())
                        .withStation(updatedDto.station())
                        .build();

        when(fireStationMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(fireStationService.updateFireStation(updatedEntity)).thenReturn(updatedEntity);
        when(fireStationMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(
                        put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateFireStation_shouldReturnServerError_whenFireStationDoesNotExist() throws Exception {
        FireStationDTO updatedDto = new FireStationDTO(dto.address(), 4);
        FireStation updatedEntity =
                new FireStationTestBuilder()
                        .withAddress(updatedDto.address())
                        .withStation(updatedDto.station())
                        .build();

        when(fireStationMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(fireStationService.updateFireStation(updatedEntity))
                .thenThrow(new ResourceNotFoundException("FireStation not found"));

        mockMvc.perform(
                        put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFireStation_shouldReturnOk_whenFireStationIsDeleted() throws Exception {
        doNothing().when(fireStationService).deleteFireStation(dto.address());

        mockMvc.perform(delete("/firestation").param("address", dto.address()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFireStation_shouldReturnServerError_whenFireStationDoesNotExist() throws Exception {
        String address = "Unknown Address 123";
        doThrow(new ResourceNotFoundException("FireStation not found"))
                .when(fireStationService)
                .deleteFireStation(address);

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isNotFound());
    }
}
