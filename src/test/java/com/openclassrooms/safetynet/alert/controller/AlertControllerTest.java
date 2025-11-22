package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.PopulationService;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(AlertController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class AlertControllerTest {
    @Autowired MockMvc mockMvc;

    @MockitoBean FireStationService fireStationService;

    @MockitoBean PopulationService populationService;

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists()
                    throws Exception {
        Integer fireStationNumber = 1;
        String fireStationAddress = "1509 Culver St";
        List<String> fireStationList = List.of(fireStationAddress);
        List<Person> personList =
                List.of(
                        new Person(
                                "John",
                                "Doe",
                                "1509 Culver St",
                                "Culver",
                                "97451",
                                "841-874-6512",
                                "johndoe@email.com"));
        when(fireStationService.getFireStationAddressListByFireStationNumber(fireStationNumber))
                .thenReturn(fireStationList);
        when(populationService.getPersonListByAddressList(fireStationList)).thenReturn(personList);

        mockMvc.perform(
                        get("/phoneAlert")
                                .param("firestation", String.valueOf(fireStationNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]", containsInAnyOrder("841-874-6512")));

        verify(fireStationService, times(1))
                .getFireStationAddressListByFireStationNumber(fireStationNumber);
        verify(populationService, times(1)).getPersonListByAddressList(fireStationList);
    }

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnEmptyList_whenFireStationNumberDoesNotExist()
                    throws Exception {
        Integer fireStationNumber = 99;
        List<String> fireStationList = List.of();
        when(fireStationService.getFireStationAddressListByFireStationNumber(fireStationNumber))
                .thenReturn(fireStationList);
        when(populationService.getPersonListByAddressList(fireStationList)).thenReturn(List.of());

        mockMvc.perform(
                        get("/phoneAlert")
                                .param("firestation", String.valueOf(fireStationNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(fireStationService, times(1))
                .getFireStationAddressListByFireStationNumber(fireStationNumber);
        verify(populationService, times(1)).getPersonListByAddressList(fireStationList);
    }
}
