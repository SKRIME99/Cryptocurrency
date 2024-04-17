package com.coinsearch.controller;

import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.service.CoinCapService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.coinsearch.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/person")
public class PersonController {
    private final PersonService personService;
    private final CoinCapService coinCapService;

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person){
        Person savedPerson = personService.createPerson(person);
        return new ResponseEntity<>(savedPerson, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Person getPersonById(@PathVariable("id") Long personId){
        return personService.getPersonById(personId);
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPeople(){
        List<Person> people = personService.getAllPeople();
        return ResponseEntity.ok(people);
    }

    @GetMapping("/with/{cryptoName}")
    public ResponseEntity<List<Person>> getAllPeopleWithCrypto(@PathVariable("cryptoName") String crypto) {
        List<Person> people = personService.getAllPeopleWithCrypto(crypto);
        return ResponseEntity.ok(people);
    }

    @PutMapping("{id}")
    public Person updatePerson(@PathVariable("id") Long personId, @RequestBody Person updatedPerson){
        return personService.updatePerson(personId, updatedPerson);
    }

    @PutMapping("/{person_id}/crypto/{crypto_id}")
    public Person addCryptoToPerson(@PathVariable("person_id") Long personId, @PathVariable("crypto_id") Long cryptoId){
        Person person = personService.getPersonById(personId);
        CryptoData cryptoData = coinCapService.getCryptoDataById(cryptoId);
        person.addCrypto(cryptoData);
        return personService.updatePerson(personId, person);
    }

    @DeleteMapping("/{person_id}")
    public ResponseEntity<String> deletePerson(@PathVariable("person_id") Long personId) {
        personService.deletePerson(personId);
        return ResponseEntity.ok("Person deleted successfully");
    }
}
