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

        // Mock data
        List<CryptoData> mockCryptoData = Arrays.asList(new CryptoData(1L), new CryptoData(2L));
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

        CryptoData mockCryptoData = new CryptoData(1L);
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
        CryptoData mockCryptoData = new CryptoData(1L);
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
        // Mock data
        List<CryptoData> mockCryptoData = Arrays.asList(new CryptoData(1L), new CryptoData(2L));
        when(coinCapService.addList(cryptoCurrencies)).thenReturn(mockCryptoData);

        // Test
        ResponseEntity<List<CryptoData>> responseEntity = cryptocurrencyController.bulkOperation(cryptoCurrencies);

        // Verify
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockCryptoData, responseEntity.getBody());
        verify(coinCapService, times(1)).addList(cryptoCurrencies);
    }



}
