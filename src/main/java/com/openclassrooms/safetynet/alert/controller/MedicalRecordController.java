package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.MedicalRecordDTO;
import com.openclassrooms.safetynet.alert.mapper.MedicalRecordMapper;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for handling medical record-related requests. */
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordController(
            MedicalRecordService medicalRecordService, MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordService = medicalRecordService;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    /**
     * Add a medical record
     *
     * @param dto the medical record DTO to add
     * @return the created medical record DTO
     */
    @PostMapping
    public ResponseEntity<MedicalRecordDTO> addMedicalRecord(
            @Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecord entity = medicalRecordMapper.toEntity(dto);
        entity = medicalRecordService.addMedicalRecord(entity);
        MedicalRecordDTO result = medicalRecordMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing medical record
     *
     * @param dto the medical record DTO to update
     * @return the updated medical record DTO
     */
    @PutMapping
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(
            @Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecord entity = medicalRecordMapper.toEntity(dto);
        entity = medicalRecordService.updateMedicalRecord(entity);
        MedicalRecordDTO result = medicalRecordMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Delete a medical record
     *
     * @param firstName first name of the person
     * @param lastName last name of the person
     */
    @DeleteMapping
    public void deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        medicalRecordService.deleteMedicalRecord(firstName, lastName);
    }
}
