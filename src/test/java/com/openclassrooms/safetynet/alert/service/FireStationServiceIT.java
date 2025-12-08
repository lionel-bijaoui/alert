package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.repository.JsonFireStationRepository;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
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

    @Autowired FireStationService fireStationService;

    @Autowired JsonFireStationRepository fireStationRepository;

    @Test
    void addFireStation_shouldPersistAndReturn_whenNew() {
        FireStation toAdd =
                new FireStationTestBuilder().withAddress("29 15th St").withStation(4).build();

        FireStation saved = fireStationService.addFireStation(toAdd);

        assertNotNull(saved);
        assertEquals(toAdd.getAddress(), saved.getAddress());
        assertEquals(toAdd.getStation(), saved.getStation());

        Optional<FireStation> found = fireStationRepository.findByAddress(toAdd.getAddress());
        assertTrue(found.isPresent(), "The new fire station should be present in the repository");
        assertEquals(toAdd.getAddress(), found.get().getAddress());
        assertEquals(toAdd.getStation(), found.get().getStation());
    }

    @Test
    void updateFireStation_shouldPersistUpdate_whenExisting() {
        FireStation updated = new FireStationTestBuilder().withStation(99).build();

        FireStation result = fireStationService.updateFireStation(updated);

        assertNotNull(result);
        assertEquals(updated.getAddress(), result.getAddress());
        assertEquals(updated.getStation(), result.getStation());

        Optional<FireStation> found = fireStationRepository.findByAddress(updated.getAddress());
        assertTrue(found.isPresent(), "The fire station should still be present after update");
        assertEquals(updated.getStation(), found.get().getStation());
    }

    @Test
    void deleteFireStation_shouldRemove_whenExisting() {
        FireStation toDelete = new FireStationTestBuilder().build();
        Optional<FireStation> before = fireStationRepository.findByAddress(toDelete.getAddress());
        assertTrue(before.isPresent(), "Precondition: existing fire station should be present");

        fireStationService.deleteFireStation(toDelete.getAddress());

        Optional<FireStation> after = fireStationRepository.findByAddress(toDelete.getAddress());
        assertTrue(after.isEmpty(), "The fire station should be removed from the repository");
    }

    @Test
    void getFireStationAddressListByFireStationNumber_shouldReturnAddresses_whenExisting() {
        FireStation existing = new FireStationTestBuilder().build();

        List<String> addresses =
                fireStationService.getFireStationAddressListByFireStationNumber(
                        existing.getStation());

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty(), "Addresses list should not be empty");
        assertTrue(
                addresses.contains(existing.getAddress()),
                "Addresses list should contain the existing fire station address");
    }

    @Test
    void getFireStationNumberListByAddress_shouldReturnNumberList_whenExists() {
        FireStation existing = new FireStationTestBuilder().build();

        List<Integer> stationNumber =
                fireStationService.getFireStationNumberListByAddress(existing.getAddress());

        assertEquals(
                existing.getStation(),
                stationNumber.getFirst(),
                "The returned station number should match the existing one");
    }

    @Test
    void getFireStationListByFireStationNumberList_shouldReturnFireStations_whenExisting() {
        FireStation existing = new FireStationTestBuilder().build();
        var stationNumbers = List.of(existing.getStation(), 99);

        var fireStations =
                fireStationService.getFireStationListByFireStationNumberList(stationNumbers);

        assertNotNull(fireStations);
        assertFalse(fireStations.isEmpty(), "Fire stations list should not be empty");
        assertTrue(
                fireStations.stream()
                        .anyMatch(
                                fs ->
                                        fs.getAddress().equals(existing.getAddress())
                                                && fs.getStation().equals(existing.getStation())),
                "Fire stations list should contain the existing fire station");
    }

    @Test
    void getAddressListFromFireStationNumberList_shouldReturnAddresses_whenExisting() {
        FireStation existing = new FireStationTestBuilder().build();
        var stationNumbers = List.of(existing.getStation(), 99);

        var addresses = fireStationService.getAddressListFromFireStationNumberList(stationNumbers);

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty(), "Addresses list should not be empty");
        assertTrue(
                addresses.contains(existing.getAddress()),
                "Addresses list should contain the existing fire station address");
    }

    @Test
    void getAddressListFromFireStationNumberList_shouldReturnEmptyList_whenNoneExists() {
        var stationNumbers = List.of(100, 101);

        var addresses = fireStationService.getAddressListFromFireStationNumberList(stationNumbers);

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty(), "Addresses list should be empty when no matches found");
    }
}
