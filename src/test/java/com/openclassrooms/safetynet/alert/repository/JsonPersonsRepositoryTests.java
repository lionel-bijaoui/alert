package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.TestSentenceGenerator;
import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("repository")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class JsonPersonsRepositoryTests {

    static final String PERSON_FIRST_NAME = "John";
    static final String PERSON_LAST_NAME = "Doe";
    static final String PERSON_ADDRESS = "1509 Culver St";

    @Mock
    JsonFileDataStore jsonFileDataStore;

    @InjectMocks
    JsonPersonRepository personsRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.setPersons(new ArrayList<>());
    }

    @Test
    void save_shouldSavePersonAndReturnIt_whenNewPerson() {
        Person personToSave = new Person();
        personToSave.setFirstName(PERSON_FIRST_NAME);
        personToSave.setLastName(PERSON_LAST_NAME);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Person savedPerson = personsRepository.save(personToSave);

        assertNotNull(savedPerson);
        assertEquals(PERSON_FIRST_NAME, savedPerson.getFirstName());
        assertEquals(PERSON_LAST_NAME, savedPerson.getLastName());
        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertTrue(database.getPersons().contains(personToSave), "The person should be added to the database.");
    }

    @Test
    void save_shouldUpdateExistingPerson_whenPersonAlreadyExists() {
        final String PERSON_NEW_ADDRESS = "123 New Address";
        Person existingPerson = new Person();
        existingPerson.setFirstName(PERSON_FIRST_NAME);
        existingPerson.setLastName(PERSON_LAST_NAME);
        database.getPersons().add(existingPerson);

        Person updatedPerson = new Person();
        updatedPerson.setFirstName(PERSON_FIRST_NAME);
        updatedPerson.setLastName(PERSON_LAST_NAME);
        updatedPerson.setAddress(PERSON_NEW_ADDRESS);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Person savedPerson = personsRepository.save(updatedPerson);

        assertNotNull(savedPerson);
        assertEquals(PERSON_NEW_ADDRESS, savedPerson.getAddress());
        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(1, database.getPersons().size(), "There should still be only one person in the database.");
        assertEquals(PERSON_NEW_ADDRESS, database.getPersons().getFirst().getAddress(), "The existing person's address should be updated.");
    }

    @Test
    void findAll_shouldReturnPersons_whenDatabaseHasPersons() {
        Person p1 = new Person();
        p1.setFirstName(PERSON_FIRST_NAME);
        p1.setLastName(PERSON_LAST_NAME);
        database.getPersons().add(p1);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PERSON_FIRST_NAME, result.getFirst().getFirstName());
        assertEquals(PERSON_LAST_NAME, result.getFirst().getLastName());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        when(jsonFileDataStore.readAll()).thenReturn(Optional.empty());

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The list should be empty when the store contains nothing.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullPersons() {
        database.setPersons(null);
        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> result = personsRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The list should be empty when the database contains null for persons.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByFirstNameAndLastName_shouldBeCaseInsensitiveAndFindPerson_whenMatchingExists() {
        Person p = new Person();
        p.setFirstName(PERSON_FIRST_NAME);
        p.setLastName(PERSON_LAST_NAME);
        database.getPersons().add(p);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<Person> found = personsRepository.findByFirstNameAndLastName(PERSON_FIRST_NAME.toLowerCase(), PERSON_LAST_NAME.toUpperCase());

        assertTrue(found.isPresent());
        assertEquals(PERSON_FIRST_NAME, found.get().getFirstName());
        assertEquals(PERSON_LAST_NAME, found.get().getLastName());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnEmpty_whenNotFound() {
        Person p = new Person();
        p.setFirstName(PERSON_FIRST_NAME);
        p.setLastName(PERSON_LAST_NAME);
        database.getPersons().add(p);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<Person> found = personsRepository.findByFirstNameAndLastName("Not", "Here");

        assertTrue(found.isEmpty());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByAddress_shouldBeCaseInsensitiveAndReturnPersons_whenMatchingExists() {
        Person p1 = new Person();
        p1.setFirstName(PERSON_FIRST_NAME);
        p1.setLastName(PERSON_LAST_NAME);
        p1.setAddress(PERSON_ADDRESS);
        database.getPersons().add(p1);

        Person p2 = new Person();
        p2.setFirstName("Another");
        p2.setLastName("Person");
        p2.setAddress(PERSON_ADDRESS);
        database.getPersons().add(p2);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> found = personsRepository.findByAddress(PERSON_ADDRESS.toUpperCase());

        assertNotNull(found);
        assertEquals(found, List.of(p1, p2));
        assertEquals(2, found.size());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByAddress_shouldReturnEmptyList_whenNoMatch() {
        Person p1 = new Person();
        p1.setFirstName(PERSON_FIRST_NAME);
        p1.setLastName(PERSON_LAST_NAME);
        p1.setAddress(PERSON_ADDRESS);
        database.getPersons().add(p1);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<Person> found = personsRepository.findByAddress("Unknown Address");

        assertNotNull(found);
        assertTrue(found.isEmpty(), "The list should be empty when no persons match the address.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void deleteByFirstNameAndLastName_shouldRemovePerson_whenPersonExists() {
        Person p1 = new Person();
        p1.setFirstName(PERSON_FIRST_NAME);
        p1.setLastName(PERSON_LAST_NAME);
        database.getPersons().add(p1);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        personsRepository.deleteByFirstNameAndLastName(PERSON_FIRST_NAME, PERSON_LAST_NAME);

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertFalse(database.getPersons().contains(p1), "The person should be removed from the database.");
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDoNothing_whenPersonDoesNotExist() {
        Person p1 = new Person();
        p1.setFirstName(PERSON_FIRST_NAME);
        p1.setLastName(PERSON_LAST_NAME);
        database.getPersons().add(p1);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        personsRepository.deleteByFirstNameAndLastName("Nonexistent", "Person");

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(1, database.getPersons().size(), "The database should still contain the original person.");
    }

}
