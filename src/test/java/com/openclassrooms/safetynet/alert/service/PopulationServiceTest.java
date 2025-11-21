package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PopulationServiceTest {

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
    private static Person personA;
    private static Person personB;

    @Mock private JsonPersonRepository jsonPersonRepository;

    @InjectMocks private PopulationService populationService;

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
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));
        List<String> addressList = List.of(PERSON_A_ADDRESS, PERSON_B_ADDRESS);

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddressList_shouldReturnEmptyList_whenNoMatchingAddresses() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));
        List<String> addressList = List.of(PERSON_B_ADDRESS);

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByAddress_shouldReturnPersonList_whenAddressProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<Person> result = populationService.getPersonListByAddress(PERSON_B_ADDRESS);

        assertEquals(1, result.size());
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddress_shouldReturnEmptyList_whenNoMatchingAddress() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<Person> result = populationService.getPersonListByAddress(PERSON_B_ADDRESS);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByCity_shouldReturnPersonList_whenCityProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<Person> result = populationService.getPersonListByCity(PERSON_A_CITY);

        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByCity_shouldReturnEmptyList_whenNoMatchingCity() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<Person> result = populationService.getPersonListByCity("NonExistingCity");

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenLastNameProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<Person> result = populationService.getPersonListByLastName(PERSON_B_LAST_NAME);

        assertEquals(1, result.size());
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByLastName_shouldReturnEmptyList_whenNoMatchingLastName() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<Person> result = populationService.getPersonListByLastName("NonExistingLastName");

        assertTrue(result.isEmpty());
    }
}
