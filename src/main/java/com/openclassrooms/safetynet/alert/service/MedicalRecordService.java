package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.dto.PersonWithAge;
import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.MedicalRecordRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

/** Service class for managing medical records. */
@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /**
     * Calculate age from birthdate
     *
     * @param birthdate the birthdate
     * @return the age in years
     * @throws IllegalArgumentException if birthdate is null or in the future
     */
    @NotNull
    public static Integer calculateAgeFromBirthdate(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        boolean isBirthdateValid =
                (birthdate != null)
                        && (birthdate.isBefore(currentDate) || birthdate.isEqual(currentDate));

        if (!isBirthdateValid) {
            throw new IllegalArgumentException("Birthdate is invalid " + birthdate);
        }

        return Period.between(birthdate, currentDate).getYears();
    }

    // CRUD operations for MedicalRecord

    /**
     * Add a new medical record
     *
     * @param medicalRecord the medical record to add
     * @return the added medical record
     * @throws ConflictException if a medical record already exists for the given first and last
     *     name
     */
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

    /**
     * Update an existing medical record
     *
     * @param medicalRecord the medical record to update
     * @return the updated medical record
     * @throws ResourceNotFoundException if the medical record does not exist
     */
    @NotNull
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        Optional<MedicalRecord> maybeMedicalRecord =
                medicalRecordRepository.findByFirstNameAndLastName(
                        medicalRecord.getFirstName(), medicalRecord.getLastName());

        if (maybeMedicalRecord.isEmpty()) {
            throw new ResourceNotFoundException("Medical record does not exist");
        }

        MedicalRecord existingMedicalRecord = maybeMedicalRecord.get();
        existingMedicalRecord.setBirthdate(medicalRecord.getBirthdate());
        existingMedicalRecord.setMedications(medicalRecord.getMedications());
        existingMedicalRecord.setAllergies(medicalRecord.getAllergies());
        return medicalRecordRepository.save(existingMedicalRecord);
    }

    /**
     * Delete a medical record by first and last name
     *
     * @param firstName the first name
     * @param lastName the last name
     * @throws ResourceNotFoundException if the medical record does not exist
     */
    public void deleteMedicalRecord(String firstName, String lastName) {
        Optional<MedicalRecord> maybeMedicalRecord =
                medicalRecordRepository.findByFirstNameAndLastName(firstName, lastName);
        if (maybeMedicalRecord.isEmpty()) {
            throw new ResourceNotFoundException("Medical record does not exist");
        }
        medicalRecordRepository.deleteByFirstNameAndLastName(firstName, lastName);
    }

    // Additional methods

    public Optional<MedicalRecord> getMedicalRecordByFullName(String firstName, String lastName) {
        return medicalRecordRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    /**
     * Enrich a list of persons with their respective ages to cache ages and avoid multiple lookups
     *
     * @param persons list of person
     * @return Stream of PersonWithAge
     */
    public List<PersonWithAge> enrichPersonsWithAge(List<Person> persons) {
        return persons.stream()
                .map(
                        person ->
                                new PersonWithAge(
                                        person,
                                        calculateAgeFromBirthdate(
                                                getMedicalRecordByFullName(
                                                                person.getFirstName(),
                                                                person.getLastName())
                                                        .orElseThrow(
                                                                () ->
                                                                        new ResourceNotFoundException(
                                                                                "No medical record found for: "
                                                                                        + person
                                                                                                .getFirstName()
                                                                                        + " "
                                                                                        + person
                                                                                                .getLastName()))
                                                        .getBirthdate())))
                .toList();
    }

    public PersonWithMedicalInfosDTO mapToPersonWithMedicalInfosDTO(Person person) {
        Optional<MedicalRecord> optionalMedicalRecord =
                getMedicalRecordByFullName(person.getFirstName(), person.getLastName());

        Integer age =
                optionalMedicalRecord
                        .map(MedicalRecord::getBirthdate)
                        .map(MedicalRecordService::calculateAgeFromBirthdate)
                        .orElse(null);

        List<String> medications =
                optionalMedicalRecord.map(MedicalRecord::getMedications).orElse(List.of());

        List<String> allergies =
                optionalMedicalRecord.map(MedicalRecord::getAllergies).orElse(List.of());

        return new PersonWithMedicalInfosDTO(
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getPhone(),
                person.getEmail(),
                age,
                medications,
                allergies);
    }
}
