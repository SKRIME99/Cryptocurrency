package com.coinsearch.service;

import com.coinsearch.model.Person;

import java.util.List;

public interface PersonService {
    Person createPerson(Person person);
    Person getPersonById(Long personId);

    List<Person> getAllPeople();

    Person updatePerson(Long PersonId, Person updatedPerson);


}
