package com.openclassrooms.safetynet.alert.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonMedicalRecordRepository;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
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

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class AlertControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Autowired JsonPersonRepository jsonPersonRepository;

    @Autowired JsonMedicalRecordRepository jsonMedicalRecordRepository;

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists()
                    throws Exception {
        Integer fireStationNumber = 3;

        mockMvc.perform(
                        get("/phoneAlert")
                                .param("firestation", String.valueOf(fireStationNumber))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*]", containsInAnyOrder("841-874-6512")));
    }

    @Test
    void getChildrenListByAddress_shouldReturnChildrenList_whenAddressExists() throws Exception {
        Person child =
                new PersonTestBuilder()
                        .withFirstName("Jane")
                        .withLastName("Smith")
                        .withEmail("janesmith@email.com")
                        .build();
        jsonPersonRepository.save(child);

        LocalDate twelveYearsAgo = LocalDate.now().minusYears(12);
        jsonMedicalRecordRepository.save(
                new MedicalRecordTestBuilder()
                        .withFirstName(child.getFirstName())
                        .withLastName(child.getLastName())
                        .withBirthdate(twelveYearsAgo)
                        .build());

        mockMvc.perform(
                        get("/childAlert")
                                .param("address", child.getAddress())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children[0].firstName").value(child.getFirstName()))
                .andExpect(jsonPath("$.children[0].lastName").value(child.getLastName()))
                .andExpect(jsonPath("$.adults").isArray())
                .andExpect(jsonPath("$.adults").isNotEmpty());
    }
}
