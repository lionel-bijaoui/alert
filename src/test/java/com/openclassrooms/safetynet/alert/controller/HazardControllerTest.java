package com.openclassrooms.safetynet.alert.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationsDTO;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.PopulationService;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

@WebMvcTest(HazardController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class HazardControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean PopulationService populationService;

    @Test
    void
            getHouseholdsByFireStationNumberList_shouldReturnHouseholdsList_whenFireStationNumberListExists()
                    throws Exception {
        Person person = new PersonTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();

        PersonWithMedicalInfosDTO personWithMedicalInfosDTO =
                new PersonWithMedicalInfosDTO(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getPhone(),
                        person.getEmail(),
                        30,
                        medicalRecord.getMedications(),
                        medicalRecord.getAllergies());
        when(populationService.getHouseholdsByFireStationNumberList(anyList()))
                .thenReturn(Map.of(person.getAddress(), List.of(personWithMedicalInfosDTO)));

        String base = "$['" + person.getAddress() + "'][0]";

        mockMvc.perform(
                        get("/flood/stations")
                                .param("stations", "1", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(base + ".firstName").value(person.getFirstName()))
                .andExpect(jsonPath(base + ".lastName").value(person.getLastName()))
                .andExpect(jsonPath(base + ".address").value(person.getAddress()))
                .andExpect(jsonPath(base + ".phone").value(person.getPhone()))
                .andExpect(jsonPath(base + ".email").value(person.getEmail()))
                .andExpect(jsonPath(base + ".age").value(30))
                .andExpect(
                        jsonPath(base + ".medications[0]")
                                .value(medicalRecord.getMedications().getFirst()))
                .andExpect(
                        jsonPath(base + ".allergies[0]")
                                .value(medicalRecord.getAllergies().getFirst()));
    }

    @Test
    void
            getHouseholdsByFireStationNumberList_shouldReturnEmptyList_whenFireStationNumberListDoesNotExist()
                    throws Exception {
        when(populationService.getHouseholdsByFireStationNumberList(anyList()))
                .thenReturn(Map.of());

        mockMvc.perform(
                        get("/flood/stations")
                                .param("stations", "99")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getHouseholdsByFireStationNumberList_shouldThrowException_whenNoStationNumberProvided()
            throws Exception {
        mockMvc.perform(get("/flood/stations").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void
            getPersonAndFireStationListByAddress_shouldReturnPersonAndFireStationList_whenAddressExists()
                    throws Exception {
        FireStation fireStation = new FireStationTestBuilder().build();
        Person person = new PersonTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        PopulationByFireStationsDTO populationByFireStationsDTO =
                new PopulationByFireStationsDTO(
                        List.of(fireStation.getStation()),
                        List.of(
                                new PersonWithMedicalInfosDTO(
                                        person.getFirstName(),
                                        person.getLastName(),
                                        person.getAddress(),
                                        person.getPhone(),
                                        person.getEmail(),
                                        30,
                                        medicalRecord.getMedications(),
                                        medicalRecord.getAllergies())));
        when(populationService.getPersonAndFireStationListByAddress(person.getAddress()))
                .thenReturn(populationByFireStationsDTO);

        String base = "$.population[0]";

        mockMvc.perform(
                        get("/fire")
                                .param("address", person.getAddress())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fireStationNumbers[0]").value(fireStation.getStation()))
                .andExpect(jsonPath(base + ".firstName").value(person.getFirstName()))
                .andExpect(jsonPath(base + ".lastName").value(person.getLastName()))
                .andExpect(jsonPath(base + ".address").value(person.getAddress()))
                .andExpect(jsonPath(base + ".phone").value(person.getPhone()))
                .andExpect(jsonPath(base + ".email").value(person.getEmail()))
                .andExpect(jsonPath(base + ".age").value(30))
                .andExpect(
                        jsonPath(base + ".medications[0]")
                                .value(medicalRecord.getMedications().getFirst()))
                .andExpect(
                        jsonPath(base + ".allergies[0]")
                                .value(medicalRecord.getAllergies().getFirst()));
    }

    @Test
    void getPersonAndFireStationListByAddress_shouldReturnEmptyLists_whenAddressNotFound()
            throws Exception {
        when(populationService.getPersonAndFireStationListByAddress(anyString()))
                .thenReturn(new PopulationByFireStationsDTO(List.of(), List.of()));

        mockMvc.perform(get("/fire").param("address", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fireStationNumbers").isEmpty())
                .andExpect(jsonPath("$.population").isEmpty());
    }
}
