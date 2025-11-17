package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.TestSentenceGenerator;
import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

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

    static final String PERSON_FIRST_NAME = "John";
    static final String PERSON_LAST_NAME = "Doe";
    static final String PERSON_ADDRESS = "1509 Culver St";
    static final String PERSON_CITY = "Culver";
    static final String PERSON_ZIP = "97451";
    static final String PERSON_PHONE = "841-874-6512";
    static final String PERSON_EMAIL = "john.doe@email.com";

    @Mock JsonFileDataStore jsonFileDataStore;

    @InjectMocks JsonPersonRepository personsRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database(new ArrayList<>(), null, null);
    }

    @Test
    void save_shouldSavePersonAndReturnIt_whenNewPerson() {
        Person personToSave =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Person savedPerson = personsRepository.save(personToSave);

        assertNotNull(savedPerson);
        assertEquals(PERSON_FIRST_NAME, savedPerson.getFirstName());
        assertEquals(PERSON_LAST_NAME, savedPerson.getLastName());
        assertEquals(PERSON_ADDRESS, savedPerson.getAddress());
        assertEquals(PERSON_CITY, savedPerson.getCity());
        assertEquals(PERSON_ZIP, savedPerson.getZip());
        assertEquals(PERSON_PHONE, savedPerson.getPhone());
        assertEquals(PERSON_EMAIL, savedPerson.getEmail());
        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertTrue(
                database.getPersons().contains(personToSave),
                "The person should be added to the database.");
    }

    @Test
    void save_shouldUpdateExistingPerson_whenPersonAlreadyExists() {
        Person existingPerson =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(existingPerson);

        final String PERSON_NEW_ADDRESS = "123 New Address";
        Person updatedPerson =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_NEW_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Person savedPerson = personsRepository.save(updatedPerson);

        assertNotNull(savedPerson);
        assertEquals(PERSON_NEW_ADDRESS, savedPerson.getAddress());
        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(
                1,
                database.getPersons().size(),
                "There should still be only one person in the database.");
        assertEquals(
                PERSON_NEW_ADDRESS,
                database.getPersons().getFirst().getAddress(),
                "The existing person's address should be updated.");
    }

    @Test
    void findAll_shouldReturnPersons_whenDatabaseHasPersons() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size(), "There should be one person in the returned list.");
        assertEquals(PERSON_FIRST_NAME, result.getFirst().getFirstName());
        assertEquals(PERSON_LAST_NAME, result.getFirst().getLastName());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        when(jsonFileDataStore.readAll()).thenReturn(Optional.empty());

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned persons list should be empty.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullPersons() {
        database.setPersons(null);
        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned persons list should be empty.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByFirstNameAndLastName_shouldBeCaseInsensitiveAndFindPerson_whenMatchingExists() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<Person> found =
                personsRepository.findByFirstNameAndLastName(
                        PERSON_FIRST_NAME.toLowerCase(), PERSON_LAST_NAME.toUpperCase());

        assertTrue(found.isPresent(), "The person should be found.");
        assertEquals(PERSON_FIRST_NAME, found.get().getFirstName());
        assertEquals(PERSON_LAST_NAME, found.get().getLastName());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnEmpty_whenNotFound() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<Person> found = personsRepository.findByFirstNameAndLastName("Not", "Here");

        assertTrue(found.isEmpty(), "No person should not be found.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByAddress_shouldBeCaseInsensitiveAndReturnPersons_whenMatchingExists() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        Person anotherPerson =
                new Person(
                        "Another",
                        "Person",
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(anotherPerson);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> found = personsRepository.findByAddress(PERSON_ADDRESS.toUpperCase());

        assertNotNull(found);
        assertEquals(2, found.size(), "There should be two persons found matching the address.");
        assertEquals(
                found,
                List.of(person, anotherPerson),
                "The found persons should match the expected persons.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByAddress_shouldReturnEmptyList_whenNoMatchFound() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> found = personsRepository.findByAddress("Unknown Address");

        assertNotNull(found);
        assertTrue(
                found.isEmpty(),
                "The returned persons list should be empty when no persons match the address.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void deleteByFirstNameAndLastName_shouldRemovePerson_whenPersonExists() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        personsRepository.deleteByFirstNameAndLastName(PERSON_FIRST_NAME, PERSON_LAST_NAME);

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertFalse(
                database.getPersons().contains(person),
                "The person should be deleted from the database.");
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDoNothing_whenPersonDoesNotExist() {
        Person person =
                new Person(
                        PERSON_FIRST_NAME,
                        PERSON_LAST_NAME,
                        PERSON_ADDRESS,
                        PERSON_CITY,
                        PERSON_ZIP,
                        PERSON_PHONE,
                        PERSON_EMAIL);
        database.getPersons().add(person);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        personsRepository.deleteByFirstNameAndLastName("Nonexistent", "Person");

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(
                1,
                database.getPersons().size(),
                "The database should still contain the original person.");
    }
}
