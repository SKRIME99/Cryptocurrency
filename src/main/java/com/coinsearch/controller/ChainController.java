package com.coinsearch.controller;

import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Chain;
import com.coinsearch.service.ChainService;
import com.coinsearch.service.CoinCapService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/chain")
public class ChainController {
    private final ChainService chainService;
    private final CoinCapService coinCapService;

    @PostMapping
    public ResponseEntity<Chain> create(@RequestBody Chain chain){
        Chain savedChain = chainService.createChain(chain);
        return new ResponseEntity<>(savedChain, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Chain getChainById(@PathVariable("id") Long chainId){
        return chainService.getChainById(chainId);
    }

    @GetMapping
    public ResponseEntity<List<Chain>> getAllChains(){
        List<Chain> chains = chainService.getAllChains();
        return ResponseEntity.ok(chains);
    }

    @PutMapping("{id}")
    public Chain updateChain(@PathVariable("id") Long chainId, @RequestBody Chain updatedChain){
        return chainService.updateChain(chainId, updatedChain);
    }

    @PutMapping("/{chain_id}/crypto/{crypto_id}")
    public Chain addCryptoToChain(@PathVariable("chain_id") Long chainId, @PathVariable("crypto_id") Long cryptoId){
        Chain chain = chainService.getChainById(chainId);
        CryptoData cryptoData = coinCapService.getCryptoDataById(cryptoId);
        chain.addCrypto(cryptoData);
        cryptoData.setChain(chain);
        coinCapService.updateCryptoData(cryptoId, cryptoData);
        return chainService.updateChain(chainId, chain);
    }

    @DeleteMapping("/{chain_id}")
    public ResponseEntity<String> deleteChain(@PathVariable("chain_id") Long chainId){
        chainService.deleteChain(chainId);
        return ResponseEntity.ok("Chain deleted successfully");
    }

}
