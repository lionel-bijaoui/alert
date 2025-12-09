package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class JsonPersonsRepositoryTests {

    @Mock JsonFileDataStore jsonFileDataStore;

    @InjectMocks JsonPersonRepository personsRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void save_shouldSavePersonAndReturnIt_whenNewPerson() {
        Person personToSave = new PersonTestBuilder().build();

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Person savedPerson = personsRepository.save(personToSave);

        assertNotNull(savedPerson);
        assertEquals(personToSave.getFirstName(), savedPerson.getFirstName());
        assertEquals(personToSave.getLastName(), savedPerson.getLastName());
        assertEquals(personToSave.getAddress(), savedPerson.getAddress());
        assertEquals(personToSave.getCity(), savedPerson.getCity());
        assertEquals(personToSave.getZip(), savedPerson.getZip());
        assertEquals(personToSave.getPhone(), savedPerson.getPhone());
        assertEquals(personToSave.getEmail(), savedPerson.getEmail());
        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertTrue(
                database.getPersons().contains(personToSave),
                "The person should be added to the database.");
    }

    @Test
    void save_shouldUpdateExistingPerson_whenPersonAlreadyExists() {
        Person existingPerson = new PersonTestBuilder().build();
        Person updatedPerson = new PersonTestBuilder().withAddress("123 New Address").build();
        database.getPersons().add(existingPerson);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Person savedPerson = personsRepository.save(updatedPerson);

        assertNotNull(savedPerson);
        assertEquals(updatedPerson.getAddress(), savedPerson.getAddress());
        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertEquals(
                1,
                database.getPersons().size(),
                "There should still be only one person in the database.");
        assertEquals(
                updatedPerson.getAddress(),
                database.getPersons().getFirst().getAddress(),
                "The existing person's address should be updated.");
    }

    @Test
    void findAll_shouldReturnPersons_whenDatabaseHasPersons() {
        Person person = new PersonTestBuilder().build();
        database.getPersons().add(person);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size(), "There should be one person in the returned list.");
        assertEquals(person.getFirstName(), result.getFirst().getFirstName());
        assertEquals(person.getLastName(), result.getFirst().getLastName());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        Database emptyDatabase =
                new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(jsonFileDataStore.getCachedDatabase()).thenReturn(emptyDatabase);

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned persons list should be empty.");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullPersons() {
        database.setPersons(null);
        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned persons list should be empty.");
    }

    @Test
    void findByFirstNameAndLastName_shouldBeCaseInsensitiveAndFindPerson_whenMatchingExists() {
        Person person = new PersonTestBuilder().build();
        database.getPersons().add(person);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Optional<Person> found =
                personsRepository.findByFirstNameAndLastName(
                        person.getFirstName().toLowerCase(), person.getLastName().toUpperCase());

        found.ifPresentOrElse(
                p -> {
                    assertEquals(person.getFirstName(), p.getFirstName());
                    assertEquals(person.getLastName(), p.getLastName());
                },
                () -> fail("The person should be found in the database."));
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnEmpty_whenNotFound() {
        Person person = new PersonTestBuilder().build();
        database.getPersons().add(person);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Optional<Person> found = personsRepository.findByFirstNameAndLastName("Not", "Here");

        assertTrue(found.isEmpty(), "No person should not be found.");
    }

    @Test
    void findByAddress_shouldBeCaseInsensitiveAndReturnPersons_whenMatchingExists() {
        Person person = new PersonTestBuilder().build();
        Person anotherPerson =
                new PersonTestBuilder().withFirstName("Another").withLastName("Person").build();
        database.getPersons().add(person);
        database.getPersons().add(anotherPerson);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<Person> found = personsRepository.findByAddress(person.getAddress().toUpperCase());

        assertNotNull(found);
        assertEquals(2, found.size(), "There should be two persons found matching the address.");
        assertEquals(
                found,
                List.of(person, anotherPerson),
                "The found persons should match the expected persons.");
    }

    @Test
    void findByAddress_shouldReturnEmptyList_whenNoMatchFound() {
        Person person = new PersonTestBuilder().build();
        database.getPersons().add(person);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<Person> found = personsRepository.findByAddress("Unknown Address");

        assertNotNull(found);
        assertTrue(
                found.isEmpty(),
                "The returned persons list should be empty when no persons match the address.");
    }

    @Test
    void deleteByFirstNameAndLastName_shouldRemovePerson_whenPersonExists() {
        Person person = new PersonTestBuilder().withLastName("Doe").build();
        database.getPersons().add(person);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        personsRepository.deleteByFirstNameAndLastName(person.getFirstName(), person.getLastName());

        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertFalse(
                database.getPersons().contains(person),
                "The person should be deleted from the database.");
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDoNothing_whenPersonDoesNotExist() {
        Person person = new PersonTestBuilder().build();
        database.getPersons().add(person);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        personsRepository.deleteByFirstNameAndLastName("Nonexistent", "Person");

        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertEquals(
                1,
                database.getPersons().size(),
                "The database should still contain the original person.");
    }
}
