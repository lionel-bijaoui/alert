package com.openclassrooms.safetynet.alert.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
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
public class HazardControllerIT extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Test
    void
            getHouseholdsByFireStationNumberList_shouldReturnHouseholdsList_whenFireStationNumberListExists()
                    throws Exception {
        mockMvc.perform(
                        get("/flood/stations")
                                .param("stations", "2", "3")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['1509 Culver St'][0].firstName").value("John"))
                .andExpect(jsonPath("$['1509 Culver St'][0].lastName").value("Doe"))
                .andExpect(jsonPath("$['1509 Culver St'][0].age").value(41))
                .andExpect(jsonPath("$['1509 Culver St'][0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$['1509 Culver St'][0].phone").value("841-874-6512"))
                .andExpect(jsonPath("$['1509 Culver St'][0].email").value("johndoe@email.com"))
                .andExpect(jsonPath("$['1509 Culver St'][0].medications[0]").value("aznol:350mg"))
                .andExpect(
                        jsonPath("$['1509 Culver St'][0].medications[1]")
                                .value("hydrapermazol:100mg"))
                .andExpect(jsonPath("$['1509 Culver St'][0].allergies[0]").value("nillacilan"));
    }

    @Test
    void
            getPersonAndFireStationListByAddress_shouldReturnPersonAndFireStationList_whenAddressExists()
                    throws Exception {
        FireStation fireStation = new FireStationTestBuilder().build();
        Person person = new PersonTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();

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
                .andExpect(jsonPath(base + ".age").value(41))
                .andExpect(
                        jsonPath(base + ".medications[0]")
                                .value(medicalRecord.getMedications().getFirst()))
                .andExpect(
                        jsonPath(base + ".allergies[0]")
                                .value(medicalRecord.getAllergies().getFirst()));
    }
}
