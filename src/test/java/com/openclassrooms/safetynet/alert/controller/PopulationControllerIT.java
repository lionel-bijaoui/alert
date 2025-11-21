package com.openclassrooms.safetynet.alert.controller;

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
    private static final String PERSON_A_ZIP = "97451";
    private static final String PERSON_A_PHONE = "841-874-6512";
    private static final String PERSON_A_EMAIL = "johndoe@email.com";
    private static final String PERSON_A_BIRTHDATE = "1984-06-15";
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
                .andExpect(jsonPath("$[0].firstName").value(PERSON_A_FIRST_NAME))
                .andExpect(jsonPath("$[0].lastName").value(PERSON_A_LAST_NAME))
                .andExpect(jsonPath("$[0].address").value(PERSON_A_ADDRESS))
                .andExpect(jsonPath("$[0].email").value(PERSON_A_EMAIL))
                .andExpect(jsonPath("$[0].age").value(41))
                .andExpect(jsonPath("$[0].medications[0]").value(PERSON_A_MEDICATIONS.get(0)))
                .andExpect(jsonPath("$[0].medications[1]").value(PERSON_A_MEDICATIONS.get(1)))
                .andExpect(jsonPath("$[0].allergies[0]").value(PERSON_A_ALLERGIES.getFirst()));
    }
}
