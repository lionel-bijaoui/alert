package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
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

    static final String FIRST_NAME = "John";
    static final String LAST_NAME = "Doe";

    @Mock JsonFileDataStore jsonFileDataStore;

    @InjectMocks JsonMedicalRecordRepository medicalRecordRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void save_shouldSaveMedicalRecordAndPersist_whenNewRecord() {
        MedicalRecord record = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        medicalRecordRepository.save(record);

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertTrue(
                database.getMedicalrecords().contains(record),
                "The medical record should be added to the database");
    }

    @Test
    void save_shouldUpdateExistingMedicalRecord_whenRecordExists() {
        final String NEW_MED = "aznol:350mg";
        MedicalRecord existing = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);
        database.getMedicalrecords().add(existing);

        MedicalRecord updated =
                new MedicalRecord(
                        FIRST_NAME, LAST_NAME, null, new ArrayList<>(List.of(NEW_MED)), null);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        medicalRecordRepository.save(updated);

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(
                1,
                database.getMedicalrecords().size(),
                "There should still be only one medical record in the database.");
        assertEquals(
                NEW_MED,
                database.getMedicalrecords().getFirst().getMedications().getFirst(),
                "The medical record should be updated in the database");
    }

    @Test
    void findAll_shouldReturnMedicalRecords_whenDatabaseHasRecords() {
        MedicalRecord medicalRecord = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);
        database.getMedicalrecords().add(medicalRecord);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size(), "There should be one medical record in the returned list.");
        assertEquals(FIRST_NAME, result.getFirst().getFirstName());
        assertEquals(LAST_NAME, result.getFirst().getLastName());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        when(jsonFileDataStore.readAll()).thenReturn(Optional.empty());

        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned medical records list should be empty.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullMedicalRecords() {
        database.setMedicalrecords(null);
        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<MedicalRecord> result = medicalRecordRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "The returned medical records list should be empty.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByFirstNameAndLastName_shouldBeCaseInsensitiveAndFindRecord_whenMatchingExists() {
        MedicalRecord medicalRecord = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);
        database.getMedicalrecords().add(medicalRecord);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        FIRST_NAME.toLowerCase(), LAST_NAME.toUpperCase());
        found.ifPresentOrElse(
                mr -> {
                    assertEquals(FIRST_NAME, mr.getFirstName());
                    assertEquals(LAST_NAME, mr.getLastName());
                },
                () -> fail("Medical record should be found."));
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnNull_whenNoMatchFound() {
        MedicalRecord medicalRecord = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);
        database.getMedicalrecords().add(medicalRecord);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName("Not", "Here");
        found.ifPresent(mr -> fail("No medical record should be found."));
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDeleteRecord_whenRecordExists() {
        MedicalRecord medicalRecord = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);
        database.getMedicalrecords().add(medicalRecord);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        medicalRecordRepository.deleteByFirstNameAndLastName(FIRST_NAME, LAST_NAME);

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertTrue(
                database.getMedicalrecords().isEmpty(),
                "The medical record should be deleted from the database.");
    }

    @Test
    void deleteByFirstNameAndLastName_shouldDoNothing_whenRecordDoesNotExist() {
        MedicalRecord medicalRecord = new MedicalRecord(FIRST_NAME, LAST_NAME, null, null, null);
        database.getMedicalrecords().add(medicalRecord);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        medicalRecordRepository.deleteByFirstNameAndLastName("Unknown", "Person");

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(
                1,
                database.getMedicalrecords().size(),
                "The database should still contain the original medical record.");
    }
}
