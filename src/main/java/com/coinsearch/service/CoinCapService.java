package com.coinsearch.service;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.model.CryptocurrencyData;
import com.coinsearch.repository.ChainRepository;
import com.coinsearch.repository.CryptocurrencyRepository;
import com.coinsearch.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CoinCapService {
    private static final String COINCAP_API_URL = "https://api.coincap.io/v2/assets/%s";

    private final RestTemplate restTemplate;
    private final CryptocurrencyRepository cryptoRepository;
    private final PersonRepository personRepository;
    private final ChainRepository chainRepository;
    private static final String ERROR_MESSAGE = "Crypto does not exist with given id: ";

    public CoinCapService(RestTemplate restTemplate, CryptocurrencyRepository cryptoRepository, PersonRepository personRepository, ChainRepository chainRepository) {
        this.restTemplate = restTemplate;
        this.cryptoRepository = cryptoRepository;
        this.personRepository = personRepository;
        this.chainRepository = chainRepository;
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
        return cryptoRepository.findByName(name);
    }

    public List<CryptoData> getAllCryptoData() {
        return cryptoRepository.findAll();
    }

    public CryptoData updateCryptoData(Long cryptoId, CryptoData updatedCryptoData) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(ERROR_MESSAGE + cryptoId)
        );

        cryptoData.setName(updatedCryptoData.getName());
        cryptoData.setChain(updatedCryptoData.getChain());
        cryptoData.setPersons(updatedCryptoData.getPersons());

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
        personRepository.save(person);
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