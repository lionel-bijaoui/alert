package com.openclassrooms.safetynet.alert.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class JsonFileDataStoreTests {

    final String currentPathStr = "target/current.json";
    final String initialPathStr = "src/test/resources/initial.json";

    JsonFileDataStore store;

    @Mock FilesOperations fileOperations;

    @TempDir Path tempDirectory;

    @BeforeEach
    void setup() {
        store = new JsonFileDataStore(currentPathStr, initialPathStr, fileOperations);
    }

    @Test
    void load_shouldCopyInitialToCurrent_whenCurrentPathMissing() throws Exception {
        when(fileOperations.notExists(any(Path.class))).thenReturn(true);
        doNothing().when(fileOperations).createDirectories(any(Path.class));
        doNothing()
                .when(fileOperations)
                .copy(any(Path.class), any(Path.class), any(StandardCopyOption.class));

        store.load();

        verify(fileOperations, times(2)).notExists(any(Path.class));
        verify(fileOperations).createDirectories(any(Path.class));
        verify(fileOperations)
                .copy(any(Path.class), any(Path.class), any(StandardCopyOption.class));
    }

    @Test
    void readAll_shouldReturnDatabase_whenCurrentFilePresent() throws Exception {
        String json =
                "{"
                        + "\"persons\":[{\"firstName\":\"John\",\"lastName\":\"Doe\"}],"
                        + "\"firestations\":[{\"station\":1}],\"medicalrecords\":[{\"record\":1}]}";
        Path textFile = tempDirectory.resolve("current.json");
        Files.writeString(textFile, json, StandardCharsets.UTF_8);
        File jsonFile = textFile.toFile();

        when(fileOperations.getFile(currentPathStr)).thenReturn(jsonFile);

        Optional<Database> result = store.readAll();

        verify(fileOperations).getFile(currentPathStr);

        assertTrue(result.isPresent());
        result.ifPresent(
                database -> {
                    assertTrue(database.getPersons() != null && !database.getPersons().isEmpty());
                    assertTrue(
                            database.getFirestations() != null
                                    && !database.getFirestations().isEmpty());
                    assertTrue(
                            database.getMedicalrecords() != null
                                    && !database.getMedicalrecords().isEmpty());
                });
    }

    @Test
    void writeAll_shouldPreserveDatabase_whenWritingSameDatabase() {
        List<Person> persons = new ArrayList<>();
        Person person =
                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "");
        persons.add(person);
        Database database = new Database(persons, new ArrayList<>(), new ArrayList<>());

        File jsonFile = tempDirectory.resolve("current.json").toFile();
        when(fileOperations.getFile(any(String.class))).thenReturn(jsonFile);

        store.writeAll(database);

        verify(fileOperations).getFile(any(String.class));

        Optional<Database> maybeAfter = store.readAll();
        assertTrue(maybeAfter.isPresent());
        Database after = maybeAfter.get();
        assertEquals(database.getPersons().size(), after.getPersons().size());
        assertEquals(database.getFirestations().size(), after.getFirestations().size());
        assertEquals(database.getMedicalrecords().size(), after.getMedicalrecords().size());
    }

    @Test
    void writeAll_shouldPersistChanges_whenDatabaseModified() {
        Database newDatabase =
                new Database(
                        List.of(
                                new Person(
                                        "TestFirstName",
                                        "TestLastName",
                                        "123 Test St",
                                        "TestCity",
                                        "12345",
                                        "123-456-7890",
                                        "")),
                        new ArrayList<>(),
                        null);

        File jsonFile = tempDirectory.resolve("current.json").toFile();
        when(fileOperations.getFile(any(String.class))).thenReturn(jsonFile);

        store.writeAll(newDatabase);

        verify(fileOperations).getFile(any(String.class));

        Optional<Database> maybeAfter = store.readAll();
        assertTrue(maybeAfter.isPresent());
        Database after = maybeAfter.get();

        assertFalse(after.getPersons().isEmpty());
        assertEquals(1, after.getPersons().size());
        assertTrue(after.getFirestations().isEmpty());
        assertNull(after.getMedicalrecords());
    }
}
