package com.coinsearch.controller;

import com.coinsearch.model.CryptoData;
import com.coinsearch.service.CoinCapService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/cryptocurrency")
public class CryptocurrencyController {

    private final CoinCapService coinCapService;

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
        CryptoData cryptoData = coinCapService.createCryptocurrency(cryptoCurrency);
        return new ResponseEntity<>(cryptoData, HttpStatus.CREATED);
    }
}