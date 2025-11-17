package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.model.Person;

import java.util.List;
import java.util.Optional;

/** Repository interface for managing Person entities. */
public interface PersonRepository {

    /**
     * Save a person. If the person already exists (based on first and last name), update their
     * information.
     */
    Person save(Person item);

    /** Return all persons from the data store, or an empty list if none. */
    List<Person> findAll();

    /** Find a person by first and last name (case-insensitive). */
    Optional<Person> findByFirstNameAndLastName(String firstName, String lastName);

    /** Find all persons matching an address. */
    List<Person> findByAddress(String address);

    /** Delete a person by first and last name (case-insensitive). */
    void deleteByFirstNameAndLastName(String firstName, String lastName);
}
