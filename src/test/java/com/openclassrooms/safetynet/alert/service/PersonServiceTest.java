package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(TestSentenceGenerator.class)
class PersonServiceTest {

    static final String EXISTING_FIRST = "John";
    static final String EXISTING_LAST = "Boyd";
    static final String EXISTING_ADDRESS = "1509 Culver St";
    static final String EXISTING_CITY = "Culver";
    static final String EXISTING_ZIP = "97451";
    static final String EXISTING_PHONE = "841-874-6512";
    static final String EXISTING_EMAIL = "john.boyd@email.com";

    @Mock JsonPersonRepository jsonPersonRepository;

    @InjectMocks PersonService personService;

    Person existingPerson;

    @BeforeEach
    void setUp() {
        existingPerson =
                new Person(
                        EXISTING_FIRST,
                        EXISTING_LAST,
                        EXISTING_ADDRESS,
                        EXISTING_CITY,
                        EXISTING_ZIP,
                        EXISTING_PHONE,
                        EXISTING_EMAIL);
    }

    @Test
    void addPerson_shouldReturnSavedPerson_whenNotPresent() {
        Person toAdd = new Person("New", "Person", "123 New St", "City", "00000", "000", "n@p.com");

        when(jsonPersonRepository.findByFirstNameAndLastName(
                        toAdd.getFirstName(), toAdd.getLastName()))
                .thenReturn(Optional.empty());
        when(jsonPersonRepository.save(toAdd)).thenReturn(toAdd);

        Person result = personService.addPerson(toAdd);

        assertNotNull(result);
        assertEquals(toAdd.getFirstName(), result.getFirstName());
        assertEquals(toAdd.getLastName(), result.getLastName());

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(toAdd.getFirstName(), toAdd.getLastName());
        verify(jsonPersonRepository, times(1)).save(toAdd);
    }

    @Test
    void addPerson_shouldThrowConflictException_whenPersonExists() {
        when(jsonPersonRepository.findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST))
                .thenReturn(Optional.of(existingPerson));

        assertThrows(ConflictException.class, () -> personService.addPerson(existingPerson));

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
        verify(jsonPersonRepository, never()).save(any());
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson_whenExists() {
        Person updated =
                new Person(
                        EXISTING_FIRST,
                        EXISTING_LAST,
                        "New Addr",
                        EXISTING_CITY,
                        EXISTING_ZIP,
                        EXISTING_PHONE,
                        EXISTING_EMAIL);

        when(jsonPersonRepository.findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST))
                .thenReturn(Optional.of(existingPerson));
        when(jsonPersonRepository.save(any(Person.class))).thenReturn(updated);

        Person result = personService.updatePerson(updated);

        assertNotNull(result);
        assertEquals(EXISTING_FIRST, result.getFirstName());
        assertEquals("New Addr", result.getAddress());

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
        verify(jsonPersonRepository, times(1)).save(any(Person.class));
    }

    @Test
    void updatePerson_shouldThrowResourceNotFoundException_whenNotExists() {
        Person updated = new Person("No", "Body", "Addr", "City", "00000", "000", "e@x.com");

        when(jsonPersonRepository.findByFirstNameAndLastName(
                        updated.getFirstName(), updated.getLastName()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personService.updatePerson(updated));

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(updated.getFirstName(), updated.getLastName());
        verify(jsonPersonRepository, never()).save(any());
    }

    @Test
    void deletePerson_shouldRemovePerson_whenExists() {
        when(jsonPersonRepository.findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST))
                .thenReturn(Optional.of(existingPerson));
        doNothing()
                .when(jsonPersonRepository)
                .deleteByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);

        personService.deletePerson(EXISTING_FIRST, EXISTING_LAST);

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
        verify(jsonPersonRepository, times(1))
                .deleteByFirstNameAndLastName(EXISTING_FIRST, EXISTING_LAST);
    }

    @Test
    void deletePerson_shouldThrowResourceNotFoundException_whenNotExists() {
        when(jsonPersonRepository.findByFirstNameAndLastName("Nobody", "Here"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> personService.deletePerson("Nobody", "Here"));

        verify(jsonPersonRepository, times(1)).findByFirstNameAndLastName("Nobody", "Here");
        verify(jsonPersonRepository, never()).deleteByFirstNameAndLastName(any(), any());
    }
}
