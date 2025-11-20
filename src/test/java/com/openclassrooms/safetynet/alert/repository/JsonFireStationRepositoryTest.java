package com.openclassrooms.safetynet.alert.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;
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

    static final String FIRE_STATION_ADDRESS = "1509 Culver St";
    static final Integer FIRE_STATION_NUMBER = 3;

    @Mock JsonFileDataStore jsonFileDataStore;

    @InjectMocks JsonFireStationRepository fireStationRepository;

    Database database;

    @BeforeEach
    void setUp() {
        database = new Database(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Test
    void save_shouldSaveFireStationAndReturnIt_whenNewFireStation() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));
        doNothing().when(jsonFileDataStore).writeAll(any(Database.class));

        FireStation savedFireStation = fireStationRepository.save(fireStation);

        assertNotNull(savedFireStation);
        assertEquals(FIRE_STATION_ADDRESS, savedFireStation.getAddress());
        assertEquals(FIRE_STATION_NUMBER, savedFireStation.getStation());
        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertTrue(
                database.getFirestations().contains(savedFireStation),
                "The fire station should be added to the database");
    }

    @Test
    void save_shouldUpdateExistingFireStation_whenFireStationAlreadyExists() {
        final Integer FIRE_STATION_NEW_NUMBER = 4;
        FireStation existingFireStation =
                new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(existingFireStation);

        FireStation updatedFireStation =
                new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NEW_NUMBER);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));
        doNothing().when(jsonFileDataStore).writeAll(any(Database.class));

        FireStation savedFireStation = fireStationRepository.save(updatedFireStation);

        assertNotNull(savedFireStation);
        assertEquals(FIRE_STATION_ADDRESS, savedFireStation.getAddress());
        assertEquals(FIRE_STATION_NEW_NUMBER, savedFireStation.getStation());
        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(
                1,
                database.getFirestations().size(),
                "There should still be only one fire station in the database.");
        assertEquals(
                FIRE_STATION_NEW_NUMBER,
                database.getFirestations().getFirst().getStation(),
                "The fire station should be added to the database");
    }

    @Test
    void findAll_shouldReturnFireStations_whenDatabaseHasFireStations() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<FireStation> fireStations = fireStationRepository.findAll();

        assertNotNull(fireStations);
        assertEquals(
                1, fireStations.size(), "There should be one fire station in the returned list.");
        assertEquals(FIRE_STATION_ADDRESS, fireStations.getFirst().getAddress());
        assertEquals(FIRE_STATION_NUMBER, fireStations.getFirst().getStation());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseEmpty() {
        when(jsonFileDataStore.readAll()).thenReturn(Optional.empty());

        List<FireStation> fireStations = fireStationRepository.findAll();

        assertNotNull(fireStations);
        assertTrue(fireStations.isEmpty(), "The returned fire stations list should be empty.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenDatabaseHasNullPersons() {
        database.setFirestations(null);
        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<FireStation> fireStations = fireStationRepository.findAll();

        assertNotNull(fireStations);
        assertTrue(fireStations.isEmpty(), "The returned fire stations list should be empty.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByAddress_shouldBeCaseInsensitiveAndFindFireStation_whenMatchingExists() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<FireStation> foundFireStation =
                fireStationRepository.findByAddress(FIRE_STATION_ADDRESS.toUpperCase());

        assertTrue(foundFireStation.isPresent(), "The fire station should be found.");
        assertEquals(FIRE_STATION_ADDRESS, foundFireStation.get().getAddress());
        assertEquals(FIRE_STATION_NUMBER, foundFireStation.get().getStation());
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByAddress_shouldReturnEmpty_whenNoMatchFound() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        Optional<FireStation> foundFireStation =
                fireStationRepository.findByAddress("Fake Address 123");

        assertTrue(foundFireStation.isEmpty(), "No fire station should be found.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByStationNumber_shouldBeCaseInsensitiveAndReturnPersons_whenMatchingExists() {
        FireStation fireStation1 = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation1);

        final String FIRE_STATION_NEW_ADDRESS = "29 15th St";
        FireStation fireStation2 = new FireStation(FIRE_STATION_NEW_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation2);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<FireStation> foundFireStations =
                fireStationRepository.findByStationNumber(FIRE_STATION_NUMBER);

        assertNotNull(foundFireStations);
        assertEquals(
                2,
                foundFireStations.size(),
                "There should be two fire stations found matching the station number.");
        assertEquals(
                foundFireStations,
                List.of(fireStation1, fireStation2),
                "The found fire stations should match the expected ones.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void findByStationNumber_shouldReturnEmptyList_whenNoMatchFound() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));

        List<FireStation> foundFireStations = fireStationRepository.findByStationNumber(999);

        assertNotNull(foundFireStations);
        assertTrue(
                foundFireStations.isEmpty(),
                "The returned fire stations list should be empty when no fire stations match the station number.");
        verify(jsonFileDataStore, times(1)).readAll();
    }

    @Test
    void deleteByAddress_shouldDeleteFireStation_whenFireStationExists() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));
        doNothing().when(jsonFileDataStore).writeAll(any(Database.class));

        fireStationRepository.deleteByAddress(FIRE_STATION_ADDRESS);

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertTrue(
                database.getFirestations().isEmpty(),
                "The fire station should be deleted from the database.");
    }

    @Test
    void deleteByAddress_shouldDoNothing_whenFireStationDoesNotExist() {
        FireStation fireStation = new FireStation(FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        database.getFirestations().add(fireStation);

        when(jsonFileDataStore.readAll()).thenReturn(Optional.of(database));
        doNothing().when(jsonFileDataStore).writeAll(any(Database.class));

        fireStationRepository.deleteByAddress("Unknown Address 123");

        verify(jsonFileDataStore, times(1)).readAll();
        verify(jsonFileDataStore, times(1)).writeAll(database);
        assertEquals(
                1,
                database.getFirestations().size(),
                "The database should still contain the original fire station.");
    }
}
