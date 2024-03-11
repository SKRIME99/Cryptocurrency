package com.coinsearch.repository;

import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChainRepository extends JpaRepository<Chain, Integer> {
}
