package com.coinsearch.testController;

import com.coinsearch.controller.PersonController;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.service.CoinCapService;
import com.coinsearch.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @Mock
    private CoinCapService coinCapService;

    @InjectMocks
    private PersonController personController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        // Mock data
        Person person = new Person(1L, "test", new HashSet<>());
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
        Person mockPerson = new Person(1L, "test", new HashSet<>());
        when(personService.getPersonById(personId)).thenReturn(mockPerson);

        // Test
        Person result = personController.getPersonById(personId);

        // Verify
        assertEquals(mockPerson, result);
        verify(personService, times(1)).getPersonById(personId);
    }

    @Test
    void testGetAllPeople() {
        // Mock data
        List<Person> mockPeople = Arrays.asList(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()));
        when(personService.getAllPeople()).thenReturn(mockPeople);

        // Test
        ResponseEntity<List<Person>> responseEntity = personController.getAllPeople();

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockPeople, responseEntity.getBody());
        verify(personService, times(1)).getAllPeople();
    }

    @Test
    void testGetAllPeopleWithCrypto() {
        // Mock data
        String crypto = "bitcoin";
        List<Person> mockPeople = Arrays.asList(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()));
        when(personService.getAllPeopleWithCrypto(crypto)).thenReturn(mockPeople);

        // Test
        ResponseEntity<List<Person>> responseEntity = personController.getAllPeopleWithCrypto(crypto);

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockPeople, responseEntity.getBody());
        verify(personService, times(1)).getAllPeopleWithCrypto(crypto);
    }

    @Test
    void testUpdatePerson() {
        // Mock data
        Long personId = 1L;
        Person updatedPerson = new Person(1L, "test", new HashSet<>());
        when(personService.updatePerson(personId, updatedPerson)).thenReturn(updatedPerson);

        // Test
        Person result = personController.updatePerson(personId, updatedPerson);

        // Verify
        assertEquals(updatedPerson, result);
        verify(personService, times(1)).updatePerson(personId, updatedPerson);
    }

    @Test
    void testAddCryptoToPerson() {
        // Mock data
        Long personId = 1L;
        Long cryptoId = 1L;
        Person person = new Person(1L, "test", new HashSet<>());
        CryptoData cryptoData = new CryptoData();
        when(personService.getPersonById(personId)).thenReturn(person);
        when(coinCapService.getCryptoDataById(cryptoId)).thenReturn(cryptoData);
        when(personService.updatePerson(personId, person)).thenReturn(person);

        // Test
        Person result = personController.addCryptoToPerson(personId, cryptoId);

        // Verify
        assertEquals(person, result);
        verify(personService, times(1)).getPersonById(personId);
        verify(coinCapService, times(1)).getCryptoDataById(cryptoId);
        verify(personService, times(1)).updatePerson(personId, person);
    }

    @Test
    void testDeletePerson() {
        // Mock data
        Long personId = 1L;

        // Test
        ResponseEntity<String> responseEntity = personController.deletePerson(personId);

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(personService, times(1)).deletePerson(personId);
    }
}
