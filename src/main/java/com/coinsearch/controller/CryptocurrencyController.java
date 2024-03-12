package com.coinsearch.controller;

import com.coinsearch.model.CryptoData;
import com.coinsearch.service.CoinCapService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/cryptocurrency")
public class CryptocurrencyController {

    private final CoinCapService coinCapService;
    private final List<String> allowedCryptocurrencies = Arrays.asList("bitcoin", "ethtreum", "solana", "tether", "xrp", "cardano", "dogecoin", "polkadot", "tron", "litecoin");

    private boolean isValidCryptoCurrency(String cryptoCurrency) {
        return allowedCryptocurrencies.contains(cryptoCurrency.toLowerCase());
    }
    @GetMapping
    public ResponseEntity<List<CryptoData>> getAllCryptoData(){
        List<CryptoData> data = coinCapService.getAllCryptoData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{name}")
    public CryptoData getCryptoDataById(@PathVariable("name") String name){
        return coinCapService.getCryptoDataByName(name);
    }

    @PostMapping
    public ResponseEntity<CryptoData> create(@RequestBody String cryptoCurrency){
        if (!isValidCryptoCurrency(cryptoCurrency)) {
            throw new IllegalArgumentException("Invalid cryptocurrency: " + cryptoCurrency);
        }
        int index = allowedCryptocurrencies.indexOf(cryptoCurrency);
        String crypto = allowedCryptocurrencies.get(index);
        CryptoData cryptoData = coinCapService.createCryptocurrency(crypto);
        return new ResponseEntity<>(cryptoData, HttpStatus.CREATED);
    }

    @DeleteMapping("/{crypto_id}")
    public ResponseEntity<String> deleteCrypto(@PathVariable("crypto_id") Long cryptoId) {
        coinCapService.deleteCrypto(cryptoId);
        return ResponseEntity.ok("Person deleted successfully");
    }

}