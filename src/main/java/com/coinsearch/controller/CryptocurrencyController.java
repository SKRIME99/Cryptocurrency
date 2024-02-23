package com.coinsearch.controller;

import com.coinsearch.service.CoinCapService;
import com.coinsearch.model.CryptocurrencyData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CryptocurrencyController {
    private final CoinCapService coinCapService;

    public CryptocurrencyController(CoinCapService coinCapService) {
        this.coinCapService = coinCapService;
    }

    @GetMapping("/cryptocurrency/{name}")
    public CryptocurrencyData.Data getCryptocurrency(@PathVariable String name) {
        return coinCapService.getCryptocurrencyData(name).getData();
    }
}