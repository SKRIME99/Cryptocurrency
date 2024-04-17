package com.coinsearch.testService;

import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.PersonRepository;
import com.coinsearch.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private PersonService personService;


    @Test
    void testCreatePerson() {
        // Mock data
        Person person = new Person(1L, "test", new HashSet<>());
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
        Person expectedPerson = new Person(1L, "test", new HashSet<>());
        when(cache.getFromCache(anyString())).thenReturn(null);
        when(personRepository.findById(Math.toIntExact(personId))).thenReturn(Optional.of(expectedPerson));

        // Test
        Person result = personService.getPersonById(personId);

        // Verify
        assertEquals(expectedPerson, result);
        verify(cache, times(1)).addToCache(anyString(), eq(expectedPerson));
    }


    @Test
    void testUpdatePerson() {
        // Mock data
        Long personId = 1L;
        Person updatedPerson = new Person(1L, "test", new HashSet<>());
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
        Person person = new Person(1L, "test", new HashSet<>());
        Set<Person> set = new HashSet<>(Set.of(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()),new Person(3L, "test", new HashSet<>())));
        CryptoData cryptoData = new CryptoData(1L, set, new Chain(1L, "test", new HashSet<>()));
        person.setCryptocurrencies(new HashSet<>(List.of(cryptoData)));
        when(personRepository.findById(Math.toIntExact(personId))).thenReturn(Optional.of(person));

        // Test
        assertDoesNotThrow(() -> personService.deletePerson(personId));

        // Verify
        verify(personRepository, times(1)).deleteById(Math.toIntExact(personId));
        assertTrue(person.getCryptocurrencies().isEmpty());
        verify(cache, times(1)).removeFromCache(anyString());
    }

}