package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
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
class JsonMedicalRecordRepositoryTest {

    @Mock JsonFileDataStore jsonFileDataStore;

    @InjectMocks JsonMedicalRecordRepository medicalRecordRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void save_shouldSaveMedicalRecordAndPersist_whenNewRecord() {
        MedicalRecord record = new MedicalRecordTestBuilder().build();

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        medicalRecordRepository.save(record);

        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertTrue(
                database.getMedicalRecords().contains(record),
                "The medical record should be added to the database");
    }

    @Test
    void save_shouldUpdateExistingMedicalRecord_whenRecordExists() {
        MedicalRecord existing = new MedicalRecordTestBuilder().build();
        MedicalRecord updated =
                new MedicalRecordTestBuilder().withMedications(List.of("aznol:350mg")).build();
        database.getMedicalRecords().add(existing);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        medicalRecordRepository.save(updated);

        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertEquals(
                1,
                database.getMedicalRecords().size(),
                "There should still be only one medical record in the database.");
        assertEquals(
                updated.getMedications(),
                database.getMedicalRecords().getFirst().getMedications(),
                "The medical record should be updated in the database");
    }

    @Test
    void findAll_shouldReturnMedicalRecords_whenDatabaseHasRecords() {
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        database.getMedicalRecords().add(medicalRecord);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size(), "There should be one medical record in the returned list.");
        assertEquals(medicalRecord.getFirstName(), result.getFirst().getFirstName());
        assertEquals(medicalRecord.getLastName(), result.getFirst().getLastName());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        Database emptyDatabase =
                new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(jsonFileDataStore.getCachedDatabase()).thenReturn(emptyDatabase);

        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned medical records list should be empty.");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullMedicalRecords() {
        database.setMedicalRecords(null);
        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned medical records list should be empty.");
    }

    @Test
    void findByFirstNameAndLastName_shouldBeCaseInsensitiveAndFindRecord_whenMatchingExists() {
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        database.getMedicalRecords().add(medicalRecord);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        medicalRecord.getFirstName().toLowerCase(),
                        medicalRecord.getLastName().toUpperCase());

        found.ifPresentOrElse(
                mr -> {
                    assertEquals(medicalRecord.getFirstName(), mr.getFirstName());
                    assertEquals(medicalRecord.getLastName(), mr.getLastName());
                },
                () -> fail("Medical record should be found."));
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnNull_whenNoMatchFound() {
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        database.getMedicalRecords().add(medicalRecord);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName("Not", "Here");

        found.ifPresent(mr -> fail("No medical record should be found."));
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDeleteRecord_whenRecordExists() {
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        database.getMedicalRecords().add(medicalRecord);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        medicalRecordRepository.deleteByFirstNameAndLastName("John", "Doe");

        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertTrue(
                database.getMedicalRecords().isEmpty(),
                "The medical record should be deleted from the database.");
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDoNothing_whenRecordDoesNotExist() {
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        database.getMedicalRecords().add(medicalRecord);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        medicalRecordRepository.deleteByFirstNameAndLastName("Unknown", "Person");

        verify(jsonFileDataStore, times(1)).setCachedDatabase(database);
        assertEquals(
                1,
                database.getMedicalRecords().size(),
                "The database should still contain the original medical record.");
    }
}
