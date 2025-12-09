package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationDTO;
import com.openclassrooms.safetynet.alert.mapper.FireStationMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for managing fire station related operations. */
@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final FireStationService fireStationService;
    private final FireStationMapper fireStationMapper;
    private final PopulationService populationService;

    public FireStationController(
            FireStationService fireStationService,
            FireStationMapper fireStationMapper,
            PopulationService populationService) {
        this.fireStationService = fireStationService;
        this.fireStationMapper = fireStationMapper;
        this.populationService = populationService;
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
        FireStationDTO body = fireStationMapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
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
        FireStationDTO body = fireStationMapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Delete the mapping for a fire station or address
     *
     * @param address the address of the fire station to delete
     * @return a ResponseEntity with no content
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteFireStation(
            @RequestParam @NotBlank(message = "address is required") String address) {
        fireStationService.deleteFireStation(address);

        return ResponseEntity.noContent().build();
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
            @RequestParam @NotNull(message = "stationNumber is required") int stationNumber) {
        PopulationByFireStationDTO body =
                populationService.getPersonListByStationNumber(stationNumber);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
