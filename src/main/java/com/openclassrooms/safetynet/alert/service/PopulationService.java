package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.dto.PersonSummaryDTO;
import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationsDTO;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Service class for population-related operations. */
@Service
public class PopulationService {

    private final JsonPersonRepository jsonPersonRepository;
    private final FireStationService fireStationService;
    private final MedicalRecordService medicalRecordService;
    private final PersonMapper personMapper;

    public PopulationService(
            JsonPersonRepository jsonPersonRepository,
            FireStationService fireStationService,
            MedicalRecordService medicalRecordService,
            PersonMapper personMapper) {
        this.jsonPersonRepository = jsonPersonRepository;
        this.fireStationService = fireStationService;
        this.medicalRecordService = medicalRecordService;
        this.personMapper = personMapper;
    }

    public List<Person> getPersonListByAddressList(List<String> addressList) {
        return jsonPersonRepository.findAll().stream()
                .filter(
                        person ->
                                addressList.stream()
                                        .anyMatch(
                                                address ->
                                                        address.equalsIgnoreCase(
                                                                person.getAddress())))
                .toList();
    }

    public List<Person> getPersonListByAddress(String address) {
        return jsonPersonRepository.findAll().stream()
                .filter(person -> person.getAddress().equalsIgnoreCase(address))
                .toList();
    }

    public List<PersonWithMedicalInfosDTO> getPersonListByLastName(String lastName) {
        return jsonPersonRepository.findAll().stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                .map(medicalRecordService::mapToPersonWithMedicalInfosDTO)
                .toList();
    }

    public List<String> getPersonEmailListByCity(String city) {
        return jsonPersonRepository.findAll().stream()
                .filter(person -> person.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .distinct()
                .toList();
    }

    public Map<String, List<PersonWithMedicalInfosDTO>> getHouseholdsByFireStationNumberList(
            List<Integer> fireStationNumberList) {
        List<String> addressList =
                fireStationService.getAddressListFromFireStationNumberList(fireStationNumberList);

        return getPersonListByAddressList(addressList).stream()
                .map(medicalRecordService::mapToPersonWithMedicalInfosDTO)
                .collect(Collectors.groupingBy(PersonWithMedicalInfosDTO::address));
    }

    public PopulationByFireStationsDTO getPersonAndFireStationListByAddress(String address) {
        List<PersonWithMedicalInfosDTO> personList =
                getPersonListByAddress(address).stream()
                        .map(medicalRecordService::mapToPersonWithMedicalInfosDTO)
                        .toList();
        List<Integer> fireStationNumberList =
                fireStationService.getFireStationNumberListByAddress(address);
        return new PopulationByFireStationsDTO(fireStationNumberList, personList);
    }

    public PopulationByFireStationDTO getPersonListByStationNumber(Integer stationNumber) {
        List<Person> personsAtFireStationAddress =
                getPersonListByAddressList(
                        fireStationService.getFireStationAddressListByFireStationNumber(
                                stationNumber));

        Map<Boolean, List<PersonSummaryDTO>> personsAtFireStationAddressByAgeGroup =
                medicalRecordService.enrichPersonsWithAge(personsAtFireStationAddress).stream()
                        .collect(
                                Collectors.partitioningBy(
                                        personWithAge -> personWithAge.age() <= 18,
                                        Collectors.mapping(
                                                personWithAge ->
                                                        personMapper.toSummaryDto(
                                                                personWithAge.person()),
                                                Collectors.toList())));

        return new PopulationByFireStationDTO(
                personsAtFireStationAddressByAgeGroup.values().stream()
                        .flatMap(List::stream)
                        .toList(),
                personsAtFireStationAddressByAgeGroup.getOrDefault(false, List.of()).size(),
                personsAtFireStationAddressByAgeGroup.getOrDefault(true, List.of()).size());
    }
}
