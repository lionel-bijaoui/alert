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

    @NotNull
    public FireStation updateFireStation(FireStation fireStation) {
        Optional<FireStation> maybeFireStation =
                jsonFireStationRepository.findByAddress(fireStation.getAddress());
        if (maybeFireStation.isPresent()) {
            FireStation existingFireStation = maybeFireStation.get();
            existingFireStation.setStation(fireStation.getStation());
            return jsonFireStationRepository.save(existingFireStation);
        } else {
            throw new ResourceNotFoundException("Fire station does not exist");
        }
    }

    public void deleteFireStation(String address) {
        Optional<FireStation> maybeFireStation = jsonFireStationRepository.findByAddress(address);
        if (maybeFireStation.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No existing fire station to delete at address: " + address);
        }
        jsonFireStationRepository.deleteByAddress(address);
    }

    public List<String> getFireStationAddressListByFireStationNumber(Integer stationNumber) {
        return jsonFireStationRepository.findByStationNumber(stationNumber).stream()
                .map(FireStation::getAddress)
                .distinct()
                .toList();
    }

    public List<Integer> getFireStationNumberListByAddress(String address) {
        return jsonFireStationRepository
                .findAllByAddress(address)
                .map(FireStation::getStation)
                .toList();
    }

    public List<FireStation> getFireStationListByFireStationNumberList(
            List<Integer> stationNumberList) {
        return jsonFireStationRepository.findAll().stream()
                .filter(fs -> stationNumberList.contains(fs.getStation()))
                .toList();
    }
}
