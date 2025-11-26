// filepath:
// /home/lionel/workspace/alert/src/test/java/com/openclassrooms/safetynet/alert/repository/JsonMedicalRecordRepositoryIT.java
package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class JsonMedicalRecordRepositoryIT extends IntegrationTestBase {

    @Autowired JsonMedicalRecordRepository medicalRecordRepository;

    @Test
    void save_shouldPersistRecordAndRetrieveIt_whenNewRecord() {
        MedicalRecord recordToSave =
                new MedicalRecordTestBuilder().withFirstName("Jane").withLastName("Smith").build();

        medicalRecordRepository.save(recordToSave);

        Optional<MedicalRecord> saved =
                medicalRecordRepository.findByFirstNameAndLastName(
                        recordToSave.getFirstName(), recordToSave.getLastName());
        saved.ifPresentOrElse(
                mr -> {
                    assertEquals(recordToSave.getFirstName(), mr.getFirstName());
                    assertEquals(recordToSave.getLastName(), mr.getLastName());
                    assertEquals(recordToSave.getBirthdate(), mr.getBirthdate());
                    assertEquals(2, mr.getMedications().size());
                    assertEquals(recordToSave.getMedications(), mr.getMedications());
                    assertEquals(
                            recordToSave.getMedications().getFirst(),
                            mr.getMedications().getFirst());
                    assertEquals(
                            recordToSave.getMedications().getLast(), mr.getMedications().getLast());
                    assertEquals(1, mr.getAllergies().size());
                    assertEquals(recordToSave.getAllergies(), mr.getAllergies());
                },
                () -> fail("The saved medical record should be retrievable"));
    }

    @Test
    void findAll_shouldReturnEntriesFromFile_whenStoreLoaded() {
        MedicalRecord existingRecord = new MedicalRecordTestBuilder().build();
        List<MedicalRecord> all = medicalRecordRepository.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(
                all.stream()
                        .anyMatch(
                                mr ->
                                        existingRecord.getFirstName().equals(mr.getFirstName())
                                                && existingRecord
                                                        .getLastName()
                                                        .equals(mr.getLastName())));
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnRecordForExistingPerson() {
        MedicalRecord existingRecord = new MedicalRecordTestBuilder().build();

        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        existingRecord.getFirstName(), existingRecord.getLastName());

        found.ifPresentOrElse(
                mr -> {
                    assertEquals(existingRecord.getFirstName(), mr.getFirstName());
                    assertEquals(existingRecord.getLastName(), mr.getLastName());
                    assertEquals(existingRecord.getMedications(), mr.getMedications());
                    assertEquals(existingRecord.getAllergies(), mr.getAllergies());
                },
                () -> fail("Medical record should be found."));
    }
}
