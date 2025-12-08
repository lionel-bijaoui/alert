package com.openclassrooms.safetynet.alert.service;

import com.openclassrooms.safetynet.alert.dto.ChildDTO;
import com.openclassrooms.safetynet.alert.dto.ChildrenAndAdultsDTO;
import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.dto.PersonWithAge;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.Person;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContactInformationService {

    private final FireStationService fireStationService;
    private final PopulationService populationService;
    private final MedicalRecordService medicalRecordService;
    private final PersonMapper personMapper;

    public ContactInformationService(
            FireStationService fireStationService,
            PopulationService populationService,
            MedicalRecordService medicalRecordService,
            PersonMapper personMapper) {
        this.fireStationService = fireStationService;
        this.populationService = populationService;
        this.medicalRecordService = medicalRecordService;
        this.personMapper = personMapper;
    }

    public List<String> getPhoneNumberListByFireStationNumber(Integer fireStationNumber) {
        List<String> fireStationAddressListByFireStationNumber =
                fireStationService.getFireStationAddressListByFireStationNumber(fireStationNumber);

        return populationService
                .getPersonListByAddressList(fireStationAddressListByFireStationNumber)
                .stream()
                .map(Person::getPhone)
                .toList();
    }

    public ChildrenAndAdultsDTO getChildrenListByAddress(String address) {
        List<Person> personsAtAddress = populationService.getPersonListByAddress(address);

        // Partition into children and adults
        Map<Boolean, List<PersonWithAge>> partitioned =
                medicalRecordService.enrichPersonsWithAge(personsAtAddress).stream()
                        .collect(Collectors.partitioningBy(p -> p.age() <= 18));

        List<ChildDTO> children =
                partitioned.get(true).stream()
                        .map(
                                child ->
                                        new ChildDTO(
                                                child.person().getFirstName(),
                                                child.person().getLastName(),
                                                child.age()))
                        .toList();

        List<PersonDTO> adults =
                partitioned.get(false).stream().map(p -> personMapper.toDto(p.person())).toList();

        return new ChildrenAndAdultsDTO(children, adults);
    }
}
