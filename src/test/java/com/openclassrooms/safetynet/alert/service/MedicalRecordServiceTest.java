package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class MedicalRecordServiceTest {

    static final String FIRST_NAME = "John";
    static final String LAST_NAME = "Doe";

    @Mock MedicalRecordRepository medicalRecordRepository;

    @InjectMocks MedicalRecordService medicalRecordService;

    MedicalRecord existingMedicalRecord;

    @BeforeEach
    void setUp() {
        existingMedicalRecord =
                new MedicalRecord(
                        FIRST_NAME, LAST_NAME, null, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void addMedicalRecord_shouldReturnSavedMedicalRecord_whenNotPresent() {
        MedicalRecord newMedicalRecord =
                new MedicalRecord(
                        FIRST_NAME, LAST_NAME, null, new ArrayList<>(), new ArrayList<>());

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        newMedicalRecord.getFirstName(), newMedicalRecord.getLastName()))
                .thenReturn(Optional.empty());
        when(medicalRecordRepository.save(newMedicalRecord)).thenReturn(newMedicalRecord);

        MedicalRecord result = medicalRecordService.addMedicalRecord(newMedicalRecord);

        assertNotNull(result);
        assertEquals(newMedicalRecord.getFirstName(), result.getFirstName());
        assertEquals(newMedicalRecord.getLastName(), result.getLastName());

        verify(medicalRecordRepository, times(1))
                .findByFirstNameAndLastName(
                        newMedicalRecord.getFirstName(), newMedicalRecord.getLastName());
        verify(medicalRecordRepository, times(1)).save(newMedicalRecord);
    }

    @Test
    void addMedicalRecord_shouldThrowConflictException_whenRecordExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(FIRST_NAME, LAST_NAME))
                .thenReturn(Optional.of(existingMedicalRecord));

        assertThrows(
                ConflictException.class,
                () -> medicalRecordService.addMedicalRecord(existingMedicalRecord));

        verify(medicalRecordRepository, times(1)).findByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void updateMedicalRecord_shouldReturnUpdatedMedicalRecord_whenExists() {
        MedicalRecord updatedMedicalRecord =
                new MedicalRecord(
                        FIRST_NAME,
                        LAST_NAME,
                        null,
                        new ArrayList<>(List.of("aznol:350mg")),
                        new ArrayList<>());

        when(medicalRecordRepository.findByFirstNameAndLastName(FIRST_NAME, LAST_NAME))
                .thenReturn(Optional.of(updatedMedicalRecord));
        when(medicalRecordRepository.save(updatedMedicalRecord)).thenReturn(updatedMedicalRecord);

        MedicalRecord result = medicalRecordService.updateMedicalRecord(updatedMedicalRecord);

        assertNotNull(result);
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(LAST_NAME, result.getLastName());
        assertEquals(1, result.getMedications().size());

        verify(medicalRecordRepository, times(1)).findByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
        verify(medicalRecordRepository, times(1)).save(updatedMedicalRecord);
    }

    @Test
    void updateMedicalRecord_shouldThrowResourceNotFoundException_whenNotExists() {
        MedicalRecord updated =
                new MedicalRecord("Unknown", "Person", null, new ArrayList<>(), new ArrayList<>());

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        updated.getFirstName(), updated.getLastName()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicalRecordService.updateMedicalRecord(updated));

        verify(medicalRecordRepository, times(1))
                .findByFirstNameAndLastName(updated.getFirstName(), updated.getLastName());
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void deleteMedicalRecord_shouldRemoveMedicalRecord_whenExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(FIRST_NAME, LAST_NAME))
                .thenReturn(Optional.of(existingMedicalRecord));
        doNothing()
                .when(medicalRecordRepository)
                .deleteByFirstNameAndLastName(FIRST_NAME, LAST_NAME);

        medicalRecordService.deleteMedicalRecord(FIRST_NAME, LAST_NAME);

        verify(medicalRecordRepository, times(1)).findByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
        verify(medicalRecordRepository, times(1))
                .deleteByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
    }

    @Test
    void deleteMedicalRecord_shouldThrowResourceNotFoundException_whenNotExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName("Not", "Here"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicalRecordService.deleteMedicalRecord("Not", "Here"));

        verify(medicalRecordRepository, times(1)).findByFirstNameAndLastName("Not", "Here");
        verify(medicalRecordRepository, never()).deleteByFirstNameAndLastName(any(), any());
    }

    // New tests added below

    @Test
    void getMedicalRecordByFullName_shouldReturnRecord_whenExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(FIRST_NAME, LAST_NAME))
                .thenReturn(Optional.ofNullable(existingMedicalRecord));

        MedicalRecord found =
                medicalRecordService.getMedicalRecordByFullName(FIRST_NAME, LAST_NAME);

        assertNotNull(found);
        assertEquals(FIRST_NAME, found.getFirstName());
        assertEquals(LAST_NAME, found.getLastName());

        verify(medicalRecordRepository, times(1)).findByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
    }

    @Test
    void getMedicalRecordByFullName_shouldReturnNull_whenNotExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName("No", "Body"))
                .thenReturn(Optional.empty());

        MedicalRecord found = medicalRecordService.getMedicalRecordByFullName("No", "Body");

        assertNull(found);
        verify(medicalRecordRepository, times(1)).findByFirstNameAndLastName("No", "Body");
    }

    @Test
    void calculateAgeFromBirthdate_shouldReturnCorrectAge_whenBirthdateIsCorrect() {
        LocalDate birthdate = LocalDate.now().minusYears(25);

        Integer age = medicalRecordService.calculateAgeFromBirthdate(birthdate);

        assertNotNull(age);
        assertEquals(25, age);
    }

    @Test
    void calculateAgeFromBirthdate_shouldThrow_whenBirthdateIsIncorrect() {
        LocalDate futureBirthdate = LocalDate.now().plusYears(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> medicalRecordService.calculateAgeFromBirthdate(futureBirthdate));
    }
}
