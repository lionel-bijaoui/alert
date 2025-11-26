package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
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
    void getPersonListByCity_shouldReturnPersonList_whenCityProvided() {
        jsonPersonRepository.save(personB);

        List<Person> result = populationService.getPersonListByCity(personB.getCity());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenLastNameProvided() {
        jsonPersonRepository.save(personB);

        List<Person> result = populationService.getPersonListByLastName(personB.getLastName());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(personB));
    }
}
