package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.repository.JsonFireStationRepository;

import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Service class for managing FireStation entities. */
@Service
@Slf4j
public class FireStationService {

    private final JsonFireStationRepository jsonFireStationRepository;

    public FireStationService(JsonFireStationRepository jsonFireStationRepository) {
        this.jsonFireStationRepository = jsonFireStationRepository;
    }

    // CRUD operations for FireStation

    /**
     * Add a new fire station.
     *
     * @param fireStation the fire station to add
     * @return the saved fire station
     * @throws ConflictException if a fire station already exists for the given address
     */
    @NotNull
    public FireStation addFireStation(FireStation fireStation) {
        Optional<FireStation> maybeFireStation =
                jsonFireStationRepository.findByAddress(fireStation.getAddress());

        if (maybeFireStation.isPresent()) {
            throw new ConflictException(
                    "Fire station already exists for address: " + fireStation.getAddress());
        }

        return jsonFireStationRepository.save(fireStation);
    }

    /**
     * Update an existing fire station.
     *
     * @param fireStation the fire station to update
     * @return the updated fire station
     * @throws ResourceNotFoundException if the fire station does not exist for the given address
     */
    @NotNull
    public FireStation updateFireStation(FireStation fireStation) {
        Optional<FireStation> maybeFireStation =
                jsonFireStationRepository.findByAddress(fireStation.getAddress());

        if (maybeFireStation.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Fire station does not exist for address: " + fireStation.getAddress());
        }

        FireStation existingFireStation = maybeFireStation.get();
        existingFireStation.setStation(fireStation.getStation());

        return jsonFireStationRepository.save(existingFireStation);
    }

    /**
     * Delete a fire station by address.
     *
     * @param address the address of the fire station to delete
     * @throws ResourceNotFoundException if the fire station does not exist for the given address
     */
    public void deleteFireStation(String address) {
        Optional<FireStation> maybeFireStation = jsonFireStationRepository.findByAddress(address);

        if (maybeFireStation.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No existing fire station to delete at address: " + address);
        }

        jsonFireStationRepository.deleteByAddress(address);
    }

    // Additional methods

    public List<String> getFireStationAddressListByFireStationNumber(Integer stationNumber) {
        return jsonFireStationRepository.findByStationNumber(stationNumber).stream()
                .map(FireStation::getAddress)
                .distinct()
                .toList();
    }

    public List<Integer> getFireStationNumberListByAddress(String address) {
        return jsonFireStationRepository.findAllByAddress(address).stream()
                .map(FireStation::getStation)
                .toList();
    }

    public List<FireStation> getFireStationListByFireStationNumberList(
            List<Integer> stationNumberList) {
        return jsonFireStationRepository.findAll().stream()
                .filter(fs -> stationNumberList.contains(fs.getStation()))
                .toList();
    }

    public List<String> getAddressListFromFireStationNumberList(
            List<Integer> fireStationNumberList) {
        return getFireStationListByFireStationNumberList(fireStationNumberList).stream()
                .map(FireStation::getAddress)
                .distinct()
                .toList();
    }
}
