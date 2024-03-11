package com.coinsearch.repository;

import com.coinsearch.model.Chain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChainRepository extends JpaRepository<Chain, Integer> {
}
