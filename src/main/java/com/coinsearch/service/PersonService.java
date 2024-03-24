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
    private static final String CACHE_HIT = "Cash HIT using key: %s";
    private static final String CACHE_MISS = "Cash MISS using key: %s";
    private static final String CACHE_KEY = "person-";

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }


    public Person getPersonById(Long personId) {
        String cacheKey = CACHE_KEY + personId;
        Person cachedPerson = (Person) cache.getFromCache(cacheKey);
        if (cachedPerson != null){
            String logstash = String.format(CACHE_HIT, cacheKey);
            log.info(logstash);
            return cachedPerson;
        }
        String logstash = String.format(CACHE_MISS, cacheKey);
        log.info(logstash);
        Person personFromRepo = personRepository.findById(Math.toIntExact(personId))
                .orElseThrow(()->
                        new EntityNotFoundException(ERROR_MESSAGE + personId));
        cache.addToCache(cacheKey, personFromRepo);
        return personFromRepo;
    }

    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    public List<Person> getAllPeopleWithCrypto(String cryptoName){
        return personRepository.findAllPeopleWithCrypto(cryptoName);
    }

    public Person updatePerson(Long personId, Person updatedPerson) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + personId)
        );
        String cacheKey = CACHE_KEY + person.getId();
        cache.removeFromCache(cacheKey);
        person.setName(updatedPerson.getName());
        person.setCryptocurrencies(updatedPerson.getCryptocurrencies());
        cache.addToCache(cacheKey,person);
        return personRepository.save(person);
    }


    @Transactional
    public void deletePerson(Long personId) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException("Person with ID " + personId + " not found")
        );


        for (CryptoData cryptoData : person.getCryptocurrencies()) {
            cryptoData.getPersons().remove(person);
        }

        String cacheKey = CACHE_KEY + personId;
        cache.removeFromCache(cacheKey);
        person.getCryptocurrencies().clear();
        personRepository.deleteById(Math.toIntExact(personId));
    }
}
