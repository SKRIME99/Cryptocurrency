package com.coinsearch.service;

import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Person;
import com.coinsearch.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class PersonService {

    private PersonRepository personRepository;

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }


    public Person getPersonById(Long personId) {
        return personRepository.findById(Math.toIntExact(personId))
                .orElseThrow(()->
                        new EntityNotFoundException("Person does not exist with given id: " + personId));
    }


    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }


    public Person updatePerson(Long personId, Person updatedPerson) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException("Person does not exist with given id: " + personId)
        );

        person.setName(updatedPerson.getName());
        person.setCryptocurrencies(updatedPerson.getCryptocurrencies());

        return personRepository.save(person);
    }


    public void deletePerson(Long personId) {
        personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException("Person does not exist with given id: " + personId)
        );
        personRepository.deleteById(Math.toIntExact(personId));
    }
}
