package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/** Repository exposing medical record-related operations backed by {@link JsonFileDataStore}. */
@Repository
public class JsonMedicalRecordRepository implements MedicalRecordRepository {

    private final JsonFileDataStore jsonFileDataStore;

    public JsonMedicalRecordRepository(JsonFileDataStore jsonFileDataStore) {
        this.jsonFileDataStore = jsonFileDataStore;
    }

    @Override
    public void save(MedicalRecord item) {
        Database database =
                jsonFileDataStore
                        .readAll()
                        .orElse(
                                new Database(
                                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        List<MedicalRecord> medicalRecords = database.getMedicalrecords();

        if (medicalRecords == null) {
            medicalRecords = new ArrayList<>();
        }

        medicalRecords.removeIf(
                mr ->
                        mr.getFirstName().equalsIgnoreCase(item.getFirstName())
                                && mr.getLastName().equalsIgnoreCase(item.getLastName()));
        medicalRecords.add(item);

        database.setMedicalrecords(medicalRecords);
        jsonFileDataStore.writeAll(database);
    }

    @Override
    public List<MedicalRecord> findAll() {
        return jsonFileDataStore
                .readAll()
                .map(Database::getMedicalrecords)
                .orElse(new ArrayList<>());
    }

    @Override
    public MedicalRecord findByFirstNameAndLastName(String firstName, String lastName) {
        return findAll().stream()
                .filter(
                        mr ->
                                mr.getFirstName().equalsIgnoreCase(firstName)
                                        && mr.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteByFirstNameAndLastName(String firstName, String lastName) {
        Database database =
                jsonFileDataStore
                        .readAll()
                        .orElse(
                                new Database(
                                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        List<MedicalRecord> medicalRecords = database.getMedicalrecords();

        if (medicalRecords == null) {
            medicalRecords = new ArrayList<>();
        }

        medicalRecords.removeIf(
                mr ->
                        mr.getFirstName().equalsIgnoreCase(firstName)
                                && mr.getLastName().equalsIgnoreCase(lastName));

        database.setMedicalrecords(medicalRecords);
        jsonFileDataStore.writeAll(database);
    }
}
