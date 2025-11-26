package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/** Controller for handling population-related requests. */
@RestController
public class PopulationController {

    private final PopulationService populationService;
    private final MedicalRecordService medicalRecordService;

    public PopulationController(
            PopulationService populationService, MedicalRecordService medicalRecordService) {
        this.populationService = populationService;
        this.medicalRecordService = medicalRecordService;
    }

    /**
     * Return the name, address, age, email address, and medical history (medications, dosage, and
     * allergies) of each resident. If several people have the same name, they must all appear.
     *
     * @param lastName
     * @return
     */
    @RequestMapping("/personInfolastName")
    public List<PersonWithMedicalInfosDTO> getPersonListByLastName(@RequestParam String lastName) {
        return populationService.getPersonListByLastName(lastName).stream()
                .map(this::mapToPersonInfoDTO)
                .toList();
    }

    private PersonWithMedicalInfosDTO mapToPersonInfoDTO(Person person) {
        return Optional.ofNullable(
                        medicalRecordService.getMedicalRecordByFullName(
                                person.getFirstName(), person.getLastName()))
                .map(
                        medicalRecord ->
                                new PersonWithMedicalInfosDTO(
                                        person.getFirstName(),
                                        person.getLastName(),
                                        person.getAddress(),
                                        person.getEmail(),
                                        medicalRecordService.calculateAgeFromBirthdate(
                                                medicalRecord.getBirthdate()),
                                        medicalRecord.getMedications(),
                                        medicalRecord.getAllergies()))
                .orElseGet(
                        () ->
                                new PersonWithMedicalInfosDTO(
                                        person.getFirstName(),
                                        person.getLastName(),
                                        person.getAddress(),
                                        person.getEmail(),
                                        null,
                                        List.of(),
                                        List.of()));
    }

    /**
     * Return the email addresses of all residents of the city.
     *
     * @param city
     * @return
     */
    @RequestMapping("/communityEmail")
    public List<String> getAllEmailFromCity(@RequestParam String city) {
        return populationService.getPersonListByCity(city).stream()
                .map(Person::getEmail)
                .distinct()
                .toList();
    }
}
