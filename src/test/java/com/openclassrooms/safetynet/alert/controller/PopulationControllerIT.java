package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PopulationControllerIT extends IntegrationTestBase {
    private static final String PERSON_A_FIRST_NAME = "John";
    private static final String PERSON_A_LAST_NAME = "Doe";
    private static final String PERSON_A_ADDRESS = "1509 Culver St";
    private static final String PERSON_A_CITY = "Culver";
    private static final String PERSON_A_EMAIL = "johndoe@email.com";
    private static final List<String> PERSON_A_MEDICATIONS =
            List.of("aznol:350mg", "hydrapermazol:100mg");
    private static final List<String> PERSON_A_ALLERGIES = List.of("nillacilan");

    @Autowired MockMvc mockMvc;

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenLastNameExists() throws Exception {

        mockMvc.perform(
                        get("/personInfolastName")
                                .param("lastName", PERSON_A_LAST_NAME)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].firstName", containsInAnyOrder(PERSON_A_FIRST_NAME)))
                .andExpect(jsonPath("$[*].lastName", containsInAnyOrder(PERSON_A_LAST_NAME)))
                .andExpect(jsonPath("$[*].address", containsInAnyOrder(PERSON_A_ADDRESS)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(PERSON_A_EMAIL)))
                .andExpect(jsonPath("$[*].age", containsInAnyOrder(41)))
                .andExpect(jsonPath("$[*].medications", containsInAnyOrder(PERSON_A_MEDICATIONS)))
                .andExpect(jsonPath("$[*].allergies", containsInAnyOrder(PERSON_A_ALLERGIES)));
    }

    @Test
    void getAllEmailFromCity_shouldReturnEmailList_whenCityExists() throws Exception {
        mockMvc.perform(
                        get("/communityEmail")
                                .param("city", PERSON_A_CITY)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$", containsInAnyOrder(PERSON_A_EMAIL)));
    }
}
