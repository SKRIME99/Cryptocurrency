package com.coinsearch.testController;

import com.coinsearch.controller.CryptocurrencyController;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.Person;
import com.coinsearch.service.CoinCapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptocurrencyControllerTest {

    @Mock
    private CoinCapService coinCapService;

    @InjectMocks
    private CryptocurrencyController cryptocurrencyController;


    @Test
    void testGetAllCryptoData() {
        Set<Person> set = new HashSet<>(Set.of(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()),new Person(3L, "test", new HashSet<>())));
        // Mock data
        List<CryptoData> mockCryptoData = Arrays.asList(new CryptoData(1L, set, new Chain(1L, "test", new HashSet<>())), new CryptoData(2L, set, new Chain(1L, "test", new HashSet<>())));
        when(coinCapService.getAllCryptoData()).thenReturn(mockCryptoData);

        // Test
        ResponseEntity<List<CryptoData>> responseEntity = cryptocurrencyController.getAllCryptoData();

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockCryptoData, responseEntity.getBody());
        verify(coinCapService, times(1)).getAllCryptoData();
    }

    @Test
    void testGetCryptoDataById() {
        // Mock data
        Set<Person> set = new HashSet<>(Set.of(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()),new Person(3L, "test", new HashSet<>())));
        CryptoData mockCryptoData = new CryptoData(1L, set, new Chain(1L, "test", new HashSet<>()));
        String name = "bitcoin";
        when(coinCapService.getCryptoDataByName(name)).thenReturn(mockCryptoData);

        // Test
        CryptoData result = cryptocurrencyController.getCryptoDataById(name);

        // Verify
        assertEquals(mockCryptoData, result);
        verify(coinCapService, times(1)).getCryptoDataByName(name);
    }

    @Test
    void testCreate() {
        // Mock data
        String cryptoCurrency = "bitcoin";
        Set<Person> set = new HashSet<>(Set.of(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()),new Person(3L, "test", new HashSet<>())));
        CryptoData mockCryptoData = new CryptoData(1L, set, new Chain(1L, "test", new HashSet<>()));
        when(coinCapService.createCryptocurrency(cryptoCurrency)).thenReturn(mockCryptoData);

        // Test
        ResponseEntity<CryptoData> responseEntity = cryptocurrencyController.create(cryptoCurrency);

        // Verify
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockCryptoData, responseEntity.getBody());
        verify(coinCapService, times(1)).createCryptocurrency(cryptoCurrency);
    }

    @Test
    void testBulkOperation() {
        // Mock data
        List<String> cryptoCurrencies = Arrays.asList("bitcoin", "ethereum");
        Set<Person> set = new HashSet<>(Set.of(new Person(1L, "test", new HashSet<>()), new Person(2L, "test", new HashSet<>()),new Person(3L, "test", new HashSet<>())));
        // Mock data
        List<CryptoData> mockCryptoData = Arrays.asList(new CryptoData(1L, set, new Chain(1L, "test", new HashSet<>())), new CryptoData(2L, set, new Chain(1L, "test", new HashSet<>())));
        when(coinCapService.addList(cryptoCurrencies)).thenReturn(mockCryptoData);

        // Test
        ResponseEntity<List<CryptoData>> responseEntity = cryptocurrencyController.bulkOperation(cryptoCurrencies);

        // Verify
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockCryptoData, responseEntity.getBody());
        verify(coinCapService, times(1)).addList(cryptoCurrencies);
    }

    @Test
    void testDeleteCrypto() {
        // Mock data
        Long cryptoId = 1L;

        // Test
        ResponseEntity<String> responseEntity = cryptocurrencyController.deleteCrypto(cryptoId);

        // Verify
        verify(coinCapService, times(1)).deleteCrypto(cryptoId);
    }

    @Test
    void testDeleteCryptoFromPerson() {
        // Mock data
        Long cryptoId = 1L;
        Long personId = 1L;

        // Test
        ResponseEntity<String> responseEntity = cryptocurrencyController.deleteCryptoFromPerson(cryptoId, personId);

        // Verify

        verify(coinCapService, times(1)).deleteCryptoFromPerson(cryptoId, personId);
    }

    @Test
    void testDeleteCryptoFromChain() {
        // Mock data
        Long cryptoId = 1L;
        Long chainId = 1L;

        // Test
        ResponseEntity<String> responseEntity = cryptocurrencyController.deleteCryptoFromChain(cryptoId, chainId);

        // Verify
        verify(coinCapService, times(1)).deleteCryptoFromChain(cryptoId, chainId);
    }
}
