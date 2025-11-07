package com.openclassrooms.safetynet.alert.store;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JsonFileDataStoreIT {

    @Autowired
    JsonFileDataStore store;

    @AfterEach
    void cleanup() throws Exception {
        Path currentPath = Path.of(store.current());
        if (Files.exists(currentPath)) {
            Files.delete(currentPath);
        }
    }

    @Test
    void load_shouldMakeCurrentFileReadable() throws IOException {
        store.load();

        Path currentPath = Path.of(store.current());

        // The current file must exist and not be empty
        assertTrue(Files.exists(currentPath), "The current JSON file must exist after loading.");
        assertTrue(Files.size(currentPath) > 0, "The current JSON file must not be empty after loading.");

        // Read the database
        Optional<Database> maybeDb = store.readAll();
        assertTrue(maybeDb.isPresent(), "The database must be readable after loading.");
        Database db = maybeDb.get();
        assertNotNull(db.getPersons(), "The persons list must not be null.");
        assertFalse(db.getPersons().isEmpty(), "The persons list must not be empty.");
        assertNotNull(db.getFirestations(), "The fire stations list must not be null.");
        assertNotNull(db.getMedicalrecords(), "The medical records list must not be null.");
    }

    @Test
    void writeAll_shouldPersistChanges_andBeReadableAfterWrite() {
        store.load();

        // Prepare a minimal database with a single person
        Database newDb = new Database();
        newDb.setPersons(List.of(new Person()));
        newDb.setFirestations(List.of());
        newDb.setMedicalrecords(null);

        // Write modified database
        store.writeAll(newDb);

        // Read back and assert the changes are persisted
        Optional<Database> maybeDbAfter = store.readAll();
        assertTrue(maybeDbAfter.isPresent(), "The database must be readable after writing.");
        Database dbAfter = maybeDbAfter.get();

        assertNotNull(dbAfter.getPersons(), "The persons list after writing must not be null.");
        assertEquals(1, dbAfter.getPersons().size(), "After modified write, there must be exactly 1 person.");
        assertTrue(dbAfter.getFirestations() != null && dbAfter.getFirestations().isEmpty(), "The fire stations list must be empty after the modified write.");
        assertNull(dbAfter.getMedicalrecords(), "The medical records list must be null after the modified write.");
    }

}
