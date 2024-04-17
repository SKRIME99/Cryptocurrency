package com.coinsearch.testService;

import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.PersonRepository;
import com.coinsearch.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePerson() {
        // Mock data
        Person person = new Person();
        when(personRepository.save(person)).thenReturn(person);

        // Test
        Person result = personService.createPerson(person);

        // Verify
        assertEquals(person, result);
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testGetPersonById() {
        // Mock data
        long personId = 1L;
        Person expectedPerson = new Person();
        when(cache.getFromCache(anyString())).thenReturn(null);
        when(personRepository.findById(Math.toIntExact(personId))).thenReturn(Optional.of(expectedPerson));

        // Test
        Person result = personService.getPersonById(personId);

        // Verify
        assertEquals(expectedPerson, result);
        verify(cache, times(1)).addToCache(anyString(), eq(expectedPerson));
    }

    @Test
    void testGetAllPeople() {
        // Mock data
        List<Person> expectedPeople = Arrays.asList(new Person(), new Person());
        when(personRepository.findAll()).thenReturn(expectedPeople);

        // Test
        List<Person> result = personService.getAllPeople();

        // Verify
        assertEquals(expectedPeople, result);
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPeopleWithCrypto() {
        // Mock data
        String cryptoName = "bitcoin";
        List<Person> expectedPeople = Arrays.asList(new Person(), new Person());
        when(personRepository.findAllPeopleWithCrypto(cryptoName)).thenReturn(expectedPeople);

        // Test
        List<Person> result = personService.getAllPeopleWithCrypto(cryptoName);

        // Verify
        assertEquals(expectedPeople, result);
        verify(personRepository, times(1)).findAllPeopleWithCrypto(cryptoName);
    }

    @Test
    void testUpdatePerson() {
        // Mock data
        Long personId = 1L;
        Person updatedPerson = new Person();
        updatedPerson.setId(personId);
        when(personRepository.findById(Math.toIntExact(personId))).thenReturn(Optional.of(updatedPerson));
        when(personRepository.save(updatedPerson)).thenReturn(updatedPerson);

        // Test
        Person result = personService.updatePerson(personId, updatedPerson);

        // Verify
        assertEquals(updatedPerson, result);
        verify(personRepository, times(1)).save(updatedPerson);
        verify(cache, times(1)).removeFromCache(anyString());
        verify(cache, times(1)).addToCache(anyString(), eq(updatedPerson));
    }

    @Test
    void testDeletePerson() {
        // Mock data
        long personId = 1L;
        Person person = new Person();
        CryptoData cryptoData = new CryptoData();
        person.setCryptocurrencies(new HashSet<>(List.of(cryptoData)));
        when(personRepository.findById(Math.toIntExact(personId))).thenReturn(Optional.of(person));

        // Test
        assertDoesNotThrow(() -> personService.deletePerson(personId));

        // Verify
        verify(personRepository, times(1)).deleteById(Math.toIntExact(personId));
        assertTrue(person.getCryptocurrencies().isEmpty());
        verify(cache, times(1)).removeFromCache(anyString());
    }

    @Test
    void testDeletePerson_NotFound() {
        // Mock data
        long personId = 1L;
        when(personRepository.findById(Math.toIntExact(personId))).thenReturn(Optional.empty());

        // Test and Verify
        assertThrows(EntityNotFoundException.class, () -> personService.deletePerson(personId));
    }
}