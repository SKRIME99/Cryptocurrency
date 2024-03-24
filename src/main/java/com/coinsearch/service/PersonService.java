package com.coinsearch.service;

import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class PersonService {
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    private PersonRepository personRepository;
    private final Cache cache;
    private static final  String ERROR_MESSAGE = "Person does not exist with given id: ";
    private static final String CACHE_LOG = "Data loaded from cache using key: ";

    public Person createPerson(Person person) {
        cache.clearCache();
        return personRepository.save(person);
    }


    public Person getPersonById(Long personId) {
        return personRepository.findById(Math.toIntExact(personId))
                .orElseThrow(()->
                        new EntityNotFoundException(ERROR_MESSAGE + personId));
    }


    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    @SuppressWarnings("unchecked")
    public List<Person> getAllPeopleWithCrypto(String cryptoName){
        String cacheKey = "crypto-" + cryptoName;
        List<Person> peopleFromCache = (List<Person>) cache.getFromCache(cacheKey);
        if (peopleFromCache != null){
            log.info(CACHE_LOG + cacheKey);
            return peopleFromCache;
        }
        List<Person> peopleFromRepo = personRepository.findAllPeopleWithCrypto(cryptoName);
        cache.addToCache(cacheKey, peopleFromRepo);
        return peopleFromRepo;
    }

    public Person updatePerson(Long personId, Person updatedPerson) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + personId)
        );

        person.setName(updatedPerson.getName());
        person.setCryptocurrencies(updatedPerson.getCryptocurrencies());
        cache.clearCache();
        return personRepository.save(person);
    }


    @Transactional
    public void deletePerson(Long personId) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException("Person with ID " + personId + " not found")
        );

        // Remove the person from the cryptocurrencies they're associated with
        for (CryptoData cryptoData : person.getCryptocurrencies()) {
            cryptoData.getPersons().remove(person);
        }

        // Update the changes in the database
        person.getCryptocurrencies().clear();
        personRepository.deleteById(Math.toIntExact(personId));
        cache.clearCache();
    }
}
