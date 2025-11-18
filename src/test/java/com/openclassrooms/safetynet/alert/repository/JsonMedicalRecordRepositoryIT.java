// filepath:
// /home/lionel/workspace/alert/src/test/java/com/openclassrooms/safetynet/alert/repository/JsonMedicalRecordRepositoryIT.java
package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.TestSentenceGenerator;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class JsonMedicalRecordRepositoryIT {

    private static final String EXISTING_FIRST_NAME = "John";
    private static final String EXISTING_LAST_NAME = "Doe";
    private static final String NEW_FIRST_NAME = "Jane";
    private static final String NEW_LAST_NAME = "Smith";

    @Autowired JsonMedicalRecordRepository medicalRecordRepository;
    @Autowired JsonFileDataStore store;

    @BeforeEach
    void setup() {
        store.load();
    }

    @AfterEach
    void cleanup() throws Exception {
        Path current = Path.of(store.current());
        if (Files.exists(current)) {
            Files.delete(current);
        }
    }

    @Test
    @DisplayName("save should persist a new medical record and make it retrievable")
    void save_shouldPersistRecordAndRetrieveIt_whenNewRecord() throws Exception {
        Date birthdate = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1995");
        MedicalRecord recordToSave =
                new MedicalRecord(
                        NEW_FIRST_NAME,
                        NEW_LAST_NAME,
                        birthdate,
                        List.of("ibuprofene:200mg"),
                        List.of("pollen"));

        medicalRecordRepository.save(recordToSave);

        MedicalRecord saved =
                medicalRecordRepository.findByFirstNameAndLastName(NEW_FIRST_NAME, NEW_LAST_NAME);

        assertNotNull(saved);
        assertEquals(NEW_FIRST_NAME, saved.getFirstName());
        assertEquals(NEW_LAST_NAME, saved.getLastName());
        assertEquals(1, saved.getMedications().size());
        assertEquals("ibuprofene:200mg", saved.getMedications().getFirst());
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
        MedicalRecord found =
                medicalRecordRepository.findByFirstNameAndLastName(
                        EXISTING_FIRST_NAME, EXISTING_LAST_NAME);
        assertNotNull(found);
        assertEquals(EXISTING_FIRST_NAME, found.getFirstName());
        assertEquals(EXISTING_LAST_NAME, found.getLastName());
        assertTrue(found.getMedications().contains("aznol:350mg"));
        assertEquals("nillacilan", found.getAllergies().getFirst());
    }
}
