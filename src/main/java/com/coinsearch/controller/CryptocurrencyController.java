package com.coinsearch.controller;

import com.coinsearch.exception.ErrorMessage;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.service.ChainService;
import com.coinsearch.service.CoinCapService;
import com.coinsearch.service.CounterService;
import com.coinsearch.service.PersonService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/api/v1/cryptocurrency")
public class CryptocurrencyController {


    private static final String SUCCESSSTATUS = "Method success";

    private static final String ERROR_METHOD = "errorMethod";

    private static final String SUCCESS_METHOD = "successMethod";

    private final CoinCapService coinCapService;
    private final PersonService personService;
    private final ChainService chainService;
    private final List<String> allowedCryptocurrencies = Arrays.asList("bitcoin", "ethtreum", "solana", "tether", "xrp", "cardano", "dogecoin", "polkadot", "tron", "litecoin");

    private static final Logger LOG = LoggerFactory.getLogger(CryptocurrencyController.class);

    private boolean isValidCryptoCurrency(String cryptoCurrency) {
        return allowedCryptocurrencies.contains(cryptoCurrency.toLowerCase());
    }

/*    @GetMapping
    public ResponseEntity<List<CryptoData>> getAllCryptoData(){
        CounterService.enhanceCounter();
        List<CryptoData> data = coinCapService.getAllCryptoData();
        return ResponseEntity.ok(data);
    }*/

    @GetMapping("/allInfo")
    public String getAllCryptoData(Model model) {
        CounterService.enhanceCounter();
        List<CryptoData> crypto = coinCapService.getAllCryptoData();
        model.addAttribute("cryptos", crypto);
        List<Person> people = personService.getAllPeople();
        model.addAttribute("people", people);
        List<Chain> chains = chainService.getAllChains();
        model.addAttribute("chains", chains);
        return "allInfo";
    }

    @GetMapping("/{name}")
    public CryptoData getCryptoDataById(@PathVariable("name") String name){
        return coinCapService.getCryptoDataByName(name);
    }

