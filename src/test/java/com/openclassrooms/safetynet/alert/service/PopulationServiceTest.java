package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.openclassrooms.safetynet.alert.dto.PersonSummaryDTO;
import com.openclassrooms.safetynet.alert.dto.PersonWithAge;
import com.openclassrooms.safetynet.alert.dto.PersonWithMedicalInfosDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationDTO;
import com.openclassrooms.safetynet.alert.dto.PopulationByFireStationsDTO;
import com.openclassrooms.safetynet.alert.mapper.PersonMapper;
import com.openclassrooms.safetynet.alert.model.FireStation;
import com.openclassrooms.safetynet.alert.model.MedicalRecord;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.FireStationTestBuilder;
import com.openclassrooms.safetynet.alert.utils.MedicalRecordTestBuilder;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
public class PopulationServiceTest {

    @Mock JsonPersonRepository jsonPersonRepository;

    @Mock FireStationService fireStationService;

    @Mock MedicalRecordService medicalRecordService;

    @Mock PersonMapper personMapper;

    @InjectMocks PopulationService populationService;

    Person personA;
    Person personB;
    Person personC;

    @BeforeEach
    void setUp() {
        personA = new PersonTestBuilder().build();
        personB =
                new PersonTestBuilder()
                        .withFirstName("Jane")
                        .withLastName("Smith")
                        .withAddress("29 15th St")
                        .withEmail("janesmith@email.com")
                        .build();
        personC =
                new PersonTestBuilder()
                        .withFirstName("Jojo")
                        .withEmail("jojodoe@email.com")
                        .build();
    }

