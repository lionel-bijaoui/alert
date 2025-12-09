package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.PersonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

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
     * @param dto the person DTO to add
     * @return the created person DTO
     */
    @PostMapping
    public ResponseEntity<PersonDTO> addPerson(@Valid @RequestBody PersonDTO dto) {
        Person entity = personMapper.toEntity(dto);
        entity = personService.addPerson(entity);
        PersonDTO body = personMapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Update an existing person
     *
     * @param dto the person DTO to update
     * @return the updated person DTO
     */
    @PutMapping
    public ResponseEntity<PersonDTO> updatePerson(@Valid @RequestBody PersonDTO dto) {
        Person entity = personMapper.toEntity(dto);
        entity = personService.updatePerson(entity);
        PersonDTO body = personMapper.toDto(entity);

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Delete a person
     *
     * @param firstName the first name of the person to delete
     * @param lastName the last name of the person to delete
     */
    @DeleteMapping
    public ResponseEntity<Void> deletePerson(
            @RequestParam @NotBlank(message = "firstName is required") String firstName,
            @RequestParam @NotBlank(message = "lastName is required") String lastName) {
        personService.deletePerson(firstName, lastName);

        return ResponseEntity.noContent().build();
    }
}
