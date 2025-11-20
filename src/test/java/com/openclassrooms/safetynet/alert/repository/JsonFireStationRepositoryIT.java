package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.FireStation;
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

    private static final String EXISTING_FIRE_STATION_ADDRESS = "1509 Culver St";
    private static final String EXISTING_FIRE_STATION_NUMBER = "3";
    private static final String NEW_FIRE_STATION_ADDRESS = "29 15th St";
    private static final String NEW_FIRE_STATION_NUMBER = "4";

    @Autowired JsonFireStationRepository fireStationRepository;

    @Test
    void save_shouldPersistFireStation_whenNewFireStation() {
        FireStation toSave = new FireStation(NEW_FIRE_STATION_ADDRESS, NEW_FIRE_STATION_NUMBER);

        fireStationRepository.save(toSave);

        Optional<FireStation> saved = fireStationRepository.findByAddress(NEW_FIRE_STATION_ADDRESS);

        assertTrue(saved.isPresent());
        assertEquals(NEW_FIRE_STATION_ADDRESS, saved.get().getAddress());
        assertEquals(NEW_FIRE_STATION_NUMBER, saved.get().getStation());
    }

    @Test
    void findAll_shouldReturnEntriesFromFile_whenStoreLoaded() {
        List<FireStation> all = fireStationRepository.findAll();

        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(
                all.stream()
                        .anyMatch(
                                fs ->
                                        EXISTING_FIRE_STATION_ADDRESS.equals(fs.getAddress())
                                                && EXISTING_FIRE_STATION_NUMBER.equals(
                                                        fs.getStation())));
    }

    @Test
    void findByAddress_shouldReturnExistingRecord() {
        Optional<FireStation> found =
                fireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS);

        assertTrue(found.isPresent());
        assertEquals(EXISTING_FIRE_STATION_ADDRESS, found.get().getAddress());
        assertEquals(EXISTING_FIRE_STATION_NUMBER, found.get().getStation());
    }

    @Test
    void deleteByAddress_shouldRemoveRecord_whenAddressExists() {
        FireStation toDelete = new FireStation(NEW_FIRE_STATION_ADDRESS, NEW_FIRE_STATION_NUMBER);
        fireStationRepository.save(toDelete);

        Optional<FireStation> before =
                fireStationRepository.findByAddress(NEW_FIRE_STATION_ADDRESS);
        assertTrue(before.isPresent());

        fireStationRepository.deleteByAddress(NEW_FIRE_STATION_ADDRESS);

        Optional<FireStation> after = fireStationRepository.findByAddress(NEW_FIRE_STATION_ADDRESS);
        assertTrue(after.isEmpty());
    }
}
