package com.coinsearch.repository;

import com.coinsearch.model.CryptoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface CryptocurrencyRepository extends JpaRepository<CryptoData, Integer>{
    CryptoData findByName(@Param("name") String name);
}
