package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
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

    @Mock JsonPersonRepository jsonPersonRepository;

    @InjectMocks PopulationService populationService;

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
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));
        List<String> addressList = List.of(personA.getAddress(), personB.getAddress());

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddressList_shouldReturnEmptyList_whenNoMatchingAddresses() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));
        List<String> addressList = List.of(personB.getAddress());

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByAddress_shouldReturnPersonList_whenAddressProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<Person> result = populationService.getPersonListByAddress(personB.getAddress());

        assertEquals(1, result.size());
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddress_shouldReturnEmptyList_whenNoMatchingAddress() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<Person> result = populationService.getPersonListByAddress(personB.getAddress());

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByCity_shouldReturnPersonList_whenCityProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<Person> result = populationService.getPersonListByCity(personA.getCity());

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

        List<Person> result = populationService.getPersonListByLastName(personB.getLastName());

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
