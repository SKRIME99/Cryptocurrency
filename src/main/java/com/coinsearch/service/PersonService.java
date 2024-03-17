package com.coinsearch.service;

import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.CryptocurrencyRepository;
import com.coinsearch.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class PersonService {

    private PersonRepository personRepository;
    private CryptocurrencyRepository cryptoRepository;
    private static final  String ERROR_MESSAGE = "Person does not exist with given id: ";

    public Person createPerson(Person person) {
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


    public Person updatePerson(Long personId, Person updatedPerson) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + personId)
        );

        person.setName(updatedPerson.getName());
        person.setCryptocurrencies(updatedPerson.getCryptocurrencies());

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
    }
}
