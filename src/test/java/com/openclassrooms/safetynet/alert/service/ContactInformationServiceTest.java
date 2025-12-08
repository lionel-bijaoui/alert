package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.openclassrooms.safetynet.alert.dto.PersonDTO;
import com.openclassrooms.safetynet.alert.dto.PersonWithAge;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class ContactInformationServiceTest {

    @Mock FireStationService fireStationService;

    @Mock PopulationService populationService;

    @Mock MedicalRecordService medicalRecordService;

    @Mock PersonMapper personMapper;

    @InjectMocks ContactInformationService contactInformationService;

    Person person;
    List<Person> personList;

    @BeforeEach
    void setUp() {
        person = new PersonTestBuilder().build();
        personList = new ArrayList<>(List.of(person));
    }

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnPhoneNumberList_whenFireStationNumberExists() {
        FireStation fireStation = new FireStationTestBuilder().build();

        List<String> fireStationList = List.of(fireStation.getAddress());

        when(fireStationService.getFireStationAddressListByFireStationNumber(anyInt()))
                .thenReturn(fireStationList);
        when(populationService.getPersonListByAddressList(fireStationList)).thenReturn(personList);

        List<String> result =
                contactInformationService.getPhoneNumberListByFireStationNumber(
                        fireStation.getStation());

        assertEquals(result.size(), personList.size());
        assertTrue(result.contains(person.getPhone()));
    }

    @Test
    void
            getPhoneNumberListByFireStationNumber_shouldReturnEmptyList_whenFireStationNumberDoesNotExist() {
        Integer fireStationNumber = 99;
        List<String> fireStationList = List.of();

        when(fireStationService.getFireStationAddressListByFireStationNumber(fireStationNumber))
                .thenReturn(fireStationList);
        when(populationService.getPersonListByAddressList(fireStationList)).thenReturn(List.of());

        List<String> result =
                contactInformationService.getPhoneNumberListByFireStationNumber(fireStationNumber);
        assertTrue(result.isEmpty());
    }

    @Test
    void getChildrenListByAddress_shouldReturnChildrenList_whenChildrenExist() {
        Person child =
                new PersonTestBuilder()
                        .withFirstName("Jojo")
                        .withEmail("jojodoe@email.com")
                        .build();

        personList.add(child);
        when(populationService.getPersonListByAddress(person.getAddress())).thenReturn(personList);
        when(medicalRecordService.enrichPersonsWithAge(any()))
                .thenReturn(List.of(new PersonWithAge(person, 99), new PersonWithAge(child, 1)));
        when(personMapper.toDto(person))
                .thenReturn(
                        new PersonDTO(
                                person.getFirstName(),
                                person.getLastName(),
                                person.getAddress(),
                                person.getCity(),
                                person.getZip(),
                                person.getPhone(),
                                person.getEmail()));

        var result = contactInformationService.getChildrenListByAddress(person.getAddress());

        assertEquals(1, result.children().size());
        assertEquals(child.getFirstName(), result.children().getFirst().firstName());
        assertEquals(child.getLastName(), result.children().getFirst().lastName());
        assertEquals(1, result.children().getFirst().age());
    }

    @Test
    void getChildrenListByAddress_shouldReturnEmptyList_whenNoChildrenExist() {
        when(populationService.getPersonListByAddress(person.getAddress())).thenReturn(personList);

        var result = contactInformationService.getChildrenListByAddress(person.getAddress());

        assertTrue(result.children().isEmpty());
    }
}
