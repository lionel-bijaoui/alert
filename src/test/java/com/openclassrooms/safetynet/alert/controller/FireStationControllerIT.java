package com.openclassrooms.safetynet.alert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class FireStationControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @Test
    void addFireStation_shouldReturnCreated_whenFireStationIsCreated() throws Exception {
        FireStationDTO dto = new FireStationDTO("New address", 6);

        mockMvc.perform(
                        post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value(dto.address()))
                .andExpect(jsonPath("$.station").value(dto.station()));
    }

    @Test
    void updateFireStation_shouldReturnUpdated_whenFireStationIsUpdated() throws Exception {
        FireStationDTO dto = new FireStationDTO("1509 Culver St", 4);

        mockMvc.perform(
                        put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(dto.address()))
                .andExpect(jsonPath("$.station").value(dto.station()));
    }

    @Test
    void deleteFireStation_shouldReturnOk_whenFireStationIsDeleted() throws Exception {
        String address = "1509 Culver St";

        mockMvc.perform(delete("/firestation").param("address", address))
                .andExpect(status().isOk());
    }

    @Test
    void getPersonListByStationNumber_shouldReturnPersonList_whenStationNumberIsValid()
            throws Exception {
        int stationNumber = 3;

        mockMvc.perform(get("/firestation").param("stationNumber", String.valueOf(stationNumber)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.population").isArray())
                .andExpect(jsonPath("$.population.length()").value(1))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childCount").value(0));
    }
}
