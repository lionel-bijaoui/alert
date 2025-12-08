package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonMedicalRecordRepository;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PopulationServiceIT extends IntegrationTestBase {

    @Autowired JsonPersonRepository jsonPersonRepository;

    @Autowired JsonMedicalRecordRepository jsonMedicalRecordRepository;

    @Autowired PopulationService populationService;

    Person personA;
    Person personB;

    @BeforeEach
    void setUp() {
        personA = new PersonTestBuilder().build();
        personB =
                new PersonTestBuilder()
                        .withFirstName("Jane")
                        .withLastName("Smith")
                        .withAddress("29 15th St")
                        .withEmail("janesmith@email.com")
                        .build();
    }

    @Test
    void getPersonListByAddressList_shouldReturnPersonList_whenAddressListProvided() {
        jsonPersonRepository.save(personB);
        List<String> addressList = List.of(personA.getAddress(), personB.getAddress());

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddress_shouldReturnPersonList_whenAddressProvided() {
        jsonPersonRepository.save(personB);

        List<Person> result = populationService.getPersonListByAddress(personB.getAddress());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByCity_shouldReturnPersonEmailList_whenCityProvided() {
        jsonPersonRepository.save(personB);

        List<String> result = populationService.getPersonEmailListByCity(personB.getCity());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(personA.getEmail()));
        assertTrue(result.contains(personB.getEmail()));
    }

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenLastNameProvided() {
        MedicalRecord medicalRecord =
                new MedicalRecordTestBuilder().withFirstName("Jane").withLastName("Smith").build();
        jsonPersonRepository.save(personB);
        jsonMedicalRecordRepository.save(medicalRecord);

        List<PersonWithMedicalInfosDTO> result =
                populationService.getPersonListByLastName(personB.getLastName());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(personB.getFirstName(), result.getFirst().firstName());
        assertEquals(personB.getLastName(), result.getFirst().lastName());
        assertEquals(personB.getAddress(), result.getFirst().address());
        assertEquals(personB.getPhone(), result.getFirst().phone());
        assertEquals(personB.getEmail(), result.getFirst().email());
        assertEquals(41, result.getFirst().age());
        assertEquals(medicalRecord.getMedications(), result.getFirst().medications());
        assertEquals(medicalRecord.getAllergies(), result.getFirst().allergies());
    }
}
