package com.coinsearch.service;

import com.coinsearch.exception.PersonNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.CryptocurrencyData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.ChainRepository;
import com.coinsearch.repository.CryptocurrencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@AllArgsConstructor
@Service
public class ChainService {
    private final ChainRepository chainRepository;

    public Chain createChain(Chain chain) {
        return chainRepository.save(chain);
    }

    public Chain getChainById(Long chainId) {
        return chainRepository.findById(Math.toIntExact(chainId))
                .orElseThrow(()->
                        new PersonNotFoundException("Person does not exist with given id: " + chainId));
    }


    public List<Chain> getAllChains() {
        return chainRepository.findAll();
    }


    public Chain updateChain(Long chainId, Chain updatedChain) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new PersonNotFoundException("Person does not exist with given id: " + chainId)
        );

        chain.setName(updatedChain.getName());
        chain.setCryptocurrencies(updatedChain.getCryptocurrencies());

        return chainRepository.save(chain);
    }


    public void deleteChain(Long chainId) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new PersonNotFoundException("Chain does not exist with given id: " + chainId)
        );
        chainRepository.deleteById(Math.toIntExact(chainId));
    }
}
