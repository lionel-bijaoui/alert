// filepath:
// /home/lionel/workspace/alert/src/test/java/com/openclassrooms/safetynet/alert/repository/JsonMedicalRecordRepositoryIT.java
package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class JsonMedicalRecordRepositoryIT extends IntegrationTestBase {

    private static final String EXISTING_FIRST_NAME = "John";
    private static final String EXISTING_LAST_NAME = "Doe";
    private static final String NEW_FIRST_NAME = "Jane";
    private static final String NEW_LAST_NAME = "Smith";
    private static final String NEW_BIRTHDATE = "1995-01-01";
    private static final String MEDICATION = "ibuprofene:200mg";
    private static final String ALLERGY = "pollen";

    @Autowired JsonMedicalRecordRepository medicalRecordRepository;

    @Test
    @DisplayName("save should persist a new medical record and make it retrievable")
    void save_shouldPersistRecordAndRetrieveIt_whenNewRecord() {
        LocalDate birthdate = LocalDate.parse(NEW_BIRTHDATE);
        MedicalRecord recordToSave =
                new MedicalRecord(
                        NEW_FIRST_NAME,
                        NEW_LAST_NAME,
                        birthdate,
                        List.of(MEDICATION),
                        List.of(ALLERGY));

        medicalRecordRepository.save(recordToSave);

        Optional<MedicalRecord> saved =
                medicalRecordRepository.findByFirstNameAndLastName(NEW_FIRST_NAME, NEW_LAST_NAME);
        saved.ifPresentOrElse(
                mr -> {
                    assertEquals(NEW_FIRST_NAME, mr.getFirstName());
                    assertEquals(NEW_LAST_NAME, mr.getLastName());
                    assertEquals(birthdate, mr.getBirthdate());
                    assertEquals(1, mr.getMedications().size());
                    assertEquals(MEDICATION, mr.getMedications().getFirst());
                    assertEquals(1, mr.getAllergies().size());
                    assertEquals(ALLERGY, mr.getAllergies().getFirst());
                },
                () -> fail("The saved medical record should be retrievable"));
    }

    @Test
    void findAll_shouldReturnEntriesFromFile_whenStoreLoaded() {
        List<MedicalRecord> all = medicalRecordRepository.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(
                all.stream()
                        .anyMatch(
                                mr ->
                                        EXISTING_FIRST_NAME.equals(mr.getFirstName())
                                                && EXISTING_LAST_NAME.equals(mr.getLastName())));
    }

    @Test
    void findByFirstNameAndLastName_shouldReturnRecordForExistingPerson() {
        Optional<MedicalRecord> found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        EXISTING_FIRST_NAME, EXISTING_LAST_NAME);
        found.ifPresentOrElse(
                mr -> {
                    assertEquals(EXISTING_FIRST_NAME, mr.getFirstName());
                    assertEquals(EXISTING_LAST_NAME, mr.getLastName());
                    assertTrue(mr.getMedications().contains("aznol:350mg"));
                    assertEquals("nillacilan", mr.getAllergies().getFirst());
                },
                () -> fail("Medical record should be found."));
    }
}
