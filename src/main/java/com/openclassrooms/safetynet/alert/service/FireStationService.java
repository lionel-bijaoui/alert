package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.repository.JsonFireStationRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import java.util.Optional;

/** Service class for managing FireStation entities. */
@Service
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
}
