package com.openclassrooms.safetynet.alert.controller;

import com.openclassrooms.safetynet.alert.dto.ChildDTO;
import com.openclassrooms.safetynet.alert.dto.ChildrenAndAdultsDTO;
import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.service.FireStationService;
import com.openclassrooms.safetynet.alert.service.MedicalRecordService;
import com.openclassrooms.safetynet.alert.service.PopulationService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Controller for handling alert-related requests. */
@RestController
public class AlertController {

    private FireStationService fireStationService;
    private PopulationService populationService;
    private MedicalRecordService medicalRecordService;
    private PersonMapper personMapper;

    public AlertController(
            FireStationService fireStationService,
            PopulationService populationService,
            MedicalRecordService medicalRecordService,
            PersonMapper personMapper) {

        this.fireStationService = fireStationService;
        this.populationService = populationService;
        this.medicalRecordService = medicalRecordService;
        this.personMapper = personMapper;
    }

    /**
     * Return a list of phone numbers of residents served by the fire station. We will use it to
     * send emergency text messages to specific households.
     *
     * @param firestation
     * @return
     */
    @RequestMapping("/phoneAlert")
    public List<String> getPhoneNumberListByFireStationNumber(@RequestParam int firestation) {
        return populationService
                .getPersonListByAddressList(
                        fireStationService.getFireStationAddressListByFireStationNumber(
                                firestation))
                .stream()
                .map(Person::getPhone)
                .toList();
    }

    /**
     * Return a list of children (any individual aged 18 or under) living at this address. The list
     * must include each child's first and last name, their age, and a list of other members of the
     * household. If there are no children, return an empty string.
     *
     * @param address
     * @return
     */
    @RequestMapping("/childAlert")
    public ChildrenAndAdultsDTO getChildrenListByAddress(@RequestParam String address) {
        List<Person> personsAtAddress = populationService.getPersonListByAddress(address);
        if (personsAtAddress.isEmpty()) {
            return new ChildrenAndAdultsDTO(List.of(), List.of());
        }

        // Cache ages to avoid multiple lookups
        record PersonWithAge(Person person, int age) {}
        List<PersonWithAge> personsWithAge =
                personsAtAddress.stream()
                        .map(person -> new PersonWithAge(person, getAgeFromPerson(person)))
                        .toList();
        // Partition into children and adults
        Map<Boolean, List<PersonWithAge>> partitioned =
                personsWithAge.stream().collect(Collectors.partitioningBy(p -> p.age <= 18));

        List<ChildDTO> children =
                partitioned.get(true).stream()
                        .map(
                                child ->
                                        new ChildDTO(
                                                child.person.getFirstName(),
                                                child.person.getLastName(),
                                                child.age))
                        .toList();

        List<PersonDTO> adults =
                partitioned.get(false).stream().map(p -> personMapper.toDto(p.person)).toList();

        return new ChildrenAndAdultsDTO(children, adults);
    }

    private Integer getAgeFromPerson(Person person) {
        return medicalRecordService.calculateAgeFromBirthdate(
                medicalRecordService
                        .getMedicalRecordByFullName(person.getFirstName(), person.getLastName())
                        .getBirthdate());
    }
}
