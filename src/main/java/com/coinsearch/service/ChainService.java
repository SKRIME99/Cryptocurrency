package com.coinsearch.service;

import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.repository.ChainRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ChainService {
    private final ChainRepository chainRepository;
    private static final String ERROR_MESSAGE = "Chain does not exist with given id: ";

    public Chain createChain(Chain chain) {
        return chainRepository.save(chain);
    }

    public Chain getChainById(Long chainId) {
        return chainRepository.findById(Math.toIntExact(chainId))
                .orElseThrow(()->
                        new EntityNotFoundException(ERROR_MESSAGE + chainId));
    }


    public List<Chain> getAllChains() {
        return chainRepository.findAll();
    }


    public Chain updateChain(Long chainId, Chain updatedChain) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + chainId)
        );

        chain.setName(updatedChain.getName());
        chain.setCryptocurrencies(updatedChain.getCryptocurrencies());

        return chainRepository.save(chain);
    }


    public void deleteChain(Long chainId) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + chainId)
        );

        if (chain != null){
            for (CryptoData cryptoData : chain.getCryptocurrencies()) {
                cryptoData.setChain(null);
            }

            // Update the changes in the database
            chain.getCryptocurrencies().clear();
            chainRepository.deleteById(Math.toIntExact(chainId));
        }
    }
}
