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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoinCapService {
    private static final String COINCAP_API_URL = "https://api.coincap.io/v2/assets/%s";
    private static final Logger LOG = LoggerFactory.getLogger(CoinCapService.class);
    private final RestTemplate restTemplate;
    private final CryptocurrencyRepository cryptoRepository;
    private final PersonRepository personRepository;
    private final ChainRepository chainRepository;
    private final Cache cache;
    private static final String ERROR_MESSAGE = "Crypto does not exist with given id: ";
    private static final String CACHE_KEY = "crypto-";
    private static final String CACHE_HIT = "Cash HIT using key: %s";
    private static final String CACHE_MISS = "Cash MISS using key: %s";

    private final List<String> allowedCryptocurrencies = Arrays.asList("bitcoin", "ethtreum", "solana", "tether", "xrp", "cardano", "dogecoin", "polkadot", "tron", "litecoin");

    public boolean isValidCryptoCurrency(String cryptoCurrency) {
        return allowedCryptocurrencies.contains(cryptoCurrency.toLowerCase());
    }

    public CoinCapService(RestTemplate restTemplate, CryptocurrencyRepository cryptoRepository, PersonRepository personRepository, ChainRepository chainRepository, Cache cache) {
        this.restTemplate = restTemplate;
        this.cryptoRepository = cryptoRepository;
        this.personRepository = personRepository;
        this.chainRepository = chainRepository;
        this.cache = cache;
    }

    public boolean createCryptocurrency(String cryptocurrency){
        String apiUrl = String.format(COINCAP_API_URL, cryptocurrency);
        CryptocurrencyData cryptocurrencyData = restTemplate.getForObject(apiUrl, CryptocurrencyData.class);
        if (cryptocurrencyData == null){
            return false;
        }
        CryptoData cryptoData = cryptocurrencyData.getData();
        cryptoRepository.save(cryptoData);
        return true;
    }

    public boolean addList(List<String> cryptoCurrencies) {
        cryptoCurrencies.stream()
                .filter(this::isValidCryptoCurrency)
                .map(cryptoCurrency -> {
                    int index = allowedCryptocurrencies.indexOf(cryptoCurrency);
                    String crypto = allowedCryptocurrencies.get(index);
                    return createCryptocurrency(crypto);
                })
                .toList();
        return true;
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
            String logstash = String.format(CACHE_HIT,cacheKey);
            LOG.info(logstash);
            return cachedCrypto;
        }
        String logstash = String.format(CACHE_MISS, cacheKey);
        LOG.info(logstash);
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

    public boolean deleteCrypto(Long cryptoId) {
        Optional<CryptoData> cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId));
        if (!cryptoData.isPresent()){
            return false;
        }

        if (cryptoData.get().getPersons() != null && cryptoData.get().getPersons().size() != 0){
            return false;
        }
        if (cryptoData.get().getChain() != null){
            return false;
        }
        else if (cryptoData != null) {
            String cacheKey = CACHE_KEY + cryptoData.get().getName();
            cache.removeFromCache(cacheKey);
            cryptoRepository.deleteById(Math.toIntExact(cryptoId));
        }
        return true;
    }

    public boolean deleteCryptoFromPerson(Long cryptoId, Long personId) {
        Optional<CryptoData> cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId));
        if (!cryptoData.isPresent()){
            return false;
        }
        Optional<Person> person = personRepository.findById(Math.toIntExact(personId));
        if (!person.isPresent()){
            return false;
        }
        person.ifPresent(p -> {
            p.getCryptocurrencies().remove(cryptoData.orElseThrow());
            cryptoData.ifPresent(cd -> cd.getPersons().remove(p));
            personRepository.save(p);
            cryptoData.ifPresent(cryptoRepository::save);
        });

        return true;
    }

    public boolean deleteCryptoFromChain(Long cryptoId, Long chainId) {
        Optional<CryptoData> cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId));
        if (!cryptoData.isPresent()){
            return false;
        }
        Optional<Chain> chain = chainRepository.findById(Math.toIntExact(chainId));
        if (!chain.isPresent()){
            return false;
        }

        // Assuming we have Optional<Chain> chain and Optional<CryptoData> cryptoData
        chain.ifPresent(c -> {
            c.getCryptocurrencies().remove(cryptoData.orElseThrow());
            cryptoData.ifPresent(cd -> cd.setChain(null));
            chainRepository.save(c);
        });
        return true;
    }
}