    @GetMapping("/create_cryptodata")
    public String showCreateCryptoDataForm(Model model) {
        return "saveCryptoData";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false, name = "cryptoCurrency") String cryptoCurrency) {
        if (!isValidCryptoCurrency(cryptoCurrency)) {
            return ERROR_METHOD;
        }
        int index = allowedCryptocurrencies.indexOf(cryptoCurrency);
        String crypto = allowedCryptocurrencies.get(index);
        if (coinCapService.createCryptocurrency(crypto)){
            return SUCCESS_METHOD;
        }
        return ERROR_METHOD;
    }

    /*@PostMapping
    public ResponseEntity<CryptoData> create(@RequestBody String cryptoCurrency){
        if (!isValidCryptoCurrency(cryptoCurrency)) {
            throw new IllegalArgumentException("Invalid cryptocurrency: " + cryptoCurrency);
        }
        int index = allowedCryptocurrencies.indexOf(cryptoCurrency);
        String crypto = allowedCryptocurrencies.get(index);
        CryptoData cryptoData = coinCapService.createCryptocurrency(crypto);
        return new ResponseEntity<>(cryptoData, HttpStatus.CREATED);
    }*/

    @PostMapping("/bulk")
    public void bulkOperation(@RequestBody List<String> cryptoCurrencies) {
        coinCapService.addList(cryptoCurrencies);
    }

    @DeleteMapping("/{crypto_id}")
    public void deleteCrypto(@RequestParam(name = "crypto_id") Long cryptoId) {
        coinCapService.deleteCrypto(cryptoId);
    }

    @GetMapping("/deleteCryptoHTML")
    public String getDeleteCryptoHTML(){
        return "deleteCrypto";
    }
    @PostMapping("/deleteCryptoHTML")
    public String deleteCryptoHTML(@RequestParam(required = false, name = "idCrypto") String crypto_id) {
        Long cryptoId = Long.parseLong(crypto_id);
        if (coinCapService.deleteCrypto(cryptoId)){
            return SUCCESS_METHOD;
        }
        return ERROR_METHOD;
    }
    @GetMapping("/deleteCryptoFromPersonHTML")
    public String deleteTest(){
        return "deleteCryptoFromPerson";
    }

    @GetMapping("/deleteCryptoFromChainHTML")
    public String deleteTest2(){
        return "deleteCryptoFromChain";
    }
    @DeleteMapping("/{crypto_id}/person/{person_id}")
    public ResponseEntity<String> deleteCryptoFromPerson(@PathVariable("crypto_id") Long cryptoId, @PathVariable("person_id") Long personId) {
        coinCapService.deleteCryptoFromPerson(cryptoId, personId);
        return ResponseEntity.ok("Crypto " + cryptoId + " deleted successfully form person " + personId);
    }

    @PostMapping("/deleteCryptoFromPersonHTML")
    public String deleteCryptoFromPersonHTML(@RequestParam(required = false, name = "idCrypto") String crypto_id, @RequestParam(required = false, name = "idPerson") String person_id) {
        Long cryptoId = Long.parseLong(crypto_id);
        Long personId = Long.parseLong(person_id);
        if (coinCapService.deleteCryptoFromPerson(cryptoId, personId)) {
            return SUCCESS_METHOD;
        }
        return ERROR_METHOD;
    }
    @DeleteMapping("/{crypto_id}/chain/{chain_id}")
    public ResponseEntity<String> deleteCryptoFromChain(@PathVariable("crypto_id") Long cryptoId, @PathVariable("chain_id") Long chainId) {
        coinCapService.deleteCryptoFromChain(cryptoId, chainId);
        return ResponseEntity.ok("Crypto " + cryptoId + " deleted successfully form chain " + chainId);
    }

    @PostMapping("/deleteCryptoFromChainHTML")
    public String deleteCryptoFromChainHTML(@RequestParam(required = false, name = "idCrypto") String crypto_id, @RequestParam(required = false, name = "idChain") String chain_id) {
        Long cryptoId = Long.parseLong(crypto_id);
        Long chainId = Long.parseLong(chain_id);
        if (coinCapService.deleteCryptoFromChain(cryptoId, chainId)){
            return SUCCESS_METHOD;
        }
        return ERROR_METHOD;
    }


    @GetMapping("/addCryptoToPersonHTML")
    public String getAddCryptoToPersonHTML(){
        return "addCryptoToPerson";
    }
    @GetMapping("/addCryptoToChainHTML")
    public String getAddCryptoToCahinHTML(){
        return "addCryptoToChain";
    }
    @PostMapping("/addCryptoToPersonHTML")
    public String AddCryptoToPersonHTML(@RequestParam(required = false, name = "idCrypto") String crypto_id, @RequestParam(required = false, name = "idPerson") String person_id) {
        Long cryptoId = Long.parseLong(crypto_id);
        Long personId = Long.parseLong(person_id);
        Person person = personService.getPersonById(personId);
        CryptoData cryptoData = coinCapService.getCryptoDataById(cryptoId);
        if (person == null || cryptoData == null) {
            return ERROR_METHOD;
        }
        person.addCrypto(cryptoData);
        personService.updatePerson(personId, person);

        return SUCCESS_METHOD;
    }

    @PostMapping("/addCryptoToChainHTML")
    public String AddCryptoToChainHTML(@RequestParam(required = false, name = "idCrypto") String crypto_id, @RequestParam(required = false, name = "idChain") String chain_id) {
        Long cryptoId = Long.parseLong(crypto_id);
        Long chainId = Long.parseLong(chain_id);
        Chain chain = chainService.getChainById(chainId);
        CryptoData cryptoData = coinCapService.getCryptoDataById(cryptoId);
        if (chain == null || cryptoData == null){
            return ERROR_METHOD;
        }
        chain.addCrypto(cryptoData);
        cryptoData.setChain(chain);
        coinCapService.updateCryptoData(cryptoId, cryptoData);
        chainService.updateChain(chainId, chain);
        return SUCCESS_METHOD;
    }
}