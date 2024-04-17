package com.coinsearch.testService;

import com.coinsearch.component.Cache;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads(){

    }

    @Test
    void testCreateCryptocurrency() {
        // Mock data
        String cryptocurrency = "bitcoin";
        CryptoData cryptoData = new CryptoData(3L);
        when(restTemplate.getForObject(anyString(), eq(CryptoData.class))).thenReturn(cryptoData);
        when(cryptoRepository.save(cryptoData)).thenReturn(cryptoData);

        // Test
        CryptoData result = coinCapService.createCryptocurrency(cryptocurrency);

        // Verify
        assertEquals(cryptoData, result);
        verify(cryptoRepository, times(1)).save(cryptoData);
    }

    @Test
    void testAddList() {
        // Mock data
        List<String> cryptoCurrencies = Arrays.asList("bitcoin", "ethereum");
        CryptoData bitcoin = new CryptoData(3L);
        CryptoData ethereum = new CryptoData(3L);
        when(cryptoRepository.save(any())).thenReturn(bitcoin, ethereum);

        // Test
        List<CryptoData> result = coinCapService.addList(cryptoCurrencies);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.contains(bitcoin));
        assertTrue(result.contains(ethereum));
        verify(cryptoRepository, times(2)).save(any());
    }

    @Test
    void testGetCryptoDataById() {
        // Mock data
        long cryptoId = 1;
        CryptoData cryptoData = new CryptoData(3L);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));

        // Test
        CryptoData result = coinCapService.getCryptoDataById(cryptoId);

        // Verify
        assertEquals(cryptoData, result);
    }

    @Test
    void testGetCryptoDataByName() {
        // Mock data
        String name = "bitcoin";
        CryptoData cryptoData = new CryptoData(3L);
        when(cache.getFromCache(anyString())).thenReturn(null);
        when(cryptoRepository.findByName(name)).thenReturn(cryptoData);

        // Test
        CryptoData result = coinCapService.getCryptoDataByName(name);

        // Verify
        assertEquals(cryptoData, result);
        verify(cache, times(1)).addToCache(anyString(), any());
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
        CryptoData cryptoData = new CryptoData(3L);
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
        CryptoData cryptoData = new CryptoData(3L);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));
        doNothing().when(cache).removeFromCache(anyString());
        doNothing().when(cryptoRepository).deleteById((int) cryptoId);

        // Test
        assertDoesNotThrow(() -> coinCapService.deleteCrypto(cryptoId));
    }

    @Test
    void testDeleteCryptoFromPerson() {
        // Mock data
        long cryptoId = 1;
        long personId = 1;
        CryptoData cryptoData = new CryptoData(3L);
        Person person = new Person(1L, "test", new HashSet<>());
        person.getCryptocurrencies().add(cryptoData);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));
        when(personRepository.findById((int) personId)).thenReturn(java.util.Optional.of(person));
        when(cryptoRepository.save(cryptoData)).thenReturn(cryptoData);
        when(personRepository.save(person)).thenReturn(person);

        // Test
        assertDoesNotThrow(() -> coinCapService.deleteCryptoFromPerson(cryptoId, personId));
    }

    @Test
    void testDeleteCryptoFromChain() {
        // Mock data
        long cryptoId = 1;
        long chainId = 1;
        CryptoData cryptoData = new CryptoData(3L);
        Chain chain = new Chain(1L, "test", new HashSet<>());
        chain.getCryptocurrencies().add(cryptoData);
        when(cryptoRepository.findById((int) cryptoId)).thenReturn(java.util.Optional.of(cryptoData));
        when(chainRepository.findById((int) chainId)).thenReturn(java.util.Optional.of(chain));
        when(chainRepository.save(chain)).thenReturn(chain);

        // Test
        assertDoesNotThrow(() -> coinCapService.deleteCryptoFromChain(cryptoId, chainId));
    }

}
