package com.coinsearch.service;

import com.coinsearch.exception.PersonNotFoundException;
import com.coinsearch.model.Person;
import com.coinsearch.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class PersonServiceImpl implements PersonService{

    private PersonRepository personRepository;
    @Override
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person getPersonById(Long personId) {
        return personRepository.findById(Math.toIntExact(personId))
                .orElseThrow(()->
                        new PersonNotFoundException("Person does not exist with given id: " + personId));
    }

    @Override
    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    @Override
    public Person updatePerson(Long personId, Person updatedPerson) {
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new PersonNotFoundException("Person does not exist with given id: " + personId)
        );

        person.setName(updatedPerson.getName());
        person.setCryptocurrencies(updatedPerson.getCryptocurrencies());

        return personRepository.save(person);
    }

    @Override
    public void deletePerson(Long personId) {
        Person peron = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new PersonNotFoundException("Person does not exist with given id: " + personId)
        );
        personRepository.deleteById(Math.toIntExact(personId));
    }
}
