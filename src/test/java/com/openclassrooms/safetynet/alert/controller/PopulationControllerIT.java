package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
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
public class PopulationControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenLastNameExists() throws Exception {
        Person person = new PersonTestBuilder().build();
        MedicalRecord mr = new MedicalRecordTestBuilder().build();

        mockMvc.perform(
                        get("/personInfolastName")
                                .param("lastName", person.getLastName())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].firstName", containsInAnyOrder(person.getFirstName())))
                .andExpect(jsonPath("$[*].lastName", containsInAnyOrder(person.getLastName())))
                .andExpect(jsonPath("$[*].address", containsInAnyOrder(person.getAddress())))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(person.getEmail())))
                .andExpect(jsonPath("$[*].age", containsInAnyOrder(41)))
                .andExpect(jsonPath("$[*].medications", containsInAnyOrder(mr.getMedications())))
                .andExpect(jsonPath("$[*].allergies", containsInAnyOrder(mr.getAllergies())));
    }

    @Test
    void getAllEmailFromCity_shouldReturnEmailList_whenCityExists() throws Exception {
        Person person = new PersonTestBuilder().build();

        mockMvc.perform(
                        get("/communityEmail")
                                .param("city", person.getCity())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$", containsInAnyOrder(person.getEmail())));
    }
}
