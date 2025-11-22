package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.FireStationDTO;
import com.openclassrooms.safetynet.alert.mapper.FireStationMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.service.FireStationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for managing fire station related operations. */
@RestController
@RequestMapping("/firestation")
public class FireStationController {

    private final FireStationService fireStationService;
    private final FireStationMapper fireStationMapper;

    public FireStationController(
            FireStationService fireStationService, FireStationMapper fireStationMapper) {
        this.fireStationService = fireStationService;
        this.fireStationMapper = fireStationMapper;
    }

    /**
     * Add a fire station/address mapping
     *
     * @param dto
     * @return
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
     * @param dto
     * @return
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
     * @param address
     */
    @DeleteMapping
    public void deleteFireStation(@RequestParam String address) {
        fireStationService.deleteFireStation(address);
    }
}
