package com.coinsearch.repository;

import com.coinsearch.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    @Query("SELECT DISTINCT p FROM Person p " +
            "JOIN p.cryptocurrencies c " +
            "WHERE c.name = :cryptoName " +
            "ORDER BY p.name")
    List<Person> findAllPeopleWithCrypto(@Param("cryptoName") String genreName);

}
