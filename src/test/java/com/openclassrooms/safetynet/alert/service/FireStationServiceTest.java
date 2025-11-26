package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.repository.JsonFireStationRepository;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class FireStationServiceTest {

    @Mock JsonFireStationRepository jsonFireStationRepository;

    @InjectMocks FireStationService fireStationService;

    FireStation existingFireStation;

    @BeforeEach
    void setUp() {
        existingFireStation = new FireStationTestBuilder().build();
    }

    @Test
    void addFireStation_shouldReturnSavedFireStation_whenNotPresent() {
        FireStation newFireStation =
                new FireStationTestBuilder().withAddress("29 15th St").withStation(4).build();

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
        when(jsonFireStationRepository.findByAddress(existingFireStation.getAddress()))
                .thenReturn(Optional.of(existingFireStation));

        assertThrows(
                ConflictException.class,
                () -> fireStationService.addFireStation(existingFireStation));
        verify(jsonFireStationRepository, never()).save(any());
    }

    @Test
    void updateFireStation_shouldReturnUpdatedFireStation_whenExists() {
        FireStation updatedFireStation = new FireStationTestBuilder().withStation(99).build();

        when(jsonFireStationRepository.findByAddress(existingFireStation.getAddress()))
                .thenReturn(Optional.of(updatedFireStation));
        when(jsonFireStationRepository.save(updatedFireStation)).thenReturn(updatedFireStation);

        FireStation result = fireStationService.updateFireStation(updatedFireStation);

        assertNotNull(result);
        assertEquals(updatedFireStation.getAddress(), result.getAddress());
        assertEquals(updatedFireStation.getStation(), result.getStation());
    }

    @Test
    void updateFireStation_shouldThrowResourceNotFoundException_whenNotExists() {
        FireStation updated =
                new FireStationTestBuilder().withAddress("Unknown Address 123").build();

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
        when(jsonFireStationRepository.findByAddress(existingFireStation.getAddress()))
                .thenReturn(Optional.of(existingFireStation));
        doNothing()
                .when(jsonFireStationRepository)
                .deleteByAddress(existingFireStation.getAddress());

        fireStationService.deleteFireStation(existingFireStation.getAddress());

        verify(jsonFireStationRepository, times(1))
                .deleteByAddress(existingFireStation.getAddress());
    }

    @Test
    void deleteFireStation_shouldThrowResourceNotFoundException_whenNotExists() {
        when(jsonFireStationRepository.findByAddress(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class, () -> fireStationService.deleteFireStation("99"));

        verify(jsonFireStationRepository, never()).deleteByAddress(any());
    }

    @Test
    void getFireStationAddressListByFireStationNumber_shouldReturnAddressList_whenExists() {
        FireStation anotherFireStation =
                new FireStationTestBuilder().withAddress("Another address").build();

        when(jsonFireStationRepository.findByStationNumber(existingFireStation.getStation()))
                .thenReturn(List.of(existingFireStation, anotherFireStation));

        List<String> result =
                fireStationService.getFireStationAddressListByFireStationNumber(
                        existingFireStation.getStation());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(existingFireStation.getAddress()));
        assertTrue(result.contains(anotherFireStation.getAddress()));
    }

    @Test
    void getFireStationAddressListByFireStationNumber_shouldReturnEmptyList_whenNoneExists() {
        when(jsonFireStationRepository.findByStationNumber(any(Integer.class)))
                .thenReturn(List.of());

        List<String> result = fireStationService.getFireStationAddressListByFireStationNumber(99);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getFireStationNumberByAddress_shouldReturnStationNumber_whenExists() {
        when(jsonFireStationRepository.findByAddress(existingFireStation.getAddress()))
                .thenReturn(Optional.of(existingFireStation));

        int result =
                fireStationService.getFireStationNumberByAddress(existingFireStation.getAddress());

        assertEquals(
                (Integer) 3, result, "The returned station number should match the existing one");
    }

    @Test
    void getFireStationNumberByAddress_shouldThrowResourceNotFoundException_whenNotExists() {
        when(jsonFireStationRepository.findByAddress(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> fireStationService.getFireStationNumberByAddress("99"));
    }

    @Test
    void getFireStationListByFireStationNumberList_shouldReturnFireStationList_whenExists() {
        FireStation fireStation1 = new FireStationTestBuilder().withAddress("Somewhere").build();
        FireStation fireStation2 =
                new FireStationTestBuilder().withAddress("Anywhere").withStation(99).build();
        List<Integer> stationNumbers =
                List.of(fireStation1.getStation(), fireStation2.getStation());

        when(jsonFireStationRepository.findAll())
                .thenReturn(List.of(existingFireStation, fireStation1, fireStation2));

        List<FireStation> result =
                fireStationService.getFireStationListByFireStationNumberList(stationNumbers);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(existingFireStation));
        assertTrue(result.contains(fireStation1));
        assertTrue(result.contains(fireStation2));
    }

    @Test
    void getFireStationListByFireStationNumberList_shouldReturnEmptyList_whenNoneExists() {
        List<Integer> stationNumbers = List.of(99, 100);

        when(jsonFireStationRepository.findAll()).thenReturn(List.of(existingFireStation));

        List<FireStation> result =
                fireStationService.getFireStationListByFireStationNumberList(stationNumbers);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
