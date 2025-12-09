package com.openclassrooms.safetynet.alert.repository;

import com.openclassrooms.safetynet.alert.model.Database;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.store.JsonFileDataStore;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository exposing person-related operations backed by {@link JsonFileDataStore}. */
@Repository
public class JsonPersonRepository implements PersonRepository {

    private final JsonFileDataStore jsonFileDataStore;

    public JsonPersonRepository(JsonFileDataStore jsonFileDataStore) {
        this.jsonFileDataStore = jsonFileDataStore;
    }

    @Override
    public Person save(Person person) {
        Database database = jsonFileDataStore.getCachedDatabase();
        List<Person> persons = database.getPersons();

        persons.removeIf(
                p ->
                        p.getFirstName().equalsIgnoreCase(person.getFirstName())
                                && p.getLastName().equalsIgnoreCase(person.getLastName()));
        persons.add(person);

        database.setPersons(persons);
        jsonFileDataStore.setCachedDatabase(database);

        return person;
    }

    @Override
    public List<Person> findAll() {
        return jsonFileDataStore.getCachedDatabase().getPersons();
    }

    @Override
    public Optional<Person> findByFirstNameAndLastName(String firstName, String lastName) {
        return findAll().stream()
                .filter(
                        person ->
                                person.getFirstName().equalsIgnoreCase(firstName)
                                        && person.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
    }

    @Override
    public List<Person> findByAddress(String address) {
        return findAll().stream()
                .filter(person -> person.getAddress().equalsIgnoreCase(address))
                .toList();
    }

    @Override
    public void deleteByFirstNameAndLastName(String firstName, String lastName) {
        Database database = jsonFileDataStore.getCachedDatabase();

        List<Person> persons = database.getPersons();

        persons.removeIf(
                p ->
                        p.getFirstName().equalsIgnoreCase(firstName)
                                && p.getLastName().equalsIgnoreCase(lastName));

        database.setPersons(persons);
        jsonFileDataStore.setCachedDatabase(database);
    }
}
