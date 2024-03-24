package com.coinsearch.service;
import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.model.CryptocurrencyData;
import com.coinsearch.repository.ChainRepository;
import com.coinsearch.repository.CryptocurrencyRepository;
import com.coinsearch.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CoinCapService {
    private static final String COINCAP_API_URL = "https://api.coincap.io/v2/assets/%s";
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);
    private final RestTemplate restTemplate;
    private final CryptocurrencyRepository cryptoRepository;
    private final PersonRepository personRepository;
    private final ChainRepository chainRepository;
    private final Cache cache;
    private static final String ERROR_MESSAGE = "Crypto does not exist with given id: ";
    private static final String CACHE_KEY = "crypto-";
    private static final String CACHE_LOG = "Data loaded from cache using key: ";

    public CoinCapService(RestTemplate restTemplate, CryptocurrencyRepository cryptoRepository, PersonRepository personRepository, ChainRepository chainRepository, Cache cache) {
        this.restTemplate = restTemplate;
        this.cryptoRepository = cryptoRepository;
        this.personRepository = personRepository;
        this.chainRepository = chainRepository;
        this.cache = cache;
    }

    public CryptoData createCryptocurrency(String cryptocurrency){
        String apiUrl = String.format(COINCAP_API_URL, cryptocurrency);
        CryptocurrencyData cryptocurrencyData = restTemplate.getForObject(apiUrl, CryptocurrencyData.class);
        assert cryptocurrencyData != null;
        CryptoData cryptoData = cryptocurrencyData.getData();
        return cryptoRepository.save(cryptoData);
    }

    public CryptoData getCryptoDataById(Long cryptoId) {
        return cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );
    }

    public CryptoData getCryptoDataByName(String name) {
        String cacheKey = CACHE_KEY + name;
        CryptoData cachedCrypto = (CryptoData) cache.getFromCache(cacheKey);
        if (cachedCrypto != null){
            log.info("cache hit: " + CACHE_LOG +  cacheKey);
            return cachedCrypto;
        }
        log.info("cache miss: " + cacheKey);
        CryptoData cryptoData = cryptoRepository.findByName(name);
        cache.addToCache(cacheKey, cryptoData);
        return cryptoData;
    }

    public List<CryptoData> getAllCryptoData() {
        return cryptoRepository.findAll();
    }

    public CryptoData updateCryptoData(Long cryptoId, CryptoData updatedCryptoData) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );
        String cacheKey = CACHE_KEY + cryptoData.getName();
        cache.removeFromCache(cacheKey);

        cryptoData.setName(updatedCryptoData.getName());
        cryptoData.setChain(updatedCryptoData.getChain());
        cryptoData.setPersons(updatedCryptoData.getPersons());

        cache.addToCache(cacheKey, cryptoData);
        return cryptoRepository.save(cryptoData);
    }

    public void deleteCrypto(Long cryptoId) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );

        if (cryptoData.getPersons().size() != 0){
            throw new RuntimeException("Can't delete crypto " + cryptoId + " because people are using it. Try deleting this crypto from a specified person.");
        }
        if (cryptoData.getChain() != null){
            throw new RuntimeException("Can't delete crypto " + cryptoId + " because it is in a chain. Try deleting this crypto from a specified chain.");
        }
        else if (cryptoData != null) {
            String cacheKey = CACHE_KEY + cryptoData.getName();
            cache.removeFromCache(cacheKey);
            cryptoRepository.deleteById(Math.toIntExact(cryptoId));
        }
    }

    public void deleteCryptoFromPerson(Long cryptoId, Long personId) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );
        Person person = personRepository.findById(Math.toIntExact(personId)).orElseThrow(
                () -> new EntityNotFoundException("Person does not exist with given id: " + personId)
        );

        person.getCryptocurrencies().remove(cryptoData);
        cryptoData.getPersons().remove(person);
        personRepository.save(person);
        cryptoRepository.save(cryptoData);
    }

    public void deleteCryptoFromChain(Long cryptoId, Long chainId) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );
        Chain chain = chainRepository.findById(Math.toIntExact(chainId)).orElseThrow(
                () -> new EntityNotFoundException("Chain does not exist with given id: " + chainId)
        );

        chain.getCryptocurrencies().remove(cryptoData);
        cryptoData.setChain(null);
        chainRepository.save(chain);
    }
}