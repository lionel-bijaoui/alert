package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.repository.JsonFireStationRepository;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class FireStationServiceTest {

    static final String EXISTING_FIRE_STATION_ADDRESS = "1509 Culver St";
    static final Integer EXISTING_FIRE_STATION_NUMBER = 3;
    static final Integer NEW_FIRE_STATION_NUMBER = 4;
    static final String NEW_FIRE_STATION_ADDRESS = "29 15th St";

    @Mock JsonFireStationRepository jsonFireStationRepository;

    @InjectMocks FireStationService fireStationService;

    FireStation existingFireStation;

    @BeforeEach
    void setUp() {
        existingFireStation =
                new FireStation(EXISTING_FIRE_STATION_ADDRESS, EXISTING_FIRE_STATION_NUMBER);
    }

    @Test
    void addFireStation_shouldReturnSavedFireStation_whenNotPresent() {
        FireStation newFireStation =
                new FireStation(NEW_FIRE_STATION_ADDRESS, NEW_FIRE_STATION_NUMBER);

        when(jsonFireStationRepository.findByAddress(newFireStation.getAddress()))
                .thenReturn(Optional.empty());
        when(jsonFireStationRepository.save(newFireStation)).thenReturn(newFireStation);

        FireStation result = fireStationService.addFireStation(newFireStation);

        assertNotNull(result);
        assertEquals(newFireStation.getAddress(), result.getAddress());
        assertEquals(newFireStation.getStation(), result.getStation());

        verify(jsonFireStationRepository, times(1)).findByAddress(newFireStation.getAddress());
        verify(jsonFireStationRepository, times(1)).save(newFireStation);
    }

    @Test
    void addFireStation_shouldThrowConflictException_whenAddressExists() {
        when(jsonFireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS))
                .thenReturn(Optional.of(existingFireStation));

        assertThrows(
                ConflictException.class,
                () -> fireStationService.addFireStation(existingFireStation));

        verify(jsonFireStationRepository, times(1)).findByAddress(EXISTING_FIRE_STATION_ADDRESS);
        verify(jsonFireStationRepository, never()).save(any());
    }

    @Test
    void updateFireStation_shouldReturnUpdatedFireStation_whenExists() {
        FireStation updatedFireStation =
                new FireStation(EXISTING_FIRE_STATION_ADDRESS, NEW_FIRE_STATION_NUMBER);

        when(jsonFireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS))
                .thenReturn(Optional.of(updatedFireStation));
        when(jsonFireStationRepository.save(updatedFireStation)).thenReturn(updatedFireStation);

        FireStation result = fireStationService.updateFireStation(updatedFireStation);

        assertNotNull(result);
        assertEquals(EXISTING_FIRE_STATION_ADDRESS, result.getAddress());
        assertEquals(NEW_FIRE_STATION_NUMBER, result.getStation());

        verify(jsonFireStationRepository, times(1)).findByAddress(EXISTING_FIRE_STATION_ADDRESS);
        verify(jsonFireStationRepository, times(1)).save(updatedFireStation);
    }

    @Test
    void updateFireStation_shouldThrowResourceNotFoundException_whenNotExists() {
        FireStation updated = new FireStation("Unknown Address 123", NEW_FIRE_STATION_NUMBER);

        when(jsonFireStationRepository.findByAddress(updated.getAddress()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> fireStationService.updateFireStation(updated));

        verify(jsonFireStationRepository, times(1)).findByAddress(updated.getAddress());
        verify(jsonFireStationRepository, never()).save(any());
    }

    @Test
    void deleteFireStation_shouldRemoveFireStation_whenExists() {
        when(jsonFireStationRepository.findByAddress(EXISTING_FIRE_STATION_ADDRESS))
                .thenReturn(Optional.of(existingFireStation));
        doNothing().when(jsonFireStationRepository).deleteByAddress(EXISTING_FIRE_STATION_ADDRESS);

        fireStationService.deleteFireStation(EXISTING_FIRE_STATION_ADDRESS);

        verify(jsonFireStationRepository, times(1)).findByAddress(EXISTING_FIRE_STATION_ADDRESS);
        verify(jsonFireStationRepository, times(1)).deleteByAddress(EXISTING_FIRE_STATION_ADDRESS);
    }

    @Test
    void deleteFireStation_shouldThrowResourceNotFoundException_whenNotExists() {
        when(jsonFireStationRepository.findByAddress(NEW_FIRE_STATION_ADDRESS))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> fireStationService.deleteFireStation(NEW_FIRE_STATION_ADDRESS));

        verify(jsonFireStationRepository, times(1)).findByAddress(NEW_FIRE_STATION_ADDRESS);
        verify(jsonFireStationRepository, never()).deleteByAddress(any());
    }
}
