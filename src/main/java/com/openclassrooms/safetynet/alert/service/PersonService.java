package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import java.util.Optional;

/** Service class for managing Person entities. */
@Service
public class PersonService {

    private final JsonPersonRepository jsonPersonRepository;

    public PersonService(JsonPersonRepository jsonPersonRepository) {
        this.jsonPersonRepository = jsonPersonRepository;
    }

    // CRUD operations for Person

    @NotNull
    public Person addPerson(Person person) {
        Optional<Person> maybePerson =
                jsonPersonRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName());

        if (maybePerson.isPresent()) {
            throw new ConflictException(
                    "Person already exists: " + person.getFirstName() + " " + person.getLastName());
        }

        return jsonPersonRepository.save(person);
    }

    @NotNull
    public Person updatePerson(Person person) {
        Optional<Person> maybePerson =
                jsonPersonRepository.findByFirstNameAndLastName(
                        person.getFirstName(), person.getLastName());

        if (maybePerson.isEmpty()) {
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

    public void deletePerson(String firstName, String lastName) {
        Optional<Person> maybePerson =
                jsonPersonRepository.findByFirstNameAndLastName(firstName, lastName);

        if (maybePerson.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No existing person to delete: " + firstName + " " + lastName);
        }

        jsonPersonRepository.deleteByFirstNameAndLastName(firstName, lastName);
    }
}
