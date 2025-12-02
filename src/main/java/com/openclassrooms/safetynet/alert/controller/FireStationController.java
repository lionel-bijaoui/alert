package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.dto.PersonSummaryDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationDTO;
import com.openclassrooms.safetynet.alert.mapper.FireStationMapper;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Controller for managing fire station related operations. */
@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final FireStationService fireStationService;
    private final FireStationMapper fireStationMapper;
    private final PopulationService populationService;
    private final MedicalRecordService medicalRecordService;
    private final PersonMapper personMapper;

    public FireStationController(
            FireStationService fireStationService,
            FireStationMapper fireStationMapper,
            PopulationService populationService,
            MedicalRecordService medicalRecordService,
            PersonMapper personMapper) {

        this.fireStationService = fireStationService;
        this.fireStationMapper = fireStationMapper;
        this.populationService = populationService;
        this.medicalRecordService = medicalRecordService;
        this.personMapper = personMapper;
    }

    /**
     * Add a fire station/address mapping
     *
     * @param dto the FireStationDTO to add
     * @return the created FireStationDTO
     */
    @PostMapping
    public ResponseEntity<FireStationDTO> addFireStation(@Valid @RequestBody FireStationDTO dto) {
        FireStation entity = fireStationMapper.toEntity(dto);
        entity = fireStationService.addFireStation(entity);
        FireStationDTO result = fireStationMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update the fire station number for an address
     *
     * @param dto the FireStationDTO to update
     * @return the updated FireStationDTO
     */
    @PutMapping
    public ResponseEntity<FireStationDTO> updateFireStation(
            @Valid @RequestBody FireStationDTO dto) {
        FireStation entity = fireStationMapper.toEntity(dto);
        entity = fireStationService.updateFireStation(entity);
        FireStationDTO result = fireStationMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Delete the mapping for a fire station or address
     *
     * @param address the address of the fire station to delete
     */
    @DeleteMapping
    public void deleteFireStation(@RequestParam String address) {
        fireStationService.deleteFireStation(address);
    }

    /**
     * Return a list of people covered by the corresponding fire station. So, if the station number
     * = 1, it must return the residents covered by station number 1. The list must include the
     * following specific information: first name, last name, address, phone number. In addition, it
     * must provide a count of the number of adults and the number of children (any individual aged
     * 18 or younger) in the area served.
     *
     * @param stationNumber the fire station number
     * @return a PopulationByFireStationDTO
     */
    @GetMapping
    public ResponseEntity<PopulationByFireStationDTO> getPersonListByStationNumber(
            @RequestParam int stationNumber) {
        List<Person> personsAtFireStationAddress =
                populationService.getPersonListByAddressList(
                        fireStationService.getFireStationAddressListByFireStationNumber(
                                stationNumber));
        Map<Boolean, List<PersonSummaryDTO>> personsAtFireStationAddressByAgeGroup =
                medicalRecordService.enrichPersonsWithAge(personsAtFireStationAddress).stream()
                        .collect(
                                Collectors.partitioningBy(
                                        personWithAge -> personWithAge.age() <= 18,
                                        Collectors.mapping(
                                                personWithAge ->
                                                        personMapper.toSummaryDto(
                                                                personWithAge.person()),
                                                Collectors.toList())));
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new PopulationByFireStationDTO(
                                personsAtFireStationAddressByAgeGroup.values().stream()
                                        .flatMap(List::stream)
                                        .toList(),
                                personsAtFireStationAddressByAgeGroup
                                        .getOrDefault(false, List.of())
                                        .size(),
                                personsAtFireStationAddressByAgeGroup
                                        .getOrDefault(true, List.of())
                                        .size()));
    }
}
