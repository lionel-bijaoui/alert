package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationsDTO;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Controller for handling hazard-related requests. */
@RestController
public class HazardController {

    private final FireStationService fireStationService;
    private final MedicalRecordService medicalRecordService;
    private final PopulationService populationService;

    public HazardController(
            FireStationService fireStationService,
            MedicalRecordService medicalRecordService,
            PopulationService populationService) {
        this.fireStationService = fireStationService;
        this.medicalRecordService = medicalRecordService;
        this.populationService = populationService;
    }

    /**
     * Return a list of all households served by the fire station. This list must group people by
     * address. It must also include the name, phone number, and age of the residents, and list
     * their medical history (medications, dosage, and allergies) next to each name.
     *
     * @param fireStationNumberList a list of fire station numbers
     * @return a map of address to list of persons with medical infos
     */
    @RequestMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonWithMedicalInfosDTO>>>
            getHouseholdsByFireStationNumberList(
                    @RequestParam(name = "stations") List<Integer> fireStationNumberList) {
        List<String> addressList =
                fireStationService
                        .getFireStationListByFireStationNumberList(fireStationNumberList)
                        .stream()
                        .map(FireStation::getAddress)
                        .distinct()
                        .toList();
        Map<String, List<PersonWithMedicalInfosDTO>> result =
                populationService.getPersonListByAddressList(addressList).stream()
                        .map(medicalRecordService::mapToPersonWithMedicalInfosDTO)
                        .collect(Collectors.groupingBy(PersonWithMedicalInfosDTO::address));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Return a list of residents living at the given address, as well as the number of the fire
     * station serving that address. The list must include the name, phone number, age, and medical
     * history (medications, dosage, and allergies) of each person.
     *
     * @param address the address to search for residents
     * @return a PopulationByFireStationsDTO
     */
    @RequestMapping("/fire")
    public ResponseEntity<PopulationByFireStationsDTO> getPersonAndFireStationListByAddress(
            @RequestParam String address) {
        List<PersonWithMedicalInfosDTO> personList =
                populationService.getPersonListByAddress(address).stream()
                        .map(medicalRecordService::mapToPersonWithMedicalInfosDTO)
                        .toList();
        List<Integer> fireStationNumberList =
                fireStationService.getFireStationNumberListByAddress(address);
        PopulationByFireStationsDTO result =
                new PopulationByFireStationsDTO(fireStationNumberList, personList);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
