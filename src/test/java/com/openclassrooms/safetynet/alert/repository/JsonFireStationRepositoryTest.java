package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class JsonFireStationRepositoryTest {

    @Mock JsonFileDataStore jsonFileDataStore;

    @InjectMocks JsonFireStationRepository fireStationRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void save_shouldSaveFireStationAndReturnIt_whenNewFireStation() {
        FireStation fireStation = new FireStationTestBuilder().build();

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);
        doNothing().when(jsonFileDataStore).setCachedDatabase(database);

        FireStation savedFireStation = fireStationRepository.save(fireStation);

        assertNotNull(savedFireStation);
        assertEquals(fireStation.getAddress(), savedFireStation.getAddress());
        assertEquals(fireStation.getStation(), savedFireStation.getStation());
        assertTrue(
                database.getFireStations().contains(savedFireStation),
                "The fire station should be added to the database");
    }

    @Test
    void save_shouldUpdateExistingFireStation_whenFireStationAlreadyExists() {
        final Integer fireStationNewNumber = 4;
        FireStation existingFireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(existingFireStation);

        FireStation updatedFireStation =
                new FireStationTestBuilder().withStation(fireStationNewNumber).build();

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);
        doNothing().when(jsonFileDataStore).setCachedDatabase(database);

        FireStation savedFireStation = fireStationRepository.save(updatedFireStation);

        assertNotNull(savedFireStation);
        assertEquals(existingFireStation.getAddress(), savedFireStation.getAddress());
        assertEquals(fireStationNewNumber, savedFireStation.getStation());
        assertEquals(
                1,
                database.getFireStations().size(),
                "There should still be only one fire station in the database.");
        assertEquals(
                fireStationNewNumber,
                database.getFireStations().getFirst().getStation(),
                "The fire station should be added to the database");
    }

    @Test
    void findAll_shouldReturnFireStations_whenDatabaseHasFireStations() {
        FireStation existingFireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(existingFireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn((database));

        List<FireStation> fireStations = fireStationRepository.findAll();

        assertNotNull(fireStations);
        assertEquals(
                1, fireStations.size(), "There should be one fire station in the returned list.");
        assertEquals(existingFireStation.getAddress(), fireStations.getFirst().getAddress());
        assertEquals(existingFireStation.getStation(), fireStations.getFirst().getStation());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        Database emptyDatabase =
                new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        when(jsonFileDataStore.getCachedDatabase()).thenReturn(emptyDatabase);

        List<FireStation> fireStations = fireStationRepository.findAll();

        assertNotNull(fireStations);
        assertTrue(fireStations.isEmpty(), "The returned fire stations list should be empty.");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullPersons() {
        database.setFireStations(null);
        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<FireStation> fireStations = fireStationRepository.findAll();

        assertNotNull(fireStations);
        assertTrue(fireStations.isEmpty(), "The returned fire stations list should be empty.");
    }

    @Test
    void findAllByAddress_shouldBeCaseInsensitiveAndReturnStream_whenMatchingExists() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<FireStation> foundFireStations =
                fireStationRepository.findAllByAddress(fireStation.getAddress().toUpperCase());

        assertNotNull(foundFireStations);
        assertEquals(
                1,
                foundFireStations.size(),
                "There should be one fire station found matching the address.");
        assertEquals(fireStation.getAddress(), foundFireStations.getFirst().getAddress());
        assertEquals(fireStation.getStation(), foundFireStations.getFirst().getStation());
    }

    @Test
    void findAllByAddress_shouldReturnEmptyStream_whenNoMatchFound() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<FireStation> foundFireStations =
                fireStationRepository.findAllByAddress("Nonexistent Address 456");

        assertNotNull(foundFireStations);
        assertTrue(
                foundFireStations.isEmpty(),
                "The returned fire stations list should be empty when no matches are found.");
    }

    @Test
    void findByAddress_shouldBeCaseInsensitiveAndFindFireStation_whenMatchingExists() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Optional<FireStation> foundFireStation =
                fireStationRepository.findByAddress(fireStation.getAddress().toUpperCase());

        assertTrue(foundFireStation.isPresent(), "The fire station should be found.");
        assertEquals(fireStation.getAddress(), foundFireStation.get().getAddress());
        assertEquals(fireStation.getStation(), foundFireStation.get().getStation());
    }

    @Test
    void findByAddress_shouldReturnEmpty_whenNoMatchFound() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        Optional<FireStation> foundFireStation =
                fireStationRepository.findByAddress("Fake Address 123");

        assertTrue(foundFireStation.isEmpty(), "No fire station should be found.");
    }

    @Test
    void findByStationNumber_shouldBeCaseInsensitiveAndReturnPersons_whenMatchingExists() {
        FireStation fireStation1 = new FireStationTestBuilder().build();
        FireStation fireStation2 =
                new FireStationTestBuilder().withAddress("29 15th St").withStation(3).build();
        database.getFireStations().add(fireStation1);
        database.getFireStations().add(fireStation2);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<FireStation> foundFireStations =
                fireStationRepository.findByStationNumber(fireStation2.getStation());

        assertNotNull(foundFireStations);
        assertEquals(
                2,
                foundFireStations.size(),
                "There should be two fire stations found matching the station number.");
        assertEquals(
                foundFireStations,
                List.of(fireStation1, fireStation2),
                "The found fire stations should match the expected ones.");
    }

    @Test
    void findByStationNumber_shouldReturnEmptyList_whenNoMatchFound() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);

        List<FireStation> foundFireStations = fireStationRepository.findByStationNumber(999);

        assertNotNull(foundFireStations);
        assertTrue(
                foundFireStations.isEmpty(),
                "The returned fire stations list should be empty when no fire stations match the station number.");
    }

    @Test
    void deleteByAddress_shouldDeleteFireStation_whenFireStationExists() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);
        doNothing().when(jsonFileDataStore).setCachedDatabase(database);

        fireStationRepository.deleteByAddress(fireStation.getAddress());

        assertTrue(
                database.getFireStations().isEmpty(),
                "The fire station should be deleted from the database.");
    }

    @Test
    void deleteByAddress_shouldDoNothing_whenFireStationDoesNotExist() {
        FireStation fireStation = new FireStationTestBuilder().build();
        database.getFireStations().add(fireStation);

        when(jsonFileDataStore.getCachedDatabase()).thenReturn(database);
        doNothing().when(jsonFileDataStore).setCachedDatabase(any(Database.class));

        fireStationRepository.deleteByAddress("Unknown Address 123");

        assertEquals(
                1,
                database.getFireStations().size(),
                "The database should still contain the original fire station.");
    }
}
