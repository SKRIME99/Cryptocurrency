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

    private boolean isValidCryptoCurrency(String cryptoCurrency) {
        return allowedCryptocurrencies.contains(cryptoCurrency.toLowerCase());
    }

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

    public List<CryptoData> addList(List<String> cryptoCurrencies) {
        return cryptoCurrencies.stream()
                .filter(this::isValidCryptoCurrency)
                .map(cryptoCurrency -> {
                    int index = allowedCryptocurrencies.indexOf(cryptoCurrency);
                    String crypto = allowedCryptocurrencies.get(index);
                    return createCryptocurrency(crypto);
                })
                .toList();
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

    public void deleteCrypto(Long cryptoId) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );

        if (cryptoData.getPersons() != null && cryptoData.getPersons().size() != 0){
            throw new EntityNotFoundException("Can't delete crypto " + cryptoId + " because people are using it. Try deleting this crypto from a specified person.");
        }
        if (cryptoData.getChain() != null){
            throw new EntityNotFoundException("Can't delete crypto " + cryptoId + " because it is in a chain. Try deleting this crypto from a specified chain.");
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