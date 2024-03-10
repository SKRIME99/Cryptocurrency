package com.coinsearch.service;
import com.coinsearch.exception.PersonNotFoundException;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.CryptocurrencyData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.CryptocurrencyRepository;
import com.coinsearch.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CoinCapService {
    private static final String COINCAP_API_URL = "https://api.coincap.io/v2/assets/%s";

    private final RestTemplate restTemplate;
    private final CryptocurrencyRepository cryptoRepository;

    public CoinCapService(RestTemplate restTemplate, CryptocurrencyRepository cryptoRepository) {
        this.restTemplate = restTemplate;
        this.cryptoRepository = cryptoRepository;
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
                () -> new PersonNotFoundException("Person does not exist with given id: " + cryptoId)
        );
    }

    public CryptoData getCryptoDataByName(String name) {
        return cryptoRepository.findByName(name);
    }

    public List<CryptoData> getAllCryptoData() {
        return cryptoRepository.findAll();
    }

}