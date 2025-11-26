package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
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
import java.util.List;

@WebMvcTest(AlertController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class AlertControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean FireStationService fireStationService;

    @MockitoBean PopulationService populationService;

    @MockitoBean MedicalRecordService medicalRecordService;

    @MockitoBean PersonMapper personMapper;

    Person person;
    List<Person> personList;

    @BeforeEach
    void setUp() {
        person = new PersonTestBuilder().build();
        personList = new ArrayList<>(List.of(person));
    }

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists()
                    throws Exception {
        FireStation fireStation = new FireStationTestBuilder().build();
        List<String> fireStationList = List.of(fireStation.getAddress());

        when(fireStationService.getFireStationAddressListByFireStationNumber(
                        fireStation.getStation()))
                .thenReturn(fireStationList);
        when(populationService.getPersonListByAddressList(fireStationList)).thenReturn(personList);

        mockMvc.perform(
                        get("/phoneAlert")
                                .param("firestation", String.valueOf(fireStation.getStation()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]", containsInAnyOrder(person.getPhone())));
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

    @Test
    void getChildrenListByAddress_shouldReturnChildrenList_whenChildrenExist() throws Exception {
        Person child =
                new PersonTestBuilder()
                        .withFirstName("Jojo")
                        .withEmail("jojodoe@email.com")
                        .build();

        personList.add(child);
        when(populationService.getPersonListByAddress(person.getAddress())).thenReturn(personList);
        when(medicalRecordService.getAgeFromPersonFirstAndLastName(
                        person.getFirstName(), person.getLastName()))
                .thenReturn(34);
        when(medicalRecordService.getAgeFromPersonFirstAndLastName(
                        child.getFirstName(), child.getLastName()))
                .thenReturn(1);

        mockMvc.perform(get("/childAlert").param("address", person.getAddress()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].firstName").value(child.getFirstName()))
                .andExpect(jsonPath("$.children[0].lastName").value(child.getLastName()))
                .andExpect(jsonPath("$.children[0].age").value(1));
    }

    @Test
    void getChildrenListByAddress_shouldReturnEmptyList_whenNoChildrenExist() throws Exception {
        when(populationService.getPersonListByAddress(person.getAddress())).thenReturn(personList);
        when(medicalRecordService.getAgeFromPersonFirstAndLastName(
                        person.getFirstName(), person.getLastName()))
                .thenReturn(34);

        mockMvc.perform(get("/childAlert").param("address", person.getAddress()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children").isEmpty());
    }
}
