package com.coinsearch.testService;

import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    private CryptoData cryptoData;
    private CryptocurrencyData cryptocurrencyData;
    private Person person;
    private Chain chain;

    @BeforeEach
    void setUp() {
        cryptoData = new CryptoData(1L);
        cryptoData.setName("Bitcoin");
        cryptoData.setPersons(new HashSet<>());
        cryptoData.setChain(null);

        cryptocurrencyData = new CryptocurrencyData();
        cryptocurrencyData.setData(cryptoData);

        person = new Person();
        person.setId(1L);
        person.setCryptocurrencies(new HashSet<>(Arrays.asList(cryptoData)));

        chain = new Chain();
        chain.setId(1L);
        chain.setCryptocurrencies(new HashSet<>(Arrays.asList(cryptoData)));
    }

    @Test
    void testCreateCryptocurrency() {
        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(cryptocurrencyData);
        when(cryptoRepository.save(any(CryptoData.class))).thenReturn(cryptoData);

        CryptoData createdCryptoData = coinCapService.createCryptocurrency("bitcoin");

        assertEquals(cryptoData, createdCryptoData);
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
        verify(cryptoRepository, times(1)).save(any(CryptoData.class));
    }

    @Test
    void testGetCryptoDataById() {
        when(cryptoRepository.findById(anyInt()))
                .thenReturn(Optional.of(cryptoData));

        CryptoData retrievedCryptoData = coinCapService.getCryptoDataById(1L);

        assertEquals(cryptoData, retrievedCryptoData);
        verify(cryptoRepository, times(1)).findById(anyInt());
    }

    @Test
    void testGetCryptoDataByName() {
        when(cache.getFromCache(anyString()))
                .thenReturn(null);
        when(cryptoRepository.findByName(anyString()))
                .thenReturn(cryptoData);
        doNothing().when(cache).addToCache(anyString(), any(CryptoData.class));

        CryptoData retrievedCryptoData = coinCapService.getCryptoDataByName("Bitcoin");

        assertEquals(cryptoData, retrievedCryptoData);
        verify(cache, times(1)).getFromCache(anyString());
        verify(cryptoRepository, times(1)).findByName(anyString());
        verify(cache, times(1)).addToCache(anyString(), any(CryptoData.class));
    }

    @Test
    void testGetAllCryptoData() {
        when(cryptoRepository.findAll())
                .thenReturn(Arrays.asList(cryptoData));

        List<CryptoData> allCryptoData = coinCapService.getAllCryptoData();

        assertEquals(1, allCryptoData.size());
        assertEquals(cryptoData, allCryptoData.get(0));
        verify(cryptoRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCryptoData() {
        when(cryptoRepository.findById(anyInt()))
                .thenReturn(Optional.of(cryptoData));
        doNothing().when(cache).removeFromCache(anyString());
        when(cryptoRepository.save(any(CryptoData.class)))
                .thenReturn(cryptoData);
        doNothing().when(cache).addToCache(anyString(), any(CryptoData.class));

        CryptoData updatedCryptoData = coinCapService.updateCryptoData(1L, cryptoData);

        assertEquals(cryptoData, updatedCryptoData);
        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, times(1)).removeFromCache(anyString());
        verify(cryptoRepository, times(1)).save(any(CryptoData.class));
        verify(cache, times(1)).addToCache(anyString(), any(CryptoData.class));
    }

    @Test
    void testDeleteCrypto() {
        when(cryptoRepository.findById(anyInt()))
                .thenReturn(Optional.of(cryptoData));
        doNothing().when(cache).removeFromCache(anyString());
        doNothing().when(cryptoRepository).deleteById(anyInt());

        coinCapService.deleteCrypto(1L);

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, times(1)).removeFromCache(anyString());
        verify(cryptoRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteCryptoFromPerson() {
        when(cryptoRepository.findById(anyInt()))
                .thenReturn(Optional.of(cryptoData));
        when(personRepository.findById(anyInt()))
                .thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class)))
                .thenReturn(person);
        when(cryptoRepository.save(any(CryptoData.class)))
                .thenReturn(cryptoData);

        coinCapService.deleteCryptoFromPerson(1L, 1L);

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(personRepository, times(1)).findById(anyInt());
    }


    @Test
    void testGetCryptoDataByIdNotFound() {
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coinCapService.getCryptoDataById(1L));
        verify(cryptoRepository, times(1)).findById(anyInt());
    }


    @Test
    void testUpdateCryptoDataNotFound() {
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coinCapService.updateCryptoData(3L, cryptoData));
        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, never()).removeFromCache(anyString());
        verify(cryptoRepository, never()).save(any(CryptoData.class));
        verify(cache, never()).addToCache(anyString(), any(CryptoData.class));
    }

    @Test
    void testDeleteCryptoWithPersonsAndChain() {
        cryptoData.setChain(new Chain());
        cryptoData.setPersons(new HashSet<>(Arrays.asList(new Person())));

        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.of(cryptoData));

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCrypto(1L));

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, never()).removeFromCache(anyString());
        verify(cryptoRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteCryptoWithPersons() {
        cryptoData.setPersons(new HashSet<>(Arrays.asList(new Person())));

        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.of(cryptoData));

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCrypto(1L));

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, never()).removeFromCache(anyString());
        verify(cryptoRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteCryptoWithChain() {
        cryptoData.setChain(new Chain());

        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.of(cryptoData));

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCrypto(1L));

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, never()).removeFromCache(anyString());
        verify(cryptoRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteCryptoFromPersonPersonNotFound() {
        when(personRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.of(cryptoData));

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCryptoFromPerson(1L, 4L));

        verify(personRepository, times(1)).findById(anyInt());
        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(personRepository, never()).save(any(Person.class));
        verify(cryptoRepository, never()).save(any(CryptoData.class));
    }

    @Test
    void testDeleteCryptoFromPersonCryptoNotFound() {
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCryptoFromPerson(4L, 1L));

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(personRepository, never()).findById(anyInt());
        verify(personRepository, never()).save(any(Person.class));
        verify(cryptoRepository, never()).save(any(CryptoData.class));
    }

    @Test
    void testDeleteCryptoNotFound() {
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCrypto(4L));

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(cache, never()).removeFromCache(anyString());
        verify(cryptoRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteCryptoFromChain() {
        Chain chain = new Chain();
        chain.setId(3L);
        chain.setCryptocurrencies(new HashSet<>(Arrays.asList(cryptoData)));

        when(chainRepository.findById(anyInt())).thenReturn(Optional.of(chain));
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.of(cryptoData));
        when(chainRepository.save(any(Chain.class))).thenReturn(chain);

        coinCapService.deleteCryptoFromChain(1L, chain.getId());

        verify(chainRepository, times(1)).findById(anyInt());
        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(chainRepository, times(1)).save(any(Chain.class));
        assertNull(cryptoData.getChain());
        assertFalse(chain.getCryptocurrencies().contains(cryptoData));
    }

    @Test
    void testDeleteCryptoFromChainChainNotFound() {
        when(chainRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.of(cryptoData));

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCryptoFromChain(1L, 4L));

        verify(chainRepository, times(1)).findById(anyInt());
        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(chainRepository, never()).save(any(Chain.class));
    }

    @Test
    void testDeleteCryptoFromChainCryptoNotFound() {
        Chain chain = new Chain();
        chain.setCryptocurrencies(new HashSet<>(Arrays.asList(cryptoData)));

        when(cryptoRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coinCapService.deleteCryptoFromChain(3L, chain.getId()));

        verify(cryptoRepository, times(1)).findById(anyInt());
        verify(chainRepository, never()).findById(anyInt());
        verify(chainRepository, never()).save(any(Chain.class));
    }
}