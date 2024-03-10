package com.coinsearch.repository;

import com.coinsearch.model.CryptoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CryptocurrencyRepository extends JpaRepository<CryptoData, Integer>{
    CryptoData findByName(@Param("name") String name);
}
