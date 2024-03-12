package com.coinsearch.service;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.CryptocurrencyData;
import com.coinsearch.repository.CryptocurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CoinCapService {
    private static final String COINCAP_API_URL = "https://api.coincap.io/v2/assets/%s";

    private final RestTemplate restTemplate;
    private final CryptocurrencyRepository cryptoRepository;
    private static final String errorMessage = "Crypto does not exist with given id: ";

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
                () -> new EntityNotFoundException(errorMessage + cryptoId)
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
                () -> new EntityNotFoundException(errorMessage + cryptoId)
        );

        cryptoData.setName(updatedCryptoData.getName());
        cryptoData.setChain(updatedCryptoData.getChain());
        cryptoData.setPersons(updatedCryptoData.getPersons());

        return cryptoRepository.save(cryptoData);
    }

    public void deleteCrypto(Long cryptoId) {
        CryptoData cryptoData = cryptoRepository.findById(Math.toIntExact(cryptoId)).orElseThrow(
                () -> new EntityNotFoundException(errorMessage + cryptoId)
        );
        if (cryptoData != null) {
            cryptoRepository.deleteById(Math.toIntExact(cryptoId));
        }
    }
}