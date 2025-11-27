package com.openclassrooms.safetynet.alert.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.safetynet.alert.exception.ConflictException;
import com.openclassrooms.safetynet.alert.exception.ResourceNotFoundException;
import com.openclassrooms.safetynet.alert.model.Person;
import com.openclassrooms.safetynet.alert.repository.JsonPersonRepository;
import com.openclassrooms.safetynet.alert.utils.PersonTestBuilder;
import com.openclassrooms.safetynet.alert.utils.TestSentenceGenerator;

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

    @Mock JsonPersonRepository jsonPersonRepository;

    @InjectMocks PersonService personService;

    @Test
    void addPerson_shouldReturnSavedPerson_whenNotPresent() {
        Person newPerson =
                new PersonTestBuilder()
                        .withFirstName("New")
                        .withLastName("Person")
                        .withAddress("123 New St")
                        .withCity("City")
                        .withZip("00000")
                        .withPhone("000")
                        .withEmail("newperson@email.com")
                        .build();

        when(jsonPersonRepository.findByFirstNameAndLastName(
                        newPerson.getFirstName(), newPerson.getLastName()))
                .thenReturn(Optional.empty());
        when(jsonPersonRepository.save(newPerson)).thenReturn(newPerson);

        Person result = personService.addPerson(newPerson);

        assertNotNull(result);
        assertEquals(newPerson.getFirstName(), result.getFirstName());
        assertEquals(newPerson.getLastName(), result.getLastName());

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(newPerson.getFirstName(), newPerson.getLastName());
        verify(jsonPersonRepository, times(1)).save(newPerson);
    }

    @Test
    void addPerson_shouldThrowConflictException_whenPersonExists() {
        Person existingPerson = new PersonTestBuilder().build();
        when(jsonPersonRepository.findByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName()))
                .thenReturn(Optional.of(existingPerson));

        assertThrows(ConflictException.class, () -> personService.addPerson(existingPerson));

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName());
        verify(jsonPersonRepository, never()).save(any());
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson_whenExists() {
        Person existingPerson = new PersonTestBuilder().build();
        String newAddress = "New Addr";
        Person updatedPerson = new PersonTestBuilder().withAddress(newAddress).build();

        when(jsonPersonRepository.findByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName()))
                .thenReturn(Optional.of(existingPerson));
        when(jsonPersonRepository.save(any(Person.class))).thenReturn(updatedPerson);

        Person result = personService.updatePerson(updatedPerson);

        assertNotNull(result);
        assertEquals(existingPerson.getFirstName(), result.getFirstName());
        assertEquals(newAddress, result.getAddress());

        verify(jsonPersonRepository, times(1))
                .findByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName());
        verify(jsonPersonRepository, times(1)).save(any(Person.class));
    }

    @Test
    void updatePerson_shouldThrowResourceNotFoundException_whenNotExists() {
        Person nonExistingPerson =
                new PersonTestBuilder().withFirstName("Nobody").withLastName("Here").build();

        when(jsonPersonRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> personService.updatePerson(nonExistingPerson));

        verify(jsonPersonRepository, times(1)).findByFirstNameAndLastName(anyString(), anyString());
        verify(jsonPersonRepository, never()).save(any());
    }

    @Test
    void deletePerson_shouldRemovePerson_whenExists() {
        Person existingPerson = new PersonTestBuilder().build();
        when(jsonPersonRepository.findByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName()))
                .thenReturn(Optional.of(existingPerson));
        doNothing()
                .when(jsonPersonRepository)
                .deleteByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName());

        personService.deletePerson(existingPerson.getFirstName(), existingPerson.getLastName());

        verify(jsonPersonRepository, times(1))
                .deleteByFirstNameAndLastName(
                        existingPerson.getFirstName(), existingPerson.getLastName());
    }

    @Test
    void deletePerson_shouldThrowResourceNotFoundException_whenNotExists() {
        when(jsonPersonRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> personService.deletePerson("Nobody", "Here"));

        verify(jsonPersonRepository, never()).deleteByFirstNameAndLastName(any(), any());
    }
}
