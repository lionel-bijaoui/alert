package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.TestSentenceGenerator;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class JsonPersonsRepositoryIT {

    static final String PERSON_FIRST_NAME = "John";
    static final String PERSON_LAST_NAME = "Doe";
    static final String PERSON_ADDRESS = "1509 Culver St";
    static final String PERSON_CITY = "Culver";
    static final String PERSON_ZIP = "97451";
    static final String PERSON_PHONE = "841-874-6512";
    static final String PERSON_EMAIL = "john.doe@email.com";

    @Autowired JsonPersonRepository personsRepository;

    JsonFileDataStore store;

    @BeforeEach
    void setup() {
        store.load();
    }

    @AfterEach
    void cleanup() throws Exception {
        Path current = Path.of(store.current());
        if (Files.exists(current)) {
            Files.delete(current);
        }
    }

    @Test
    @DisplayName("save should save person and return it when new person")
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

        Person savedPerson = personsRepository.save(personToSave);

        assertNotNull(savedPerson);
        assertEquals(PERSON_FIRST_NAME, savedPerson.getFirstName());
        assertEquals(PERSON_LAST_NAME, savedPerson.getLastName());
        assertEquals(PERSON_ADDRESS, savedPerson.getAddress());
        assertEquals(PERSON_CITY, savedPerson.getCity());
        assertEquals(PERSON_ZIP, savedPerson.getZip());
        assertEquals(PERSON_PHONE, savedPerson.getPhone());
        assertEquals(PERSON_EMAIL, savedPerson.getEmail());

        Optional<Person> retrieved =
                personsRepository.findByFirstNameAndLastName(PERSON_FIRST_NAME, PERSON_LAST_NAME);
        assertTrue(retrieved.isPresent());
        assertEquals(PERSON_ADDRESS, retrieved.get().getAddress());
        assertEquals(PERSON_LAST_NAME, retrieved.get().getLastName());
        assertEquals(PERSON_ADDRESS, retrieved.get().getAddress());
        assertEquals(PERSON_CITY, retrieved.get().getCity());
        assertEquals(PERSON_ZIP, retrieved.get().getZip());
        assertEquals(PERSON_PHONE, retrieved.get().getPhone());
        assertEquals(PERSON_EMAIL, retrieved.get().getEmail());
    }

    @Test
    void findAll_shouldReturnDataFromFile_whenStoreLoaded() {
        List<Person> all = personsRepository.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        boolean hasPerson =
                all.stream()
                        .anyMatch(
                                p ->
                                        PERSON_FIRST_NAME.equals(p.getFirstName())
                                                && PERSON_LAST_NAME.equals(p.getLastName()));
        assertTrue(hasPerson, "The test database must contain John Doe");
    }

    @Test
    void findByFirstNameAndLastName_shouldFindPerson_whenPresent() {
        Optional<Person> found =
                personsRepository.findByFirstNameAndLastName(PERSON_FIRST_NAME, PERSON_LAST_NAME);
        assertTrue(found.isPresent());
        Person p = found.get();
        assertEquals(PERSON_FIRST_NAME, p.getFirstName());
        assertEquals(PERSON_LAST_NAME, p.getLastName());
        assertEquals(PERSON_ADDRESS, p.getAddress());
    }
}
