package com.coinsearch.service;

import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.repository.ChainRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ChainService {
    private final ChainRepository chainRepository;
    private final Cache cache;
    private static final Logger LOG = LoggerFactory.getLogger(ChainService.class);
    private static final String ERROR_MESSAGE = "Chain does not exist with given id: ";
    private static final String CACHE_KEY = "chain-";
    private static final String CACHE_HIT = "Cash HIT using key: %s";
    private static final String CACHE_MISS = "Cash MISS using key: %s";

    public Chain createChain(Chain chain) {
        return chainRepository.save(chain);
    }

    public Chain getChainById(Long chainId) {
        String cacheKey = CACHE_KEY + chainId;
        Chain cachedChain = (Chain) cache.getFromCache(cacheKey);
        if (cachedChain != null){
            String logstash = String.format(CACHE_HIT, cacheKey);
            LOG.info(logstash);
            return cachedChain;
        }
        String logstash = String.format(CACHE_MISS, cacheKey);
        LOG.info(logstash);
        Chain chainFromRepo = chainRepository.findById(Math.toIntExact(chainId))
                .orElseThrow(()-> new EntityNotFoundException(ERROR_MESSAGE + chainId));
        cache.addToCache(cacheKey, chainFromRepo);
        return chainFromRepo;
    }
    
    public List<Chain> getAllChains() {
        return chainRepository.findAll();
    }
    
    public Chain updateChain(Long chainId, Chain updatedChain) {
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
            () -> new EntityNotFoundException(ERROR_MESSAGE + chainId)
            );
        String cacheKey = CACHE_KEY + chain.getId();
        cache.removeFromCache(cacheKey);
        chain.setName(updatedChain.getName());
        chain.setCryptocurrencies(updatedChain.getCryptocurrencies());

        cache.addToCache(cacheKey, chain);
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

            String cacheKey = CACHE_KEY + chain.getId();
            cache.removeFromCache(cacheKey);
            chain.getCryptocurrencies().clear();
            chainRepository.deleteById(Math.toIntExact(chainId));
        }
    }
}