    @Test
    void getPersonListByAddressList_shouldReturnPersonList_whenAddressListProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));
        List<String> addressList = List.of(personA.getAddress(), personB.getAddress());

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertEquals(2, result.size());
        assertTrue(result.contains(personA));
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddressList_shouldReturnEmptyList_whenNoMatchingAddresses() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));
        List<String> addressList = List.of(personB.getAddress());

        List<Person> result = populationService.getPersonListByAddressList(addressList);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByAddress_shouldReturnPersonList_whenAddressProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<Person> result = populationService.getPersonListByAddress(personB.getAddress());

        assertEquals(1, result.size());
        assertTrue(result.contains(personB));
    }

    @Test
    void getPersonListByAddress_shouldReturnEmptyList_whenNoMatchingAddress() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<Person> result = populationService.getPersonListByAddress(personB.getAddress());

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonListByLastName_shouldReturnPersonList_whenLastNameProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personC));
        int personAAge = 40;
        int personCAge = 14;

        MedicalRecord medicalRecordA = new MedicalRecordTestBuilder().build();
        MedicalRecord medicalRecordC =
                new MedicalRecordTestBuilder()
                        .withFirstName(personC.getFirstName())
                        .withLastName(personC.getLastName())
                        .withBirthdate(LocalDate.parse("2010-12-01"))
                        .withMedications(List.of())
                        .withAllergies(List.of("peanut"))
                        .build();

        PersonWithMedicalInfosDTO personAWithMedicalInfosDTO =
                new PersonWithMedicalInfosDTO(
                        personA.getFirstName(),
                        personA.getLastName(),
                        personA.getAddress(),
                        personA.getPhone(),
                        personA.getEmail(),
                        personAAge,
                        medicalRecordA.getMedications(),
                        medicalRecordA.getAllergies());
        PersonWithMedicalInfosDTO personCWithMedicalInfosDTO =
                new PersonWithMedicalInfosDTO(
                        personC.getFirstName(),
                        personC.getLastName(),
                        personC.getAddress(),
                        personC.getPhone(),
                        personC.getEmail(),
                        personCAge,
                        medicalRecordC.getMedications(),
                        medicalRecordC.getAllergies());

        when(medicalRecordService.mapToPersonWithMedicalInfosDTO(personA))
                .thenReturn(personAWithMedicalInfosDTO);
        when(medicalRecordService.mapToPersonWithMedicalInfosDTO(personC))
                .thenReturn(personCWithMedicalInfosDTO);

        List<PersonWithMedicalInfosDTO> result =
                populationService.getPersonListByLastName(personA.getLastName());

        assertEquals(2, result.size());
        assertEquals(personA.getFirstName(), result.getFirst().firstName());
        assertEquals(personA.getLastName(), result.getFirst().lastName());
        assertEquals(personA.getAddress(), result.getFirst().address());
        assertEquals(personA.getPhone(), result.getFirst().phone());
        assertEquals(personA.getEmail(), result.getFirst().email());
        assertEquals(personAAge, result.getFirst().age());
        assertEquals(medicalRecordA.getMedications(), result.getFirst().medications());
        assertEquals(medicalRecordA.getAllergies(), result.getFirst().allergies());

        assertEquals(personC.getFirstName(), result.getLast().firstName());
        assertEquals(personC.getLastName(), result.getLast().lastName());
        assertEquals(personC.getAddress(), result.getLast().address());
        assertEquals(personC.getPhone(), result.getLast().phone());
        assertEquals(personC.getEmail(), result.getLast().email());
        assertEquals(personCAge, result.getLast().age());
        assertEquals(medicalRecordC.getMedications(), result.getLast().medications());
        assertEquals(medicalRecordC.getAllergies(), result.getLast().allergies());
    }

    @Test
    void getPersonListByLastName_shouldReturnEmptyList_whenNoMatchingLastName() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<PersonWithMedicalInfosDTO> result =
                populationService.getPersonListByLastName("NonExistingLastName");

        assertTrue(result.isEmpty());
    }

    @Test
    void getPersonEmailListByCity_shouldReturnPersonEmailList_whenCityProvided() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA, personB));

        List<String> result = populationService.getPersonEmailListByCity(personA.getCity());

        assertEquals(2, result.size());
        assertTrue(result.contains(personA.getEmail()));
        assertTrue(result.contains(personB.getEmail()));
    }

    @Test
    void getPersonEmailListByCity_shouldReturnEmptyEmailList_whenNoMatchingCity() {
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));

        List<String> result = populationService.getPersonEmailListByCity("NonExistingCity");

        assertTrue(result.isEmpty());
    }

    @Test
    void
            getHouseholdsByFireStationNumberList_shouldReturnHouseholdsList_whenFireStationNumberListExists() {
        FireStation fireStation = new FireStationTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        Integer personAge = 30;

        when(fireStationService.getAddressListFromFireStationNumberList(anyList()))
                .thenReturn(List.of(fireStation.getAddress()));
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));
        when(medicalRecordService.mapToPersonWithMedicalInfosDTO(personA))
                .thenReturn(
                        new PersonWithMedicalInfosDTO(
                                personA.getFirstName(),
                                personA.getLastName(),
                                personA.getAddress(),
                                personA.getPhone(),
                                personA.getEmail(),
                                personAge,
                                medicalRecord.getMedications(),
                                medicalRecord.getAllergies()));

        Map<String, List<PersonWithMedicalInfosDTO>> result =
                populationService.getHouseholdsByFireStationNumberList(List.of(3, 2));

        assertEquals(1, result.size());
        assertTrue(result.containsKey(personA.getAddress()));
        List<PersonWithMedicalInfosDTO> personsAtAddress = result.get(personA.getAddress());
        assertEquals(1, personsAtAddress.size());
        assertEquals(personA.getFirstName(), personsAtAddress.getFirst().firstName());
        assertEquals(personA.getLastName(), personsAtAddress.getFirst().lastName());
        assertEquals(personA.getAddress(), personsAtAddress.getFirst().address());
        assertEquals(personA.getPhone(), personsAtAddress.getFirst().phone());
        assertEquals(personA.getEmail(), personsAtAddress.getFirst().email());
        assertEquals(personAge, personsAtAddress.getFirst().age());
        assertEquals(medicalRecord.getMedications(), personsAtAddress.getFirst().medications());
        assertEquals(medicalRecord.getAllergies(), personsAtAddress.getFirst().allergies());
    }

    @Test
    void
            getHouseholdsByFireStationNumberList_shouldReturnEmptyList_whenFireStationNumberListDoesNotExist() {
        when(fireStationService.getAddressListFromFireStationNumberList(anyList()))
                .thenReturn(List.of());

        Map<String, List<PersonWithMedicalInfosDTO>> result =
                populationService.getHouseholdsByFireStationNumberList(List.of(99));

        assertTrue(result.isEmpty());
    }

    @Test
    void
            getPersonAndFireStationListByAddress_shouldReturnPersonAndFireStationList_whenAddressExists() {
        FireStation fireStation = new FireStationTestBuilder().build();
        MedicalRecord medicalRecord = new MedicalRecordTestBuilder().build();
        Integer personAge = 30;

        when(fireStationService.getFireStationNumberListByAddress(personA.getAddress()))
                .thenReturn(List.of(fireStation.getStation()));
        when(jsonPersonRepository.findAll()).thenReturn(List.of(personA));
        when(medicalRecordService.mapToPersonWithMedicalInfosDTO(personA))
                .thenReturn(
                        new PersonWithMedicalInfosDTO(
                                personA.getFirstName(),
                                personA.getLastName(),
                                personA.getAddress(),
                                personA.getPhone(),
                                personA.getEmail(),
                                personAge,
                                medicalRecord.getMedications(),
                                medicalRecord.getAllergies()));

        PopulationByFireStationsDTO result =
                populationService.getPersonAndFireStationListByAddress(personA.getAddress());

        assertEquals(1, result.fireStationNumbers().size());
        assertEquals(fireStation.getStation(), result.fireStationNumbers().getFirst());
        assertEquals(1, result.population().size());
        assertEquals(personA.getFirstName(), result.population().getFirst().firstName());
        assertEquals(personA.getLastName(), result.population().getFirst().lastName());
        assertEquals(personA.getAddress(), result.population().getFirst().address());
        assertEquals(personA.getPhone(), result.population().getFirst().phone());
        assertEquals(personA.getEmail(), result.population().getFirst().email());
        assertEquals(personAge, result.population().getFirst().age());
        assertEquals(medicalRecord.getMedications(), result.population().getFirst().medications());
        assertEquals(medicalRecord.getAllergies(), result.population().getFirst().allergies());
    }

    @Test
    void getPersonAndFireStationListByAddress_shouldReturnEmptyLists_whenAddressNotFound() {
        when(fireStationService.getFireStationNumberListByAddress(anyString()))
                .thenReturn(List.of());

        PopulationByFireStationsDTO result =
                populationService.getPersonAndFireStationListByAddress("Unknown");

        assertTrue(result.fireStationNumbers().isEmpty());
        assertTrue(result.population().isEmpty());
    }

    @Test
    void getPersonListByFireStationNumber_shouldReturnPersonList_whenExists() {
        Person adult = new PersonTestBuilder().build();
        Person child =
                new PersonTestBuilder().withFirstName("Lola").withAddress("29 15th St").build();
        FireStation existingFireStation = new FireStationTestBuilder().build();

        when(fireStationService.getFireStationAddressListByFireStationNumber(
                        existingFireStation.getStation()))
                .thenReturn(List.of(adult.getAddress(), child.getAddress()));
        when(medicalRecordService.enrichPersonsWithAge(any()))
                .thenReturn(List.of(new PersonWithAge(adult, 99), new PersonWithAge(child, 1)));
        when(personMapper.toSummaryDto(adult))
                .thenReturn(
                        new PersonSummaryDTO(
                                adult.getFirstName(),
                                adult.getLastName(),
                                adult.getAddress(),
                                adult.getPhone()));
        when(personMapper.toSummaryDto(child))
                .thenReturn(
                        new PersonSummaryDTO(
                                child.getFirstName(),
                                child.getLastName(),
                                child.getAddress(),
                                child.getPhone()));

        PopulationByFireStationDTO result =
                populationService.getPersonListByStationNumber(existingFireStation.getStation());

        assertEquals(2, result.population().size());
        assertEquals(1, result.adultCount());
        assertEquals(1, result.childCount());
    }

    @Test
    void getPersonListByFireStationNumber_shouldReturnEmptyList_whenNotExists() {
        FireStation nonExistingFireStation = new FireStationTestBuilder().withStation(99).build();
        when(fireStationService.getFireStationAddressListByFireStationNumber(
                        nonExistingFireStation.getStation()))
                .thenReturn(List.of());

        PopulationByFireStationDTO result =
                populationService.getPersonListByStationNumber(nonExistingFireStation.getStation());

        assertTrue(result.population().isEmpty());
        assertEquals(0, result.adultCount());
        assertEquals(0, result.childCount());
    }
}
