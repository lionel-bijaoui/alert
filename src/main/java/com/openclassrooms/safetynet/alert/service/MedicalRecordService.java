package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.repository.MedicalRecordRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

/** Service class for managing medical records. */
@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    // CRUD operations for MedicalRecord

    @NotNull
    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        Optional<MedicalRecord> maybeMedicalRecord =
                medicalRecordRepository.findByFirstNameAndLastName(
                        medicalRecord.getFirstName(), medicalRecord.getLastName());
        if (maybeMedicalRecord.isPresent()) {
            throw new ConflictException(
                    "Medical record already exists for: "
                            + medicalRecord.getFirstName()
                            + " "
                            + medicalRecord.getLastName());
        }
        return medicalRecordRepository.save(medicalRecord);
    }

    @NotNull
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        Optional<MedicalRecord> maybeMedicalRecord =
                medicalRecordRepository.findByFirstNameAndLastName(
                        medicalRecord.getFirstName(), medicalRecord.getLastName());
        if (maybeMedicalRecord.isPresent()) {
            MedicalRecord existingMedicalRecord = maybeMedicalRecord.get();
            existingMedicalRecord.setBirthdate(medicalRecord.getBirthdate());
            existingMedicalRecord.setMedications(medicalRecord.getMedications());
            existingMedicalRecord.setAllergies(medicalRecord.getAllergies());
            return medicalRecordRepository.save(existingMedicalRecord);
        } else {
            throw new ResourceNotFoundException("Medical record does not exist");
        }
    }

    public void deleteMedicalRecord(String firstName, String lastName) {
        Optional<MedicalRecord> maybeMedicalRecord =
                medicalRecordRepository.findByFirstNameAndLastName(firstName, lastName);
        if (maybeMedicalRecord.isEmpty()) {
            throw new ResourceNotFoundException("Medical record does not exist");
        }
        medicalRecordRepository.deleteByFirstNameAndLastName(firstName, lastName);
    }

    // Additional methods

    public MedicalRecord getMedicalRecordByFullName(String firstName, String lastName) {
        Optional<MedicalRecord> maybeMedicalRecord =
                medicalRecordRepository.findByFirstNameAndLastName(firstName, lastName);
        return maybeMedicalRecord.orElse(null);
    }

    public Integer calculateAgeFromBirthdate(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        if ((birthdate != null)
                && (birthdate.isBefore(currentDate) || birthdate.isEqual(currentDate))) {
            return Period.between(birthdate, currentDate).getYears();
        } else {
            throw new IllegalArgumentException("Birthdate is invalid " + birthdate);
        }
    }
}
