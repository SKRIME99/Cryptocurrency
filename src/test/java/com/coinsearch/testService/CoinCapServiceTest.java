package com.coinsearch.testService;

import com.coinsearch.component.Cache;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.model.CryptocurrencyData;
import com.coinsearch.model.Person;
import com.coinsearch.repository.ChainRepository;
import com.coinsearch.repository.CryptocurrencyRepository;
import com.coinsearch.repository.PersonRepository;
import com.coinsearch.service.CoinCapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoinCapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CryptocurrencyRepository cryptoRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ChainRepository chainRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private CoinCapService coinCapService;



    @Test
    void testCreateCryptocurrency() {
        // Mock data
        String cryptocurrency = "bitcoin";

        CryptoData cryptoData = new CryptoData(1L);
        when(restTemplate.getForObject(anyString(), eq(CryptoData.class))).thenReturn(new CryptocurrencyData(cryptoData, 1L).getData());
        when(cryptoRepository.save(cryptoData)).thenReturn(cryptoData);

        // Test
        CryptoData result = coinCapService.createCryptocurrency(cryptocurrency);

        // Verify
        assertEquals(cryptoData, result);
        verify(cryptoRepository, times(1)).save(cryptoData);
    }


    @Test
    void testGetCryptoDataById() {
        // Mock data
        long cryptoId = 1;
        CryptoData cryptoData = new CryptoData(1);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));

        // Test
        CryptoData result = coinCapService.getCryptoDataById(cryptoId);

        // Verify
        assertEquals(cryptoData, result);
    }


    @Test
    void testGetAllCryptoData() {
        // Mock data
        List<CryptoData> cryptoDataList = Arrays.asList(new CryptoData(), new CryptoData());
        when(cryptoRepository.findAll()).thenReturn(cryptoDataList);

        // Test
        List<CryptoData> result = coinCapService.getAllCryptoData();

        // Verify
        assertEquals(cryptoDataList, result);
    }

    @Test
    void testUpdateCryptoData() {
        // Mock data
        long cryptoId = 1;
        CryptoData cryptoData = new CryptoData(1L);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));
        when(cryptoRepository.save(cryptoData)).thenReturn(cryptoData);

        // Test
        CryptoData result = coinCapService.updateCryptoData(cryptoId, cryptoData);

        // Verify
        assertEquals(cryptoData, result);
    }

    @Test
    void testDeleteCrypto() {
        // Mock data
        long cryptoId = 1;
        CryptoData cryptoData = new CryptoData(1L);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));
        doNothing().when(cache).removeFromCache(anyString());
        doNothing().when(cryptoRepository).deleteById((int) cryptoId);

        // Test
        assertDoesNotThrow(() -> coinCapService.deleteCrypto(cryptoId));

    }






