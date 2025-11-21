package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
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

    private static final String PERSON_A_FIRST_NAME = "John";
    private static final String PERSON_A_LAST_NAME = "Doe";
    private static final String PERSON_A_ADDRESS = "1509 Culver St";
    private static final String PERSON_A_CITY = "Culver";
    private static final String PERSON_A_ZIP = "97451";
    private static final String PERSON_A_PHONE = "841-874-6512";
    private static final String PERSON_A_EMAIL = "johndoe@email.com";
    private static final String PERSON_B_FIRST_NAME = "Jane";
    private static final String PERSON_B_LAST_NAME = "Smith";
    private static final String PERSON_B_ADDRESS = "29 15th St";
    private static final String PERSON_B_CITY = "Culver";
    private static final String PERSON_B_ZIP = "97451";
    private static final String PERSON_B_PHONE = "841-874-6513";
    private static final String PERSON_B_EMAIL = "janesmith@email.com";

    /** Person A is in the initial test values */
    private static Person personA;

    private static Person personB;

    @Autowired private JsonPersonRepository jsonPersonRepository;
    @Autowired private PopulationService populationService;

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
    }

    @Test
    void getPersonListByAddressList_shouldReturnPersonList_whenAddressListProvided() {
        jsonPersonRepository.save(personB);
        List<String> addressList = List.of(PERSON_A_ADDRESS, PERSON_B_ADDRESS);

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));

        jsonPersonRepository.deleteByFirstNameAndLastName(PERSON_B_FIRST_NAME, PERSON_B_LAST_NAME);
    }

    @Test
    void getPersonListByAddress_shouldReturnPersonList_whenAddressProvided() {
        jsonPersonRepository.save(personB);

        List<Person> result = populationService.getPersonListByAddress(PERSON_B_ADDRESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(personB));

        jsonPersonRepository.deleteByFirstNameAndLastName(PERSON_B_FIRST_NAME, PERSON_B_LAST_NAME);
    }
}
