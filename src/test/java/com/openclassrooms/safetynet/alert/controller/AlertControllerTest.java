package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.dto.ChildDTO;
import com.openclassrooms.safetynet.alert.dto.ChildrenAndAdultsDTO;
import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.ContactInformationService;
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

import java.util.List;

@WebMvcTest(AlertController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class AlertControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean ContactInformationService contactInformationService;

    Person person;

    @BeforeEach
    void setUp() {
        person = new PersonTestBuilder().build();
    }

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists()
                    throws Exception {
        Integer fireStationNumber = 3;

        when(contactInformationService.getPhoneNumberListByFireStationNumber(anyInt()))
                .thenReturn(List.of(person.getPhone()));

        mockMvc.perform(
                        get("/phoneAlert")
                                .param("firestation", String.valueOf(fireStationNumber))
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

        when(contactInformationService.getPhoneNumberListByFireStationNumber(fireStationNumber))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/phoneAlert")
                                .param("firestation", String.valueOf(fireStationNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getChildrenListByAddress_shouldReturnChildrenList_whenChildrenExist() throws Exception {
        ChildDTO childDTO = new ChildDTO("Jojo", "Doe", 1);

        PersonDTO adultDTO =
                new PersonDTO(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getCity(),
                        person.getZip(),
                        person.getPhone(),
                        person.getEmail());

        ChildrenAndAdultsDTO childrenAndAdultsDTO =
                new ChildrenAndAdultsDTO(List.of(childDTO), List.of(adultDTO));

        when(contactInformationService.getChildrenListByAddress(any()))
                .thenReturn(childrenAndAdultsDTO);

        mockMvc.perform(get("/childAlert").param("address", person.getAddress()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].firstName").value(childDTO.firstName()))
                .andExpect(jsonPath("$.children[0].lastName").value(childDTO.lastName()))
                .andExpect(jsonPath("$.children[0].age").value(1))
                .andExpect(jsonPath("$.adults").isArray())
                .andExpect(jsonPath("$.adults").isNotEmpty());
    }

    @Test
    void getChildrenListByAddress_shouldReturnEmptyList_whenNoChildrenExist() throws Exception {
        PersonDTO personDTO =
                new PersonDTO(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getCity(),
                        person.getZip(),
                        person.getPhone(),
                        person.getEmail());

        ChildrenAndAdultsDTO onlyAdultsDTO =
                new ChildrenAndAdultsDTO(List.of(), List.of(personDTO));

        when(contactInformationService.getChildrenListByAddress(person.getAddress()))
                .thenReturn(onlyAdultsDTO);

        mockMvc.perform(get("/childAlert").param("address", person.getAddress()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children").isEmpty())
                .andExpect(jsonPath("$.adults").isArray())
                .andExpect(jsonPath("$.adults").isNotEmpty());
    }
}
