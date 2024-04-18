package com.coinsearch.testService;

import com.coinsearch.component.Cache;
import com.coinsearch.exception.EntityNotFoundException;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.repository.ChainRepository;
import com.coinsearch.service.ChainService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChainServiceTest {

    @Mock
    private ChainRepository chainRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private ChainService chainService;

    private Chain chain;

    @BeforeEach
    void setUp() {
        chain = new Chain();
        chain.setId(1L);
        chain.setName("Ethereum");
        CryptoData crypto = new CryptoData(1L);
        chain.setCryptocurrencies(new HashSet<>(Collections.singletonList(crypto)));
    }

    @Test
    void testCreateChain() {
        when(chainRepository.save(chain)).thenReturn(chain);
        Chain createdChain = chainService.createChain(chain);
        assertNotNull(createdChain);
        assertEquals(chain, createdChain);
        verify(chainRepository, times(1)).save(chain);
    }

    @Test
    void testGetChainById() {
        String cacheKey = "chain-1";
        when(cache.getFromCache(cacheKey)).thenReturn(null);
        when(chainRepository.findById(1)).thenReturn(Optional.of(chain));
        doNothing().when(cache).addToCache(cacheKey, chain);

        Chain fetchedChain = chainService.getChainById(1L);
        assertNotNull(fetchedChain);
        assertEquals(chain, fetchedChain);
        verify(chainRepository, times(1)).findById(1);
        verify(cache, times(1)).getFromCache(cacheKey);
        verify(cache, times(1)).addToCache(cacheKey, chain);
    }

    @Test
    void testGetChainByIdFromCache() {
        String cacheKey = "chain-1";
        when(cache.getFromCache(cacheKey)).thenReturn(chain);

        Chain fetchedChain = chainService.getChainById(1L);
        assertNotNull(fetchedChain);
        assertEquals(chain, fetchedChain);
        verify(chainRepository, never()).findById(1);
        verify(cache, times(1)).getFromCache(cacheKey);
        verify(cache, never()).addToCache(cacheKey, chain);
    }

    @Test
    void testGetAllChains() {
        List<Chain> chains = new ArrayList<>();
        chains.add(chain);
        when(chainRepository.findAll()).thenReturn(chains);

        List<Chain> fetchedChains = chainService.getAllChains();
        assertNotNull(fetchedChains);
        assertEquals(1, fetchedChains.size());
        assertEquals(chain, fetchedChains.get(0));
        verify(chainRepository, times(1)).findAll();
    }

    @Test
    void testUpdateChain() {
        Long chainId = 1L;
        String cacheKey = "chain-1";

        Chain existingChain = new Chain();
        existingChain.setId(chainId);
        existingChain.setName("Ethereum");
        existingChain.setCryptocurrencies(new HashSet<>());

        Chain updatedChain = new Chain();
        updatedChain.setId(chainId);
        updatedChain.setName("Ethereum 2.0");
        updatedChain.setCryptocurrencies(new HashSet<>());

        when(chainRepository.findById(Math.toIntExact(chainId))).thenReturn(Optional.of(existingChain));
        when(chainRepository.save(any(Chain.class))).thenReturn(updatedChain);
        doNothing().when(cache).removeFromCache(cacheKey);
        doNothing().when(cache).addToCache(any(String.class), any(Chain.class));

        Chain returnedChain = chainService.updateChain(chainId, updatedChain);

        assertNotNull(returnedChain);
        assertEquals("Ethereum 2.0", returnedChain.getName());
        verify(chainRepository, times(1)).findById(Math.toIntExact(chainId));
        verify(chainRepository, times(1)).save(any(Chain.class));
        verify(cache, times(1)).removeFromCache(cacheKey);
        verify(cache, times(1)).addToCache(any(String.class), any(Chain.class));
    }

    @Test
    void testDeleteChain() {
        String cacheKey = "chain-1";

        when(chainRepository.findById(1)).thenReturn(Optional.of(chain));
        doNothing().when(cache).removeFromCache(cacheKey);
        doNothing().when(chainRepository).deleteById(1);

        chainService.deleteChain(1L);

        verify(chainRepository, times(1)).findById(1);
        verify(cache, times(1)).removeFromCache(cacheKey);
        verify(chainRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetChainByIdNotFound() {
        when(chainRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> chainService.getChainById(1L));
        verify(chainRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateChainNotFound() {
        when(chainRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> chainService.updateChain(1L, new Chain()));
        verify(chainRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteChainNotFound() {
        when(chainRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> chainService.deleteChain(1L));
        verify(chainRepository, times(1)).findById(1);
    }
}