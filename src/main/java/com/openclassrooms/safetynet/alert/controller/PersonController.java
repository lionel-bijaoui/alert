package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.PersonService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for handling person-related requests. */
@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;
    private final PersonMapper personMapper;

    public PersonController(PersonService personService, PersonMapper personMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    /**
     * Add a new person
     *
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<PersonDTO> addPerson(@Valid @RequestBody PersonDTO dto) {
        Person entity = personMapper.toEntity(dto);
        entity = personService.addPerson(entity);
        PersonDTO result = personMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing person
     *
     * @param dto
     * @return
     */
    @PutMapping
    public ResponseEntity<PersonDTO> updatePerson(@Valid @RequestBody PersonDTO dto) {
        Person entity = personMapper.toEntity(dto);
        entity = personService.updatePerson(entity);
        PersonDTO result = personMapper.toDto(entity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Delete a person
     *
     * @param firstName
     * @param lastName
     */
    @DeleteMapping
    public void deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        personService.deletePerson(firstName, lastName);
    }
}
