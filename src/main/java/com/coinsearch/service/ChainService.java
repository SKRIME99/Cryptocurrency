package com.coinsearch.service;

import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.repository.ChainRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ChainService {
    private final ChainRepository chainRepository;
    private static final String errorMessage = "Chain does not exist with given id: ";

    public Chain createChain(Chain chain) {
        return chainRepository.save(chain);
    }

    public Chain getChainById(Long chainId) {
        return chainRepository.findById(Math.toIntExact(chainId))
                .orElseThrow(()->
                        new EntityNotFoundException(errorMessage + chainId));
    }


    public List<Chain> getAllChains() {
        return chainRepository.findAll();
    }


    public Chain updateChain(Long chainId, Chain updatedChain) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new EntityNotFoundException(errorMessage + chainId)
        );

        chain.setName(updatedChain.getName());
        chain.setCryptocurrencies(updatedChain.getCryptocurrencies());

        return chainRepository.save(chain);
    }


    public void deleteChain(Long chainId) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new EntityNotFoundException(errorMessage + chainId)
        );
        if (chain != null) {
            chainRepository.deleteById(Math.toIntExact(chainId));
        }
    }
}
