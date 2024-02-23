package com.coinsearch.service;
import com.coinsearch.model.CryptocurrencyData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoinCapService {
    private static final String COINCAP_API_URL = "https://api.coincap.io/v2/assets/%s";


    private final RestTemplate restTemplate;

    public CoinCapService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public CryptocurrencyData getCryptocurrencyData(String cryptocurrency) {
        String apiUrl = String.format(COINCAP_API_URL, cryptocurrency);

        return restTemplate.getForObject(apiUrl, CryptocurrencyData.class);
    }
}