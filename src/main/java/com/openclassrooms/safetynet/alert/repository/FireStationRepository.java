package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.model.FireStation;

import java.util.List;
import java.util.Optional;

/** Repository interface for managing FireStation entities. */
public interface FireStationRepository {
    /**
     * Save a FireStation to the data store. If the FireStation already exists (based on address),
     * update its information.
     */
    FireStation save(FireStation fireStation);

    /** Return all FireStations from the data store. */
    List<FireStation> findAll();

    /** Find a FireStation by address. */
    Optional<FireStation> findByAddress(String address);

    /** Find FireStations by station number. */
    List<FireStation> findByStationNumber(String stationNumber);

    /** Delete a FireStation by address. */
    void deleteByAddress(String address);
}
