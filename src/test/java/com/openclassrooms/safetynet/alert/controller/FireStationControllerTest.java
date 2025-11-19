package com.openclassrooms.safetynet.alert.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.mapper.FireStationMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.service.FireStationService;
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

    FireStationDTO dto;
    FireStation entity;

    @BeforeEach
    void setUp() {
        dto = new FireStationDTO("1509 Culver St", 3);
        entity = new FireStation(dto.address(), String.valueOf(dto.station()));
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
                .andExpect(
                        result -> {
                            String responseBody = result.getResponse().getContentAsString();
                            FireStationDTO responseDto =
                                    objectMapper.readValue(responseBody, FireStationDTO.class);
                            assertEquals(dto.address(), responseDto.address());
                            assertEquals(dto.station(), responseDto.station());
                        });

        verify(fireStationMapper, times(1)).toEntity(dto);
        verify(fireStationService, times(1)).addFireStation(entity);
        verify(fireStationMapper, times(1)).toDto(entity);
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

        verify(fireStationMapper, times(1)).toEntity(dto);
        verify(fireStationService, times(1)).addFireStation(entity);
        verify(fireStationMapper, never()).toDto(any());
    }

    @Test
    void updateFireStation_shouldReturnUpdatedFireStation_whenFireStationIsUpdated()
            throws Exception {
        FireStationDTO updatedDto = new FireStationDTO(dto.address(), 4);
        FireStation updatedEntity =
                new FireStation(updatedDto.address(), String.valueOf(updatedDto.station()));

        when(fireStationMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(fireStationService.updateFireStation(updatedEntity)).thenReturn(updatedEntity);
        when(fireStationMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        mockMvc.perform(
                        put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());

        verify(fireStationMapper, times(1)).toEntity(updatedDto);
        verify(fireStationService, times(1)).updateFireStation(updatedEntity);
        verify(fireStationMapper, times(1)).toDto(updatedEntity);
    }

    @Test
    void updateFireStation_shouldReturnServerError_whenFireStationDoesNotExist() throws Exception {
        FireStationDTO updatedDto = new FireStationDTO(dto.address(), 4);
        FireStation updatedEntity =
                new FireStation(updatedDto.address(), String.valueOf(updatedDto.station()));

        when(fireStationMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(fireStationService.updateFireStation(updatedEntity))
                .thenThrow(new ResourceNotFoundException("FireStation not found"));

        mockMvc.perform(
                        put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isNotFound());

        verify(fireStationMapper, times(1)).toEntity(updatedDto);
        verify(fireStationService, times(1)).updateFireStation(updatedEntity);
        verify(fireStationMapper, never()).toDto(any());
    }

    @Test
    void deleteFireStation_shouldReturnOk_whenFireStationIsDeleted() throws Exception {
        doNothing().when(fireStationService).deleteFireStation(dto.address());

        mockMvc.perform(delete("/firestation").param("address", dto.address()))
                .andExpect(status().isOk());

        verify(fireStationService, times(1)).deleteFireStation(dto.address());
    }

    @Test
    void deleteFireStation_shouldReturnServerError_whenFireStationDoesNotExist() throws Exception {
        String address = "Unknown Address 123";
        doThrow(new ResourceNotFoundException("FireStation not found"))
                .when(fireStationService)
                .deleteFireStation(address);

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isNotFound());

        verify(fireStationService, times(1)).deleteFireStation(address);
    }
}
