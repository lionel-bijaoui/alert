package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;
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
import java.util.List;

@WebMvcTest(AlertController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class AlertControllerTest {
    static final String PERSON_A_FIRST_NAME = "John";
    static final String PERSON_A_LAST_NAME = "Doe";
    static final String PERSON_A_ADDRESS = "1509 Culver St";
    static final String PERSON_A_CITY = "Culver";
    static final String PERSON_A_ZIP = "97451";
    static final String PERSON_A_PHONE = "841-874-6512";
    static final String PERSON_A_EMAIL = "johndoe@email.com";
    static final String PERSON_A_BIRTHDATE = "1990-01-01";

    static final String PERSON_C_FIRST_NAME = "Jojo";
    static final String PERSON_C_LAST_NAME = PERSON_A_LAST_NAME;
    static final String PERSON_C_ADDRESS = PERSON_A_ADDRESS;
    static final String PERSON_C_CITY = PERSON_A_CITY;
    static final String PERSON_C_ZIP = PERSON_A_ZIP;
    static final String PERSON_C_PHONE = PERSON_A_PHONE;
    static final String PERSON_C_EMAIL = "jojodoe@email.com";
    static final String PERSON_C_BIRTHDATE = "2025-01-01";

    @Autowired MockMvc mockMvc;

    @MockitoBean FireStationService fireStationService;

    @MockitoBean PopulationService populationService;

    @MockitoBean MedicalRecordService medicalRecordService;

    @MockitoBean PersonMapper personMapper;
    private List<Person> personList;

    @BeforeEach
    void setUp() {
        Person personA =
                new Person(
                        PERSON_A_FIRST_NAME,
                        PERSON_A_LAST_NAME,
                        PERSON_A_ADDRESS,
                        PERSON_A_CITY,
                        PERSON_A_ZIP,
                        PERSON_A_PHONE,
                        PERSON_A_EMAIL);

        personList = new ArrayList<>(List.of(personA));
    }

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists()
                    throws Exception {
        Integer fireStationNumber = 1;
        String fireStationAddress = "1509 Culver St";
        List<String> fireStationList = List.of(fireStationAddress);

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

    @Test
    void getChildrenListByAddress_shouldReturnChildrenList_whenChildrenExist() throws Exception {
        Person personC =
                new Person(
                        PERSON_C_FIRST_NAME,
                        PERSON_C_LAST_NAME,
                        PERSON_C_ADDRESS,
                        PERSON_C_CITY,
                        PERSON_C_ZIP,
                        PERSON_C_PHONE,
                        PERSON_C_EMAIL);

        MedicalRecord medicalRecordA =
                new MedicalRecord(
                        PERSON_A_FIRST_NAME,
                        PERSON_A_LAST_NAME,
                        LocalDate.parse(PERSON_A_BIRTHDATE),
                        List.of(),
                        List.of());
        MedicalRecord medicalRecordC =
                new MedicalRecord(
                        PERSON_C_FIRST_NAME,
                        PERSON_C_LAST_NAME,
                        LocalDate.parse(PERSON_C_BIRTHDATE),
                        List.of(),
                        List.of());

        personList.add(personC);
        when(populationService.getPersonListByAddress(PERSON_A_ADDRESS)).thenReturn(personList);
        when(medicalRecordService.calculateAgeFromBirthdate(LocalDate.parse(PERSON_A_BIRTHDATE)))
                .thenReturn(34);
        when(medicalRecordService.calculateAgeFromBirthdate(LocalDate.parse(PERSON_C_BIRTHDATE)))
                .thenReturn(1);
        when(medicalRecordService.getMedicalRecordByFullName(
                        PERSON_A_FIRST_NAME, PERSON_A_LAST_NAME))
                .thenReturn(medicalRecordA);
        when(medicalRecordService.getMedicalRecordByFullName(
                        PERSON_C_FIRST_NAME, PERSON_C_LAST_NAME))
                .thenReturn(medicalRecordC);

        mockMvc.perform(get("/childAlert").param("address", PERSON_A_ADDRESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].firstName").value(PERSON_C_FIRST_NAME))
                .andExpect(jsonPath("$.children[0].lastName").value(PERSON_C_LAST_NAME))
                .andExpect(jsonPath("$.children[0].age").value(1));
    }

    @Test
    void getChildrenListByAddress_shouldReturnEmptyList_whenNoChildrenExist() throws Exception {
        MedicalRecord medicalRecordA =
                new MedicalRecord(
                        PERSON_A_FIRST_NAME,
                        PERSON_A_LAST_NAME,
                        LocalDate.parse(PERSON_A_BIRTHDATE),
                        List.of(),
                        List.of());

        when(populationService.getPersonListByAddress(PERSON_A_ADDRESS)).thenReturn(personList);
        when(medicalRecordService.calculateAgeFromBirthdate(any(LocalDate.class))).thenReturn(34);
        when(medicalRecordService.getMedicalRecordByFullName(
                        PERSON_A_FIRST_NAME, PERSON_A_LAST_NAME))
                .thenReturn(medicalRecordA);

        mockMvc.perform(get("/childAlert").param("address", PERSON_A_ADDRESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children").isEmpty());
    }
}
