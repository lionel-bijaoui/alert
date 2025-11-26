package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
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
class JsonFireStationRepositoryIT extends IntegrationTestBase {

    @Autowired JsonFireStationRepository fireStationRepository;

    @Test
    void save_shouldPersistFireStationAndRetrieveIt_whenNewFireStation() {
        FireStation toSave = new FireStationTestBuilder().build();

        fireStationRepository.save(toSave);

        Optional<FireStation> saved = fireStationRepository.findByAddress(toSave.getAddress());
        saved.ifPresentOrElse(
                fireStation -> {
                    assertEquals(toSave.getAddress(), fireStation.getAddress());
                    assertEquals(toSave.getStation(), fireStation.getStation());
                },
                () -> fail("The saved fire station should be retrievable"));
    }

    @Test
    void findAll_shouldReturnEntriesFromFile_whenStoreLoaded() {
        FireStation existing = new FireStationTestBuilder().build();

        List<FireStation> all = fireStationRepository.findAll();

        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(
                all.stream()
                        .anyMatch(
                                fs ->
                                        existing.getAddress().equals(fs.getAddress())
                                                && existing.getStation().equals(fs.getStation())));
    }

    @Test
    void findByAddress_shouldReturnExistingRecord() {
        FireStation existing = new FireStationTestBuilder().build();

        Optional<FireStation> found = fireStationRepository.findByAddress(existing.getAddress());

        assertTrue(found.isPresent());
        assertEquals(existing.getAddress(), found.get().getAddress());
        assertEquals(existing.getStation(), found.get().getStation());
    }

    @Test
    void deleteByAddress_shouldRemoveRecord_whenAddressExists() {
        String address = "29 15th St";
        FireStation toDelete = new FireStationTestBuilder().withAddress(address).build();
        fireStationRepository.save(toDelete);

        Optional<FireStation> before = fireStationRepository.findByAddress(address);
        assertTrue(before.isPresent());

        fireStationRepository.deleteByAddress(address);

        Optional<FireStation> after = fireStationRepository.findByAddress(address);
        assertTrue(after.isEmpty());
    }
}
