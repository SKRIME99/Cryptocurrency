package com.coinsearch.service;

import com.coinsearch.exception.PersonNotFoundException;
import com.coinsearch.model.Person;
import com.coinsearch.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService{

    private PersonRepository personRepository;
    @Override
    public Person createPerson(Person person) {
        Person savedPerson = personRepository.save(person);
        return savedPerson;
    }

    @Override
    public Person getPersonById(Long personId) {
        Person person = personRepository.findById(Math.toIntExact(personId))
                .orElseThrow(()->
                        new PersonNotFoundException("Person does not exist with given id: " + personId));
        return person;
    }

    @Override
    public List<Person> getAllPeople() {
        List<Person> people = personRepository.findAll();
        return people;
    }

    @Override
    public Person updatePerson(Long personId, Person updatedPerson) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new PersonNotFoundException("Person does not exist with given id: " + personId)
        );

        person.setName(updatedPerson.getName());
        person.setCryptocurrencies(updatedPerson.getCryptocurrencies());

        Person updatedPersonObj = personRepository.save(person);

        return updatedPersonObj;
    }
}
