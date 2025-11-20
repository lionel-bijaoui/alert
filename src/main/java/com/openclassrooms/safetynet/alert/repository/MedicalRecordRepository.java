package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.model.MedicalRecord;

import java.util.List;
import java.util.Optional;

/** Repository interface for managing MedicalRecord entities. */
public interface MedicalRecordRepository {

    /**
     * Save a medical record. If the record already exists (based on first and last name), update
     * its information.
     */
    MedicalRecord save(MedicalRecord item);

    /** Return all medical records from the data store, or an empty list if none. */
    List<MedicalRecord> findAll();

    /** Find a medical record by first and last name (case-insensitive). */
    Optional<MedicalRecord> findByFirstNameAndLastName(String firstName, String lastName);

    /** Delete a medical record by first and last name (case-insensitive). */
    void deleteByFirstNameAndLastName(String firstName, String lastName);
}
