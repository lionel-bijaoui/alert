package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.dto.PersonWithAge;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class MedicalRecordServiceTest {

    @Mock MedicalRecordRepository medicalRecordRepository;

    @InjectMocks MedicalRecordService medicalRecordService;

    MedicalRecord existingMedicalRecord;

    @BeforeEach
    void setUp() {
        existingMedicalRecord = new MedicalRecordTestBuilder().build();
    }

    @Test
    void addMedicalRecord_shouldReturnSavedMedicalRecord_whenNotPresent() {
        MedicalRecord newMedicalRecord =
                new MedicalRecordTestBuilder()
                        .withFirstName("Jane")
                        .withLastName("Doe")
                        .withMedications(List.of())
                        .withAllergies(List.of())
                        .build();

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        newMedicalRecord.getFirstName(), newMedicalRecord.getLastName()))
                .thenReturn(Optional.empty());
        when(medicalRecordRepository.save(newMedicalRecord)).thenReturn(newMedicalRecord);

        MedicalRecord result = medicalRecordService.addMedicalRecord(newMedicalRecord);

        assertNotNull(result);
        assertEquals(newMedicalRecord.getFirstName(), result.getFirstName());
        assertEquals(newMedicalRecord.getLastName(), result.getLastName());
    }

    @Test
    void addMedicalRecord_shouldThrowConflictException_whenRecordExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(
                        existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName()))
                .thenReturn(Optional.of(existingMedicalRecord));

        assertThrows(
                ConflictException.class,
                () -> medicalRecordService.addMedicalRecord(existingMedicalRecord));

        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void updateMedicalRecord_shouldReturnUpdatedMedicalRecord_whenExists() {
        MedicalRecord updatedMedicalRecord =
                new MedicalRecordTestBuilder().withMedications(List.of("aznol:350mg")).build();

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        updatedMedicalRecord.getFirstName(), updatedMedicalRecord.getLastName()))
                .thenReturn(Optional.of(updatedMedicalRecord));
        when(medicalRecordRepository.save(updatedMedicalRecord)).thenReturn(updatedMedicalRecord);

        MedicalRecord result = medicalRecordService.updateMedicalRecord(updatedMedicalRecord);

        assertNotNull(result);
        assertEquals(updatedMedicalRecord.getFirstName(), result.getFirstName());
        assertEquals(updatedMedicalRecord.getLastName(), result.getLastName());
        assertEquals(updatedMedicalRecord.getMedications(), result.getMedications());
    }

    @Test
    void updateMedicalRecord_shouldThrowResourceNotFoundException_whenNotExists() {
        MedicalRecord updated =
                new MedicalRecordTestBuilder()
                        .withFirstName("Unknown")
                        .withLastName("Person")
                        .build();

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        updated.getFirstName(), updated.getLastName()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicalRecordService.updateMedicalRecord(updated));

        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void deleteMedicalRecord_shouldRemoveMedicalRecord_whenExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(
                        existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName()))
                .thenReturn(Optional.of(existingMedicalRecord));
        doNothing()
                .when(medicalRecordRepository)
                .deleteByFirstNameAndLastName(
                        existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName());

        medicalRecordService.deleteMedicalRecord(
                existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName());

        verify(medicalRecordRepository, times(1))
                .deleteByFirstNameAndLastName(
                        existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName());
    }

    @Test
    void deleteMedicalRecord_shouldThrowResourceNotFoundException_whenNotExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicalRecordService.deleteMedicalRecord("Not", "Here"));

        verify(medicalRecordRepository, never()).deleteByFirstNameAndLastName(any(), any());
    }

    @Test
    void getMedicalRecordByFullName_shouldReturnRecord_whenExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(
                        existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName()))
                .thenReturn(Optional.ofNullable(existingMedicalRecord));

        Optional<MedicalRecord> optionalMedicalRecord =
                medicalRecordService.getMedicalRecordByFullName(
                        existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName());

        optionalMedicalRecord.ifPresentOrElse(
                mr -> {
                    assertEquals(existingMedicalRecord, mr);
                    assertEquals(existingMedicalRecord.getFirstName(), mr.getFirstName());
                    assertEquals(existingMedicalRecord.getLastName(), mr.getLastName());
                },
                () -> fail("Expected medical record to be present"));
    }

    @Test
    void getMedicalRecordByFullName_shouldReturnNull_whenNotExists() {
        when(medicalRecordRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        Optional<MedicalRecord> optionalMedicalRecord =
                medicalRecordService.getMedicalRecordByFullName("No", "Body");

        assertTrue(optionalMedicalRecord.isEmpty());
    }

    @Test
    void calculateAgeFromBirthdate_shouldReturnCorrectAge_whenBirthdateIsCorrect() {
        int expected = 25;
        LocalDate birthdate = LocalDate.now().minusYears(expected);

        Integer age = medicalRecordService.calculateAgeFromBirthdate(birthdate);

        assertNotNull(age);
        assertEquals(expected, age);
    }

    @Test
    void calculateAgeFromBirthdate_shouldThrow_whenBirthdateIsIncorrect() {
        LocalDate futureBirthdate = LocalDate.now().plusYears(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> medicalRecordService.calculateAgeFromBirthdate(futureBirthdate));
    }

    @Test
    void enrichPersonsWithAge_shouldEnrichPersons_whenMedicalRecordExists() {
        Person person = new PersonTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        List<Person> persons = List.of(person);

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName()))
                .thenReturn(Optional.of(medicalRecord));

        Stream<PersonWithAge> enrichedStream = medicalRecordService.enrichPersonsWithAge(persons);

        List<PersonWithAge> enrichedList = enrichedStream.toList();
        assertEquals(1, enrichedList.size());
        assertEquals(41, enrichedList.getFirst().age());
    }

    @Test
    void enrichPersonsWithAge_shouldThrow_whenMedicalRecordNotExists() {
        Person person = new PersonTestBuilder().build();
        List<Person> persons = List.of(person);

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> medicalRecordService.enrichPersonsWithAge(persons).toList());
    }

    @Test
    void mapToPersonWithMedicalInfosDTO_shouldReturnDTO_whenMedicalRecordExists() {
        Person person = new PersonTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName()))
                .thenReturn(Optional.of(medicalRecord));

        var dto = medicalRecordService.mapToPersonWithMedicalInfosDTO(person);

        assertNotNull(dto);
        assertEquals(person.getFirstName(), dto.firstName());
        assertEquals(person.getLastName(), dto.lastName());
        assertEquals(medicalRecord.getMedications(), dto.medications());
        assertEquals(medicalRecord.getAllergies(), dto.allergies());
    }

    @Test
    void
            mapToPersonWithMedicalInfosDTO_shouldReturnDTOWithoutMedicalInfos_whenMedicalRecordNotExists() {
        Person person = new PersonTestBuilder().build();

        when(medicalRecordRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName()))
                .thenReturn(Optional.empty());

        var dto = medicalRecordService.mapToPersonWithMedicalInfosDTO(person);

        assertNotNull(dto);
        assertEquals(person.getFirstName(), dto.firstName());
        assertEquals(person.getLastName(), dto.lastName());
        assertTrue(dto.medications().isEmpty());
        assertTrue(dto.allergies().isEmpty());
    }
}
