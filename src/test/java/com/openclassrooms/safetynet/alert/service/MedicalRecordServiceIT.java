package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class MedicalRecordServiceIT extends IntegrationTestBase {

    @Autowired MedicalRecordService medicalRecordService;

    @Autowired MedicalRecordRepository medicalRecordRepository;

    @Test
    void addMedicalRecord_shouldPersistAndReturn_whenNew() {
        MedicalRecord toAdd =
                new MedicalRecordTestBuilder()
                        .withFirstName("Alice")
                        .withLastName("Zephyr")
                        .withBirthdate(LocalDate.parse("1990-01-01"))
                        .withMedications(List.of("med1:100mg"))
                        .withAllergies(List.of("pollen"))
                        .build();

        MedicalRecord saved = medicalRecordService.addMedicalRecord(toAdd);

        assertNotNull(saved);
        assertEquals(toAdd.getFirstName(), saved.getFirstName());
        assertEquals(toAdd.getLastName(), saved.getLastName());
        assertEquals(toAdd.getBirthdate(), saved.getBirthdate());
        assertEquals(toAdd.getMedications(), saved.getMedications());
        assertEquals(toAdd.getAllergies(), saved.getAllergies());

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        toAdd.getFirstName(), toAdd.getLastName());
        assertTrue(found.isPresent(), "The new medical record should be present in the repository");
        assertEquals(toAdd.getFirstName(), found.get().getFirstName());
        assertEquals(toAdd.getLastName(), found.get().getLastName());
        assertEquals(toAdd.getBirthdate(), found.get().getBirthdate());
    }

    @Test
    void updateMedicalRecord_shouldPersistUpdate_whenExisting() {
        MedicalRecord existing = new MedicalRecordTestBuilder().build();
        Optional<MedicalRecord> before =
                medicalRecordRepository.findByFirstNameAndLastName(
                        existing.getFirstName(), existing.getLastName());
        assertTrue(
                before.isPresent(),
                "Precondition: existing medical record should be present in fixture");

        LocalDate newBirth = LocalDate.parse("1990-01-01");
        MedicalRecord updated = new MedicalRecordTestBuilder().withBirthdate(newBirth).build();

        MedicalRecord result = medicalRecordService.updateMedicalRecord(updated);

        assertNotNull(result);
        assertEquals(updated.getFirstName(), result.getFirstName());
        assertEquals(updated.getLastName(), result.getLastName());
        assertEquals(updated.getBirthdate(), result.getBirthdate());
        assertEquals(updated.getMedications(), result.getMedications());
        assertEquals(updated.getAllergies(), result.getAllergies());

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        updated.getFirstName(), updated.getLastName());
        assertTrue(found.isPresent(), "The medical record should still be present after update");
        assertEquals(updated.getFirstName(), found.get().getFirstName());
        assertEquals(updated.getLastName(), found.get().getLastName());
        assertEquals(updated.getBirthdate(), found.get().getBirthdate());
    }

    @Test
    void deleteMedicalRecord_shouldRemove_whenExisting() {
        MedicalRecord existing = new MedicalRecordTestBuilder().build();
        Optional<MedicalRecord> before =
                medicalRecordRepository.findByFirstNameAndLastName(
                        existing.getFirstName(), existing.getLastName());
        assertTrue(before.isPresent(), "Precondition: existing medical record should be present");

        medicalRecordService.deleteMedicalRecord(existing.getFirstName(), existing.getLastName());

        Optional<MedicalRecord> after =
                medicalRecordRepository.findByFirstNameAndLastName(
                        existing.getFirstName(), existing.getLastName());
        assertTrue(after.isEmpty(), "The medical record should be removed from the repository");
    }

    @Test
    void getMedicalRecordByFullName_shouldReturnMedicalRecord_whenExists() {
        MedicalRecord existing = new MedicalRecordTestBuilder().build();
        Optional<MedicalRecord> before =
                medicalRecordRepository.findByFirstNameAndLastName(
                        existing.getFirstName(), existing.getLastName());
        assertTrue(before.isPresent(), "Precondition: existing medical record should be present");

        Optional<MedicalRecord> optionalMedicalRecord =
                medicalRecordService.getMedicalRecordByFullName(
                        existing.getFirstName(), existing.getLastName());

        optionalMedicalRecord.ifPresentOrElse(
                mr -> {
                    assertEquals(existing, mr);
                    assertEquals(existing.getFirstName(), mr.getFirstName());
                    assertEquals(existing.getLastName(), mr.getLastName());
                },
                () -> fail("Expected medical record to be present"));
    }

    @Test
    void enrichPersonsWithAge_shouldEnrichPersons_whenMedicalRecordsExist() {
        Person person = new PersonTestBuilder().build();
        List<Person> persons = List.of(person);

        var enrichedList = medicalRecordService.enrichPersonsWithAge(persons);

        assertEquals(1, enrichedList.size());
        assertEquals(41, enrichedList.getFirst().age());
    }
}
