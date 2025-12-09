package com.openclassrooms.safetynet.alert.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynet.alert.configuration.DataStoreProperties;
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

        DataStoreProperties dataStoreProperties = new DataStoreProperties();
        dataStoreProperties.setCurrent(CURRENT_PATH_STRING);
        dataStoreProperties.setInitial(INITIAL_PATH_STRING);

        store = new JsonFileDataStore(mapper, dataStoreProperties, fileOperations);
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
    void load_shouldThrowRuntimeException_whenIOExceptionOccursDuringLoad() throws IOException {
        when(fileOperations.notExists(any(Path.class))).thenReturn(true);
        doNothing().when(fileOperations).createDirectories(any(Path.class));
        doThrow(IOException.class)
                .when(fileOperations)
                .copy(any(Path.class), any(Path.class), any(StandardCopyOption.class));

        assertThrows(RuntimeException.class, () -> store.load());

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
                    assertNotNull(database.getFireStations());
                    assertFalse(database.getFireStations().isEmpty());
                    assertNotNull(database.getMedicalRecords());
                    assertFalse(database.getMedicalRecords().isEmpty());
                },
                () -> fail("Database should be present"));
    }

    @Test
    void readAll_shouldReturnEmptyOptional_whenCurrentFileMissing() {
        File jsonFile = new File("nonexistent.json");
        when(fileOperations.getFile(CURRENT_PATH_STRING)).thenReturn(jsonFile);

        Optional<Database> optionalDatabase = store.readAll();

        verify(fileOperations).getFile(CURRENT_PATH_STRING);
        assertTrue(optionalDatabase.isEmpty());
    }

    @Test
    void readAll_shouldThrowIllegalStateException_whenIOExceptionOccurs() throws IOException {
        // Unlike other tests, we need to re-initialize the store with the mocked ObjectMapper
        ObjectMapper objectMapper = mock(ObjectMapper.class);

        DataStoreProperties dataStoreProperties = new DataStoreProperties();
        dataStoreProperties.setCurrent(CURRENT_PATH_STRING);
        dataStoreProperties.setInitial(INITIAL_PATH_STRING);

        store = new JsonFileDataStore(objectMapper, dataStoreProperties, fileOperations);

        File jsonFile = mock(File.class);
        when(jsonFile.exists()).thenReturn(true);
        when(jsonFile.isFile()).thenReturn(true);
        when(jsonFile.length()).thenReturn(100L);
        when(fileOperations.getFile(anyString())).thenReturn(jsonFile);
        when(objectMapper.readValue(any(File.class), eq(Database.class)))
                .thenThrow(new IOException("Simulated read error"));

        RuntimeException exception =
                assertThrows(IllegalStateException.class, () -> store.readAll());
        assertEquals("Cannot read database file", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void writeAll_shouldPreserveDatabase_whenWritingSameDatabase() {
        Database database =
                new Database(
                        new ArrayList<>(List.of(new PersonTestBuilder().build())),
                        new ArrayList<>(),
                        new ArrayList<>());

        File jsonFile = tempDirectory.resolve("current.json").toFile();
        when(fileOperations.getFile(anyString())).thenReturn(jsonFile);

        store.writeAll(database);

        verify(fileOperations).getFile(anyString());

        Optional<Database> optionalDatabase = store.readAll();
        optionalDatabase.ifPresentOrElse(
                after -> {
                    assertEquals(database.getPersons().size(), after.getPersons().size());
                    assertEquals(database.getFireStations().size(), after.getFireStations().size());
                    assertEquals(
                            database.getMedicalRecords().size(), after.getMedicalRecords().size());
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
                        new ArrayList<>());

        File jsonFile = tempDirectory.resolve("current.json").toFile();
        when(fileOperations.getFile(anyString())).thenReturn(jsonFile);

        store.writeAll(newDatabase);

        verify(fileOperations).getFile(anyString());

        Optional<Database> optionalDatabase = store.readAll();
        optionalDatabase.ifPresentOrElse(
                database -> {
                    assertEquals(1, database.getPersons().size());
                    assertTrue(database.getFireStations().isEmpty());
                    assertInstanceOf(List.class, database.getMedicalRecords());
                },
                () -> fail("Database should be present"));
    }

    @Test
    void writeAll_shouldThrowRuntimeException_whenIOExceptionOccurs() throws IOException {
        // Unlike other tests, we need to re-initialize the store with the mocked ObjectMapper
        ObjectMapper objectMapper = mock(ObjectMapper.class);

        DataStoreProperties dataStoreProperties = new DataStoreProperties();
        dataStoreProperties.setCurrent(CURRENT_PATH_STRING);
        dataStoreProperties.setInitial(INITIAL_PATH_STRING);

        store = new JsonFileDataStore(objectMapper, dataStoreProperties, fileOperations);

        Database testDb = new Database(List.of(), List.of(), List.of());
        File mockFile = mock(File.class);
        when(fileOperations.getFile(anyString())).thenReturn(mockFile);
        doThrow(new IOException("Simulated write error"))
                .when(objectMapper)
                .writeValue(any(File.class), any(Database.class));

        // Act & Assert
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> store.writeAll(testDb));
        assertEquals("Failed to write JSON file", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void init_shouldLoadAndCacheDatabase_whenCalled() throws IOException {
        // Prepare a temporary current file
        Path currentPath = tempDirectory.resolve("current.json");
        Files.copy(Path.of(INITIAL_PATH_STRING), currentPath, StandardCopyOption.REPLACE_EXISTING);

        when(fileOperations.notExists(any(Path.class))).thenReturn(false);
        when(fileOperations.getFile(CURRENT_PATH_STRING)).thenReturn(currentPath.toFile());

        store.init();

        Database cachedDb = store.getCachedDatabase();
        assertNotNull(cachedDb);
        assertFalse(cachedDb.getPersons().isEmpty());
        assertFalse(cachedDb.getFireStations().isEmpty());
        assertFalse(cachedDb.getMedicalRecords().isEmpty());
    }

    @Test
    void init_shouldSetEmptyDatabase_whenReadAllReturnsEmpty() {
        when(fileOperations.getFile(CURRENT_PATH_STRING)).thenReturn(new File("nonexistent.json"));

        store.init();

        Database cachedDb = store.getCachedDatabase();
        assertNotNull(cachedDb);
        assertTrue(cachedDb.getPersons().isEmpty());
        assertTrue(cachedDb.getFireStations().isEmpty());
        assertTrue(cachedDb.getMedicalRecords().isEmpty());
    }
}
