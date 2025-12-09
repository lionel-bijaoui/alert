package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationsDTO;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Controller for handling hazard-related requests. */
@RestController
public class HazardController {

    private final PopulationService populationService;

    public HazardController(PopulationService populationService) {
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
    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, List<PersonWithMedicalInfosDTO>>>
            getHouseholdsByFireStationNumberList(
                    @RequestParam(name = "stations")
                            @NotNull(message = "fireStationNumberList is required")
                            List<Integer> fireStationNumberList) {
        Map<String, List<PersonWithMedicalInfosDTO>> body =
                populationService.getHouseholdsByFireStationNumberList(fireStationNumberList);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Return a list of residents living at the given address, as well as the number of the fire
     * station serving that address. The list must include the name, phone number, age, and medical
     * history (medications, dosage, and allergies) of each person.
     *
     * @param address the address to search for residents
     * @return a PopulationByFireStationsDTO
     */
    @GetMapping("/fire")
    public ResponseEntity<PopulationByFireStationsDTO> getPersonAndFireStationListByAddress(
            @RequestParam @NotBlank(message = "address is required") String address) {
        PopulationByFireStationsDTO body =
                populationService.getPersonAndFireStationListByAddress(address);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
