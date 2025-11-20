package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.repository.MedicalRecordRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class MedicalRecordServiceIT extends IntegrationTestBase {

    private static final String EXISTING_FIRST_NAME = "John";
    private static final String EXISTING_LAST_NAME = "Doe";

    private static final String NEW_FIRST_NAME = "Alice";
    private static final String NEW_LAST_NAME = "Zephyr";
    private static final String NEW_BIRTHDATE = "01/01/1990";

    @Autowired private MedicalRecordService medicalRecordService;
    @Autowired private MedicalRecordRepository medicalRecordRepository;

    @Test
    void addMedicalRecord_shouldPersistAndReturn_whenNew() throws Exception {
        Date newBirth = new SimpleDateFormat("dd/MM/yyyy").parse(NEW_BIRTHDATE);

        MedicalRecord toAdd =
                new MedicalRecord(
                        NEW_FIRST_NAME,
                        NEW_LAST_NAME,
                        newBirth,
                        List.of("med1:100mg"),
                        List.of("pollen"));

        MedicalRecord saved = medicalRecordService.addMedicalRecord(toAdd);

        assertNotNull(saved);
        assertEquals(NEW_FIRST_NAME, saved.getFirstName());
        assertEquals(NEW_LAST_NAME, saved.getLastName());
        assertEquals(newBirth, saved.getBirthdate());

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(NEW_FIRST_NAME, NEW_LAST_NAME);
        assertTrue(found.isPresent(), "The new medical record should be present in the repository");
        assertEquals(newBirth, found.get().getBirthdate());
        assertEquals(1, found.get().getMedications().size());
        assertEquals(1, found.get().getAllergies().size());
    }

    @Test
    void updateMedicalRecord_shouldPersistUpdate_whenExisting() throws Exception {
        Optional<MedicalRecord> before =
                medicalRecordRepository.findByFirstNameAndLastName(
                        EXISTING_FIRST_NAME, EXISTING_LAST_NAME);
        assertTrue(
                before.isPresent(),
                "Precondition: existing medical record should be present in fixture");

        Date newBirth = new SimpleDateFormat("dd/MM/yyyy").parse(NEW_BIRTHDATE);

        MedicalRecord updated =
                new MedicalRecord(
                        EXISTING_FIRST_NAME,
                        EXISTING_LAST_NAME,
                        newBirth,
                        List.of("aspirin:500mg"),
                        List.of("none"));

        MedicalRecord result = medicalRecordService.updateMedicalRecord(updated);

        assertNotNull(result);
        assertEquals(EXISTING_FIRST_NAME, result.getFirstName());
        assertEquals(EXISTING_LAST_NAME, result.getLastName());
        assertEquals(newBirth, result.getBirthdate());
        assertEquals(1, result.getMedications().size());

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        EXISTING_FIRST_NAME, EXISTING_LAST_NAME);
        assertTrue(found.isPresent(), "The medical record should still be present after update");
        assertEquals(newBirth, found.get().getBirthdate());
    }

    @Test
    void deleteMedicalRecord_shouldRemove_whenExisting() {
        Optional<MedicalRecord> before =
                medicalRecordRepository.findByFirstNameAndLastName(
                        EXISTING_FIRST_NAME, EXISTING_LAST_NAME);
        assertTrue(before.isPresent(), "Precondition: existing medical record should be present");

        medicalRecordService.deleteMedicalRecord(EXISTING_FIRST_NAME, EXISTING_LAST_NAME);

        Optional<MedicalRecord> after =
                medicalRecordRepository.findByFirstNameAndLastName(
                        EXISTING_FIRST_NAME, EXISTING_LAST_NAME);
        assertTrue(after.isEmpty(), "The medical record should be removed from the repository");
    }
}
