package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonInfoDTO;
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

    @RequestMapping("/personInfolastName")
    public List<PersonInfoDTO> getPersonListByLastName(@RequestParam String lastName) {
        return populationService.getPersonListByLastName(lastName).stream()
                .map(this::mapToPersonInfoDTO)
                .toList();
    }

    private PersonInfoDTO mapToPersonInfoDTO(Person person) {
        return Optional.ofNullable(
                        medicalRecordService.getMedicalRecordByFullName(
                                person.getFirstName(), person.getLastName()))
                .map(
                        medicalRecord ->
                                new PersonInfoDTO(
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
                                new PersonInfoDTO(
                                        person.getFirstName(),
                                        person.getLastName(),
                                        person.getAddress(),
                                        person.getEmail(),
                                        null,
                                        List.of(),
                                        List.of()));
    }
}
