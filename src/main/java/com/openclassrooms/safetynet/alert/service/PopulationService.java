package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;

import org.springframework.stereotype.Service;

import java.util.List;

/** Service class for population-related operations. */
@Service
public class PopulationService {

    private final JsonPersonRepository jsonPersonRepository;

    public PopulationService(JsonPersonRepository jsonPersonRepository) {
        this.jsonPersonRepository = jsonPersonRepository;
    }

    public List<Person> getPersonListByAddressList(List<String> addressList) {
        return jsonPersonRepository.findAll().stream()
                .filter(person -> addressList.contains(person.getAddress()))
                .toList();
    }

    public List<Person> getPersonListByAddress(String address) {
        return jsonPersonRepository.findAll().stream()
                .filter(person -> person.getAddress().equals(address))
                .toList();
    }

    public List<Person> getPersonListByCity(String city) {
        return jsonPersonRepository.findAll().stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .toList();
    }
}
