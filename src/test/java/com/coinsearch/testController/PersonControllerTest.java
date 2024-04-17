package com.coinsearch.testController;

import com.coinsearch.controller.PersonController;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.service.CoinCapService;
import com.coinsearch.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @Mock
    private CoinCapService coinCapService;

    @InjectMocks
    private PersonController personController;


    @Test
    void testCreate() {
        // Mock data
        Person person = new Person();
        person.setId(1L);
        when(personService.createPerson(person)).thenReturn(person);

        // Test
        ResponseEntity<Person> responseEntity = personController.create(person);

        // Verify
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(person, responseEntity.getBody());
        verify(personService, times(1)).createPerson(person);
    }

    @Test
    void testGetPersonById() {
        // Mock data
        Long personId = 1L;
        Person mockPerson = new Person();
        mockPerson.setId(personId);
        when(personService.getPersonById(personId)).thenReturn(mockPerson);

        // Test
        Person result = personController.getPersonById(personId);

        // Verify
        assertEquals(mockPerson, result);
        verify(personService, times(1)).getPersonById(personId);
    }

    @Test
    void testUpdatePerson() {
        // Mock data
        Long personId = 1L;

        Person updatedPerson = new Person();
        updatedPerson.setId(personId);
        when(personService.updatePerson(personId, updatedPerson)).thenReturn(updatedPerson);

        // Test
        Person result = personController.updatePerson(personId, updatedPerson);

        // Verify
        assertEquals(updatedPerson, result);
        verify(personService, times(1)).updatePerson(personId, updatedPerson);
    }

    @Test
    void testDeletePerson() {
        // Mock data
        Long personId = 1L;

        // Test
        ResponseEntity<String> responseEntity = personController.deletePerson(personId);

        // Verify
        verify(personService, times(1)).deletePerson(personId);
    }
}
