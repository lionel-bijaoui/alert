package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class PersonServiceIT extends IntegrationTestBase {

    private static final String EXISTING_FIRST = "John";
    private static final String EXISTING_LAST = "Doe";

    @Autowired private PersonService personService;
    @Autowired private JsonPersonRepository personRepository;

    @Test
    void addPerson_shouldPersistAndReturn_whenNew() {
        Person toAdd = new Person("New", "Person", "123 New St", "City", "00000", "000", "n@p.com");

        Person saved = personService.addPerson(toAdd);

        assertNotNull(saved);
        assertEquals("New", saved.getFirstName());
        assertEquals("Person", saved.getLastName());

        Optional<Person> found = personRepository.findByFirstNameAndLastName("New", "Person");
        assertTrue(found.isPresent(), "The new person should be present in the repository");
        assertEquals("123 New St", found.get().getAddress());
    }

    @Test
    void updatePerson_shouldPersistUpdate_whenExisting() {
        Person updated =
                new Person(
                        EXISTING_FIRST,
                        EXISTING_LAST,
                        "123 Updated St",
                        "Culver",
                        "97451",
                        "841-874-6512",
                        "jaboyd@email.com");

        Person result = personService.updatePerson(updated);

        assertNotNull(result);
        assertEquals(EXISTING_FIRST, result.getFirstName());
        assertEquals("123 Updated St", result.getAddress());

        Optional<Person> found =
                personRepository.findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
        assertTrue(found.isPresent(), "The person should still be present after update");
        assertEquals("123 Updated St", found.get().getAddress());
    }

    @Test
    void deletePerson_shouldRemove_whenExisting() {
        Optional<Person> before =
                personRepository.findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
        assertTrue(before.isPresent(), "Precondition: existing person should be present");

        personService.deletePerson(EXISTING_FIRST, EXISTING_LAST);

        Optional<Person> after =
                personRepository.findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
        assertTrue(after.isEmpty(), "The person should be removed from the repository");
    }
}
