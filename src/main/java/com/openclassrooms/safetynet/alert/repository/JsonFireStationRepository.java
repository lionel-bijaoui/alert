package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository exposing fire station-related operations backed by {@link JsonFileDataStore}. */
@Repository
public class JsonFireStationRepository implements FireStationRepository {

    private final JsonFileDataStore jsonFileDataStore;

    public JsonFireStationRepository(JsonFileDataStore jsonFileDataStore) {
        this.jsonFileDataStore = jsonFileDataStore;
    }

    @Override
    public FireStation save(FireStation fireStation) {
        Database database = jsonFileDataStore.getCachedDatabase();
        List<FireStation> fireStations = database.getFireStations();

        fireStations.removeIf(fs -> fs.getAddress().equalsIgnoreCase(fireStation.getAddress()));
        fireStations.add(fireStation);

        database.setFireStations(fireStations);
        jsonFileDataStore.setCachedDatabase(database);
        return fireStation;
    }

    @Override
    public List<FireStation> findAll() {
        return jsonFileDataStore.getCachedDatabase().getFireStations();
    }

    @Override
    public List<FireStation> findAllByAddress(String address) {
        return findAll().stream()
                .filter(fireStation -> fireStation.getAddress().equalsIgnoreCase(address))
                .toList();
    }

    @Override
    public Optional<FireStation> findByAddress(String address) {
        return findAll().stream()
                .filter(fireStation -> fireStation.getAddress().equalsIgnoreCase(address))
                .findFirst();
    }

    @Override
    public List<FireStation> findByStationNumber(Integer stationNumber) {
        return findAll().stream()
                .filter(fireStation -> fireStation.getStation().equals(stationNumber))
                .toList();
    }

    @Override
    public void deleteByAddress(String address) {
        Database database = jsonFileDataStore.getCachedDatabase();

        List<FireStation> fireStations = database.getFireStations();

        fireStations.removeIf(fs -> fs.getAddress().equalsIgnoreCase(address));

        database.setFireStations(fireStations);
        jsonFileDataStore.setCachedDatabase(database);
    }
}
