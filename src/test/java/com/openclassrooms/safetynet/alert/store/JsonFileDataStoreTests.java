package com.openclassrooms.safetynet.alert.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
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

    static final String CURRENT_PATH_STRING = "target/current.json";
    static final String INITIAL_PATH_STRING = "src/test/resources/initial.json";

    @Mock FilesOperations fileOperations;

    @TempDir Path tempDirectory;

    JsonFileDataStore store;

    @BeforeEach
    void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        store =
                new JsonFileDataStore(
                        mapper, CURRENT_PATH_STRING, INITIAL_PATH_STRING, fileOperations);
    }

    @Test
    void load_shouldCopyInitialToCurrent_whenCurrentPathMissing() throws IOException {
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
"""
{
  "persons": [
     {
       "firstName": "John",
       "lastName": "Doe",
       "address": "1509 Culver St",
       "city": "Culver",
       "zip": "97451",
       "phone": "841-874-6512",
       "email": "johndoe@email.com"
     }
   ],
   "firestations": [
     {
       "address": "1509 Culver St",
       "station": "3"
     }
   ],
   "medicalrecords": [
     {
       "firstName": "John",
       "lastName": "Doe",
       "birthdate": "03/18/1984",
       "medications": [
         "aznol:350mg",
         "hydrapermazol:100mg"
       ],
       "allergies": [
         "nillacilan"
       ]
     }
   ]
}
""";
        Path textFile = tempDirectory.resolve("current.json");
        Files.writeString(textFile, json, StandardCharsets.UTF_8);
        File jsonFile = textFile.toFile();

        when(fileOperations.getFile(CURRENT_PATH_STRING)).thenReturn(jsonFile);

        Optional<Database> optionalDatabase = store.readAll();

        verify(fileOperations).getFile(CURRENT_PATH_STRING);
        optionalDatabase.ifPresentOrElse(
                database -> {
                    assertNotNull(database.getPersons());
                    assertFalse(database.getPersons().isEmpty());
                    assertNotNull(database.getFirestations());
                    assertFalse(database.getFirestations().isEmpty());
                    assertNotNull(database.getMedicalrecords());
                    assertFalse(database.getMedicalrecords().isEmpty());
                },
                () -> fail("Database should be present"));
    }

    @Test
    void writeAll_shouldPreserveDatabase_whenWritingSameDatabase() {
        Database database =
                new Database(
                        new ArrayList<>(List.of(new PersonTestBuilder().build())),
                        new ArrayList<>(),
                        new ArrayList<>());

        File jsonFile = tempDirectory.resolve("current.json").toFile();
        when(fileOperations.getFile(any(String.class))).thenReturn(jsonFile);

        store.writeAll(database);

        verify(fileOperations).getFile(any(String.class));

        Optional<Database> optionalDatabase = store.readAll();
        optionalDatabase.ifPresentOrElse(
                after -> {
                    assertEquals(database.getPersons().size(), after.getPersons().size());
                    assertEquals(database.getFirestations().size(), after.getFirestations().size());
                    assertEquals(
                            database.getMedicalrecords().size(), after.getMedicalrecords().size());
                },
                () -> fail("Database should be present"));
    }

    @Test
    void writeAll_shouldPersistChanges_whenDatabaseModified() {
        Database newDatabase =
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
                        new ArrayList<>(),
                        null);

        File jsonFile = tempDirectory.resolve("current.json").toFile();
        when(fileOperations.getFile(any(String.class))).thenReturn(jsonFile);

        store.writeAll(newDatabase);

        verify(fileOperations).getFile(any(String.class));

        Optional<Database> optionalDatabase = store.readAll();
        optionalDatabase.ifPresentOrElse(
                database -> {
                    assertEquals(1, database.getPersons().size());
                    assertTrue(database.getFirestations().isEmpty());
                    assertNull(database.getMedicalrecords());
                },
                () -> fail("Database should be present"));
    }
}
