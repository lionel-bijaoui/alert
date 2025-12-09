package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;

import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Optional;

/** Service class for managing Person entities. */
@Slf4j
@Service
public class PersonService {

    private final JsonPersonRepository jsonPersonRepository;

    public PersonService(JsonPersonRepository jsonPersonRepository) {
        this.jsonPersonRepository = jsonPersonRepository;
    }

    // CRUD operations for Person

    /**
     * Add a new person.
     *
     * @param person the person to add
     * @return the saved person
     * @throws ConflictException if a person already exists with the same first and last name
     */
    @NotNull
    public Person addPerson(Person person) {
        Optional<Person> maybePerson =
                jsonPersonRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName());

        if (maybePerson.isPresent()) {
            log.warn(
                    "Attempted to add a person that already exists: {} {}",
                    person.getFirstName(),
                    person.getLastName());
            throw new ConflictException(
                    "Person already exists: " + person.getFirstName() + " " + person.getLastName());
        }

        return jsonPersonRepository.save(person);
    }

    /**
     * Update an existing person.
     *
     * @param person the person to update
     * @return the updated person
     * @throws ResourceNotFoundException if the person does not exist
     */
    @NotNull
    public Person updatePerson(Person person) {
        Optional<Person> maybePerson =
                jsonPersonRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName());

        if (maybePerson.isEmpty()) {
            log.warn(
                    "Attempted to update a person that does not exist : {} {}",
                    person.getFirstName(),
                    person.getLastName());
            throw new ResourceNotFoundException(
                    "Person does not exist: " + person.getFirstName() + " " + person.getLastName());
        }

        Person existing = maybePerson.get();
        existing.setAddress(person.getAddress());
        existing.setCity(person.getCity());
        existing.setZip(person.getZip());
        existing.setPhone(person.getPhone());
        existing.setEmail(person.getEmail());

        return jsonPersonRepository.save(existing);
    }

    /**
     * Delete a person by first and last name.
     *
     * @param firstName the first name of the person to delete
     * @param lastName the last name of the person to delete
     * @throws ResourceNotFoundException if the person does not exist
     */
    public void deletePerson(String firstName, String lastName) {
        Optional<Person> maybePerson =
                jsonPersonRepository.findByFirstNameAndLastName(firstName, lastName);

        if (maybePerson.isEmpty()) {
            log.warn(
                    "Attempted to delete a person that does not exist: {} {}", firstName, lastName);
            throw new ResourceNotFoundException(
                    "No existing person to delete: " + firstName + " " + lastName);
        }

        jsonPersonRepository.deleteByFirstNameAndLastName(firstName, lastName);
    }
}
