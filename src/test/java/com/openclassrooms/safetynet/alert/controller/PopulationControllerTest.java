package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.dto.PersonInfoDTO;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.PersonRepository;
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
import java.util.List;

@WebMvcTest(PopulationController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PopulationControllerTest {
    private static final String PERSON_A_FIRST_NAME = "John";
    private static final String PERSON_A_LAST_NAME = "Doe";
    private static final String PERSON_A_ADDRESS = "1509 Culver St";
    private static final String PERSON_A_CITY = "Culver";
    private static final String PERSON_A_ZIP = "97451";
    private static final String PERSON_A_PHONE = "841-874-6512";
    private static final String PERSON_A_EMAIL = "johndoe@email.com";
    private static final String PERSON_A_BIRTHDATE = "1984-06-15";
    private static final List<String> PERSON_A_MEDICATIONS =
            List.of("aznol:350mg", "hydrapermazol:100mg");
    private static final List<String> PERSON_A_ALLERGIES = List.of("nillacilan");

    private static final String PERSON_B_FIRST_NAME = "Jane";
    private static final String PERSON_B_LAST_NAME = "Smith";
    private static final String PERSON_B_ADDRESS = "29 15th St";
    private static final String PERSON_B_CITY = "Culver";
    private static final String PERSON_B_ZIP = "97451";
    private static final String PERSON_B_PHONE = "841-874-6513";
    private static final String PERSON_B_EMAIL = "janesmith@email.com";
    private static final String PERSON_B_BIRTHDATE = "1990-05-20";
    private static final List<String> PERSON_B_MEDICATIONS = List.of("ibupurin:200mg");
    private static final List<String> PERSON_B_ALLERGIES = List.of("shellfish", "peanut");

    private static final String PERSON_C_FIRST_NAME = "Jojo";
    private static final String PERSON_C_LAST_NAME = PERSON_A_LAST_NAME;
    private static final String PERSON_C_ADDRESS = PERSON_A_ADDRESS;
    private static final String PERSON_C_CITY = PERSON_A_CITY;
    private static final String PERSON_C_ZIP = PERSON_A_ZIP;
    private static final String PERSON_C_PHONE = PERSON_A_PHONE;
    private static final String PERSON_C_EMAIL = "jojodoe@email.com";
    private static final String PERSON_C_BIRTHDATE = "2010-12-01";
    private static final List<String> PERSON_C_MEDICATIONS = List.of();
    private static final List<String> PERSON_C_ALLERGIES = List.of("peanut");

    private static Person personA;
    private static Person personB;
    private static Person personC;
    private static MedicalRecord medicalRecordA;
    private static MedicalRecord medicalRecordB;
    private static MedicalRecord medicalRecordC;

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private PersonRepository personRepository;
    @MockitoBean private PopulationService populationService;
    @MockitoBean private MedicalRecordService medicalRecordService;

    @BeforeEach
    void setUp() {
        personA =
                new Person(
                        PERSON_A_FIRST_NAME,
                        PERSON_A_LAST_NAME,
                        PERSON_A_ADDRESS,
                        PERSON_A_CITY,
                        PERSON_A_ZIP,
                        PERSON_A_PHONE,
                        PERSON_A_EMAIL);
        personB =
                new Person(
                        PERSON_B_FIRST_NAME,
                        PERSON_B_LAST_NAME,
                        PERSON_B_ADDRESS,
                        PERSON_B_CITY,
                        PERSON_B_ZIP,
                        PERSON_B_PHONE,
                        PERSON_B_EMAIL);
        personC =
                new Person(
                        PERSON_C_FIRST_NAME,
                        PERSON_C_LAST_NAME,
                        PERSON_C_ADDRESS,
                        PERSON_C_CITY,
                        PERSON_C_ZIP,
                        PERSON_C_PHONE,
                        PERSON_C_EMAIL);
        medicalRecordA =
                new MedicalRecord(
                        PERSON_A_FIRST_NAME,
                        PERSON_A_LAST_NAME,
                        LocalDate.parse(PERSON_A_BIRTHDATE),
                        PERSON_A_MEDICATIONS,
                        PERSON_A_ALLERGIES);
        medicalRecordB =
                new MedicalRecord(
                        PERSON_B_FIRST_NAME,
                        PERSON_B_LAST_NAME,
                        LocalDate.parse(PERSON_B_BIRTHDATE),
                        PERSON_B_MEDICATIONS,
                        PERSON_B_ALLERGIES);
        medicalRecordC =
                new MedicalRecord(
                        PERSON_C_FIRST_NAME,
                        PERSON_C_LAST_NAME,
                        LocalDate.parse(PERSON_C_BIRTHDATE),
                        PERSON_C_MEDICATIONS,
                        PERSON_C_ALLERGIES);
    }

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenPersonWithLastNameExists()
            throws Exception {
        when(populationService.getPersonListByLastName(PERSON_A_LAST_NAME))
                .thenReturn(List.of(personA, personC));
        when(medicalRecordService.getMedicalRecordByFullName(
                        PERSON_A_FIRST_NAME, PERSON_A_LAST_NAME))
                .thenReturn(medicalRecordA);
        when(medicalRecordService.getMedicalRecordByFullName(
                        PERSON_C_FIRST_NAME, PERSON_C_LAST_NAME))
                .thenReturn(medicalRecordC);
        int personAAge = 40;
        when(medicalRecordService.calculateAgeFromBirthdate(medicalRecordA.getBirthdate()))
                .thenReturn(personAAge);
        int personBAge = 14;
        when(medicalRecordService.calculateAgeFromBirthdate(medicalRecordC.getBirthdate()))
                .thenReturn(personBAge);

        mockMvc.perform(
                        get("/personInfolastName")
                                .param("lastName", PERSON_A_LAST_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            String responseBody = result.getResponse().getContentAsString();
                            PersonInfoDTO[] responseDtoList =
                                    objectMapper.readValue(responseBody, PersonInfoDTO[].class);
                            assertEquals(2, responseDtoList.length);

                            assertEquals(PERSON_A_FIRST_NAME, responseDtoList[0].firstName());
                            assertEquals(PERSON_A_LAST_NAME, responseDtoList[0].lastName());
                            assertEquals(PERSON_A_ADDRESS, responseDtoList[0].address());
                            assertEquals(PERSON_A_EMAIL, responseDtoList[0].email());
                            assertEquals(personAAge, responseDtoList[0].age());
                            assertEquals(PERSON_A_MEDICATIONS, responseDtoList[0].medications());
                            assertEquals(PERSON_A_ALLERGIES, responseDtoList[0].allergies());

                            assertEquals(PERSON_C_FIRST_NAME, responseDtoList[1].firstName());
                            assertEquals(PERSON_C_LAST_NAME, responseDtoList[1].lastName());
                            assertEquals(PERSON_C_ADDRESS, responseDtoList[1].address());
                            assertEquals(PERSON_C_EMAIL, responseDtoList[1].email());
                            assertEquals(personBAge, responseDtoList[1].age());
                            assertEquals(PERSON_C_MEDICATIONS, responseDtoList[1].medications());
                            assertEquals(PERSON_C_ALLERGIES, responseDtoList[1].allergies());
                        });
    }

    @Test
    void getPersonListByLastName_shouldReturnEmptyList_whenNoPersonWithLastNameExists()
            throws Exception {
        when(populationService.getPersonListByLastName("NonExistingLastName"))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/personInfolastName")
                                .param("lastName", "NonExistingLastName")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            String responseBody = result.getResponse().getContentAsString();
                            PersonInfoDTO[] responseDtoList =
                                    objectMapper.readValue(responseBody, PersonInfoDTO[].class);
                            assertEquals(0, responseDtoList.length);
                        });
    }

    @Test
    void getPersonListByLastName_shouldReturnBadRequest_whenLastNameIsMissing() throws Exception {
        mockMvc.perform(get("/personInfolastName").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllEmailFromCity_shouldReturnEmailList_whenPersonsExistInCity() throws Exception {
        when(populationService.getPersonListByCity(PERSON_A_CITY))
                .thenReturn(List.of(personA, personB));

        mockMvc.perform(
                        get("/communityEmail")
                                .param("city", PERSON_A_CITY)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value(PERSON_A_EMAIL))
                .andExpect(jsonPath("$[1]").value(PERSON_B_EMAIL));
    }

    @Test
    void getAllEmailFromCity_shouldReturnEmptyList_whenNoPersonsExistInCity() throws Exception {
        when(populationService.getPersonListByCity("NonExistingCity")).thenReturn(List.of());
        mockMvc.perform(
                        get("/communityEmail")
                                .param("city", "NonExistingCity")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
