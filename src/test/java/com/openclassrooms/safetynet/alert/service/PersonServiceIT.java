package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
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

    @Autowired PersonService personService;

    @Autowired JsonPersonRepository personRepository;

    @Test
    void addPerson_shouldPersistAndReturn_whenNew() {
        Person toAdd =
                new PersonTestBuilder()
                        .withFirstName("New")
                        .withLastName("Person")
                        .withAddress("123 New St")
                        .withCity("City")
                        .withZip("00000")
                        .withPhone("000")
                        .withEmail("n@p.com")
                        .build();

        Person saved = personService.addPerson(toAdd);

        assertNotNull(saved);
        assertEquals(toAdd.getFirstName(), saved.getFirstName());
        assertEquals(toAdd.getLastName(), saved.getLastName());

        Optional<Person> found =
                personRepository.findByFirstNameAndLastName(
                        toAdd.getFirstName(), toAdd.getLastName());
        assertTrue(found.isPresent(), "The new person should be present in the repository");
        assertEquals(toAdd.getAddress(), found.get().getAddress());
    }

    @Test
    void updatePerson_shouldPersistUpdate_whenExisting() {
        Person updated = new PersonTestBuilder().withAddress("123 Updated St").build();

        Person result = personService.updatePerson(updated);

        assertNotNull(result);
        assertEquals(updated.getFirstName(), result.getFirstName());
        assertEquals(updated.getLastName(), result.getLastName());
        assertEquals(updated.getAddress(), result.getAddress());
        assertEquals(updated.getCity(), result.getCity());
        assertEquals(updated.getZip(), result.getZip());
        assertEquals(updated.getPhone(), result.getPhone());
        assertEquals(updated.getEmail(), result.getEmail());

        Optional<Person> found =
                personRepository.findByFirstNameAndLastName(
                        updated.getFirstName(), updated.getLastName());
        found.ifPresentOrElse(
                person -> {
                    assertEquals(updated.getFirstName(), person.getFirstName());
                    assertEquals(updated.getLastName(), person.getLastName());
                },
                () -> fail("The person should still be present after update"));
    }

    @Test
    void deletePerson_shouldRemove_whenExisting() {
        Person existing = new PersonTestBuilder().build();
        Optional<Person> before =
                personRepository.findByFirstNameAndLastName(
                        existing.getFirstName(), existing.getLastName());
        assertTrue(before.isPresent(), "Precondition: existing person should be present");

        personService.deletePerson(existing.getFirstName(), existing.getLastName());

        Optional<Person> after =
                personRepository.findByFirstNameAndLastName(
                        existing.getFirstName(), existing.getLastName());
        assertTrue(after.isEmpty(), "The person should be removed from the repository");
    }
}
