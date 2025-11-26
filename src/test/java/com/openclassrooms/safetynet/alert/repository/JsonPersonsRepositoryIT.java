package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class JsonPersonsRepositoryIT extends IntegrationTestBase {

    @Autowired JsonPersonRepository personsRepository;

    @Test
    void save_shouldPersistPersonAndRetrieveIt_whenNewPerson() {
        Person personToSave = new PersonTestBuilder().build();

        personsRepository.save(personToSave);

        Optional<Person> saved =
                personsRepository.findByFirstNameAndLastName(
                        personToSave.getFirstName(), personToSave.getLastName());
        saved.ifPresentOrElse(
                person -> {
                    assertEquals(personToSave.getFirstName(), person.getFirstName());
                    assertEquals(personToSave.getLastName(), person.getLastName());
                    assertEquals(personToSave.getAddress(), person.getAddress());
                    assertEquals(personToSave.getCity(), person.getCity());
                    assertEquals(personToSave.getZip(), person.getZip());
                    assertEquals(personToSave.getPhone(), person.getPhone());
                    assertEquals(personToSave.getEmail(), person.getEmail());
                },
                () -> fail("The saved person should be retrievable"));
    }

    @Test
    void findAll_shouldReturnDataFromFile_whenStoreLoaded() {
        Person existingPerson = new PersonTestBuilder().build();
        List<Person> all = personsRepository.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        boolean hasPerson =
                all.stream()
                        .anyMatch(
                                p ->
                                        existingPerson.getFirstName().equals(p.getFirstName())
                                                && existingPerson
                                                        .getLastName()
                                                        .equals(p.getLastName()));
        assertTrue(hasPerson, "The test database must contain John Doe");
    }

    @Test
    void findByFirstNameAndLastName_shouldFindPerson_whenPresent() {
        Person existingPerson = new PersonTestBuilder().build();

        Optional<Person> found =
                personsRepository.findByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName());

        found.ifPresentOrElse(
                person -> {
                    assertEquals(existingPerson.getFirstName(), person.getFirstName());
                    assertEquals(existingPerson.getLastName(), person.getLastName());
                    assertEquals(existingPerson.getAddress(), person.getAddress());
                    assertEquals(existingPerson.getCity(), person.getCity());
                    assertEquals(existingPerson.getZip(), person.getZip());
                    assertEquals(existingPerson.getPhone(), person.getPhone());
                    assertEquals(existingPerson.getEmail(), person.getEmail());
                },
                () -> fail("The person should be found"));
    }
}
