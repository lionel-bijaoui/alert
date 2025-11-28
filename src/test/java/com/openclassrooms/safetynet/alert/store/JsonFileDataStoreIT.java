package com.openclassrooms.safetynet.alert.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
public class JsonFileDataStoreIT extends IntegrationTestBase {

    @Autowired JsonFileDataStore store;

    @Test
    void load_shouldMakeCurrentFileReadable() throws IOException {
        Path currentPath = Path.of(store.current());

        assertTrue(Files.exists(currentPath), "The current JSON file must exist after loading.");
        assertTrue(
                Files.size(currentPath) > 0,
                "The current JSON file must not be empty after loading.");

        Optional<Database> optionalDatabase = store.readAll();
        optionalDatabase.ifPresentOrElse(
                db -> {
                    assertNotNull(db.getPersons(), "The persons list must not be null.");
                    assertFalse(db.getPersons().isEmpty(), "The persons list must not be empty.");
                    assertNotNull(db.getFirestations(), "The fire stations list must not be null.");
                    assertNotNull(
                            db.getMedicalrecords(), "The medical records list must not be null.");
                },
                () -> {
                    throw new AssertionError("The database must be readable after loading.");
                });
    }

    @Test
    void writeAll_shouldPersistChanges_andBeReadableAfterWrite() {
        // Prepare a minimal database with a single person
        Database newDb =
                new Database(
                        List.of(
                                new PersonTestBuilder()
                                        .withFirstName("TestFirstName")
                                        .withLastName("TestLastName")
                                        .withAddress("123 Test St")
                                        .withCity("TestCity")
                                        .withZip("12345")
                                        .withPhone("123-456-7890")
                                        .withEmail("test@email.com")
                                        .build()),
                        List.of(),
                        List.of());

        // Write modified database
        store.writeAll(newDb);

        // Read back and assert the changes are persisted
        Optional<Database> optionalDatabase = store.readAll();
        optionalDatabase.ifPresentOrElse(
                database -> {
                    assertNotNull(
                            database.getPersons(),
                            "The persons list after writing must not be null.");
                    assertEquals(
                            1,
                            database.getPersons().size(),
                            "After modified write, there must be exactly 1 person.");
                    assertTrue(
                            database.getFirestations() != null
                                    && database.getFirestations().isEmpty(),
                            "The fire stations list must be empty after the modified write.");
                    assertTrue(
                            database.getMedicalrecords() != null
                                    && database.getMedicalrecords().isEmpty(),
                            "The medical records list must be empty after the modified write.");
                },
                () -> {
                    throw new AssertionError("The database must be readable after writing.");
                });
    }
}
