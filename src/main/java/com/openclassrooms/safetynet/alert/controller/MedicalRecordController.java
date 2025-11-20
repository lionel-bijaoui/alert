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

    @PostMapping
    public ResponseEntity<MedicalRecordDTO> addMedicalRecord(
            @Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecord entity = medicalRecordMapper.toEntity(dto);
        entity = medicalRecordService.addMedicalRecord(entity);
        MedicalRecordDTO result = medicalRecordMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(
            @Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecord entity = medicalRecordMapper.toEntity(dto);
        entity = medicalRecordService.updateMedicalRecord(entity);
        MedicalRecordDTO result = medicalRecordMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping
    public void deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        medicalRecordService.deleteMedicalRecord(firstName, lastName);
    }
}
