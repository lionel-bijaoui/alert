package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.repository.JsonFireStationRepository;
import com.openclassrooms.safetynet.alert.utils.IntegrationTestBase;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(TestSentenceGenerator.class)
class FireStationServiceIT extends IntegrationTestBase {

    private static final String EXISTING_FIRE_STATION_ADDRESS = "1509 Culver St";
    private static final Integer EXISTING_FIRE_STATION_NUMBER = 3;
    private static final String NEW_FIRE_STATION_ADDRESS = "29 15th St";
    private static final Integer NEW_FIRE_STATION_NUMBER = 4;

    @Autowired private FireStationService fireStationService;
    @Autowired private JsonFireStationRepository fireStationRepository;

    @Test
    void addFireStation_shouldPersistAndReturn_whenNew() {
        FireStation toAdd = new FireStation(NEW_FIRE_STATION_ADDRESS, NEW_FIRE_STATION_NUMBER);

        FireStation saved = fireStationService.addFireStation(toAdd);

        assertNotNull(saved);
        assertEquals(NEW_FIRE_STATION_ADDRESS, saved.getAddress());
        assertEquals(NEW_FIRE_STATION_NUMBER, saved.getStation());

        Optional<FireStation> found = fireStationRepository.findByAddress(NEW_FIRE_STATION_ADDRESS);
        assertTrue(found.isPresent(), "The new fire station should be present in the repository");
        assertEquals(NEW_FIRE_STATION_ADDRESS, found.get().getAddress());
        assertEquals(NEW_FIRE_STATION_NUMBER, found.get().getStation());
    }

    @Test
    void updateFireStation_shouldPersistUpdate_whenExisting() {
        FireStation updated =
                new FireStation(EXISTING_FIRE_STATION_ADDRESS, NEW_FIRE_STATION_NUMBER);

        FireStation result = fireStationService.updateFireStation(updated);

        assertNotNull(result);
        assertEquals(EXISTING_FIRE_STATION_ADDRESS, result.getAddress());
        assertEquals(NEW_FIRE_STATION_NUMBER, result.getStation());

        Optional<FireStation> found =
                fireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS);
        assertTrue(found.isPresent(), "The fire station should still be present after update");
        assertEquals(NEW_FIRE_STATION_NUMBER, found.get().getStation());
    }

    @Test
    void deleteFireStation_shouldRemove_whenExisting() {
        Optional<FireStation> before =
                fireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS);
        assertTrue(before.isPresent(), "Precondition: existing fire station should be present");
        assertEquals(
                EXISTING_FIRE_STATION_NUMBER,
                before.get().getStation(),
                "Precondition: station number should match fixture");

        fireStationService.deleteFireStation(EXISTING_FIRE_STATION_ADDRESS);

        Optional<FireStation> after =
                fireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS);
        assertTrue(after.isEmpty(), "The fire station should be removed from the repository");
    }

    @Test
    void getFireStationAddressListByFireStationNumber_shouldReturnAddresses_whenExisting() {
        var addresses =
                fireStationService.getFireStationAddressListByFireStationNumber(
                        EXISTING_FIRE_STATION_NUMBER);

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty(), "Addresses list should not be empty");
        assertTrue(
                addresses.contains(EXISTING_FIRE_STATION_ADDRESS),
                "Addresses list should contain the existing fire station address");
    }

    @Test
    void getFireStationNumberByAddress_shouldReturnStationNumber_whenExisting() {
        int stationNumber =
                fireStationService.getFireStationNumberByAddress(EXISTING_FIRE_STATION_ADDRESS);

        assertEquals(
                EXISTING_FIRE_STATION_NUMBER,
                stationNumber,
                "The returned station number should match the existing one");
    }

    @Test
    void getFireStationListByFireStationNumberList_shouldReturnFireStations_whenExisting() {
        var stationNumbers = List.of(EXISTING_FIRE_STATION_NUMBER, NEW_FIRE_STATION_NUMBER);

        var fireStations =
                fireStationService.getFireStationListByFireStationNumberList(stationNumbers);

        assertNotNull(fireStations);
        assertFalse(fireStations.isEmpty(), "Fire stations list should not be empty");
        assertTrue(
                fireStations.stream()
                        .anyMatch(
                                fs ->
                                        fs.getAddress().equals(EXISTING_FIRE_STATION_ADDRESS)
                                                && fs.getStation()
                                                        .equals(EXISTING_FIRE_STATION_NUMBER)),
                "Fire stations list should contain the existing fire station");
    }
}
