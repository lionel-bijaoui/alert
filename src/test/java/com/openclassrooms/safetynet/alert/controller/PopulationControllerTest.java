package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.PersonRepository;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
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

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(PopulationController.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PopulationControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean PersonRepository personRepository;

    @MockitoBean PopulationService populationService;

    @MockitoBean MedicalRecordService medicalRecordService;

    Person personA;
    Person personB;

    @BeforeEach
    void setUp() {
        personA = new PersonTestBuilder().build();
        personB =
                new PersonTestBuilder()
                        .withFirstName("Jojo")
                        .withEmail("jojodoe@email.com")
                        .build();
    }

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenPersonWithLastNameExists()
            throws Exception {
        int personAAge = 40;
        int personBAge = 14;

        MedicalRecord medicalRecordA = new MedicalRecordTestBuilder().build();
        MedicalRecord medicalRecordC =
                new MedicalRecordTestBuilder()
                        .withFirstName(personB.getFirstName())
                        .withLastName(personB.getLastName())
                        .withBirthdate(LocalDate.parse("2010-12-01"))
                        .withMedications(List.of())
                        .withAllergies(List.of("peanut"))
                        .build();
        PersonWithMedicalInfosDTO personAWithMedicalInfosDTO =
                new PersonWithMedicalInfosDTO(
                        personA.getFirstName(),
                        personA.getLastName(),
                        personA.getAddress(),
                        personA.getPhone(),
                        personA.getEmail(),
                        personAAge,
                        medicalRecordA.getMedications(),
                        medicalRecordA.getAllergies());
        PersonWithMedicalInfosDTO personCWithMedicalInfosDTO =
                new PersonWithMedicalInfosDTO(
                        personB.getFirstName(),
                        personB.getLastName(),
                        personB.getAddress(),
                        personB.getPhone(),
                        personB.getEmail(),
                        personBAge,
                        medicalRecordC.getMedications(),
                        medicalRecordC.getAllergies());

        when(populationService.getPersonListByLastName(personA.getLastName()))
                .thenReturn(List.of(personAWithMedicalInfosDTO, personCWithMedicalInfosDTO));

        mockMvc.perform(
                        get("/personInfolastName")
                                .param("lastName", personA.getLastName())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(
                        jsonPath(
                                "$[*].firstName",
                                containsInAnyOrder(personA.getFirstName(), personB.getFirstName())))
                .andExpect(
                        jsonPath(
                                "$[*].lastName",
                                containsInAnyOrder(personA.getLastName(), personB.getLastName())))
                .andExpect(
                        jsonPath(
                                "$[*].address",
                                containsInAnyOrder(personA.getAddress(), personB.getAddress())))
                .andExpect(
                        jsonPath(
                                "$[*].email",
                                containsInAnyOrder(personA.getEmail(), personB.getEmail())))
                .andExpect(jsonPath("$[*].age", containsInAnyOrder(personAAge, personBAge)))
                .andExpect(
                        jsonPath(
                                "$[*].medications",
                                containsInAnyOrder(
                                        medicalRecordA.getMedications(),
                                        medicalRecordC.getMedications())))
                .andExpect(
                        jsonPath(
                                "$[*].allergies",
                                containsInAnyOrder(
                                        medicalRecordA.getAllergies(),
                                        medicalRecordC.getAllergies())));
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
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getPersonListByLastName_shouldReturnBadRequest_whenLastNameIsMissing() throws Exception {
        mockMvc.perform(get("/personInfolastName").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllEmailFromCity_shouldReturnEmailList_whenPersonsExistInCity() throws Exception {
        personB =
                new PersonTestBuilder()
                        .withFirstName("Jane")
                        .withLastName("Smith")
                        .withAddress("29 15th St")
                        .withPhone("841-874-6513")
                        .withEmail("janesmith@email.com")
                        .build();
        when(populationService.getPersonEmailListByCity(personA.getCity()))
                .thenReturn(List.of(personA.getEmail(), personB.getEmail()));

        mockMvc.perform(
                        get("/communityEmail")
                                .param("city", personA.getCity())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(
                        jsonPath("$", containsInAnyOrder(personA.getEmail(), personB.getEmail())));
    }

    @Test
    void getAllEmailFromCity_shouldReturnEmptyList_whenNoPersonsExistInCity() throws Exception {
        when(populationService.getPersonEmailListByCity("NonExistingCity")).thenReturn(List.of());

        mockMvc.perform(
                        get("/communityEmail")
                                .param("city", "NonExistingCity")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
