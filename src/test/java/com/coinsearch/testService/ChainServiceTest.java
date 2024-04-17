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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.*;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateChain() {
        // Mock data
        Chain chain = new Chain(1L, "test", new HashSet<>());
        when(chainRepository.save(chain)).thenReturn(chain);

        // Test
        Chain result = chainService.createChain(chain);

        // Verify
        assertEquals(chain, result);
        verify(chainRepository, times(1)).save(chain);
    }

    @Test
    void testGetChainById() {
        // Mock data
        long chainId = 1L;
        Chain expectedChain = new Chain(1L, "test", new HashSet<>());
        when(cache.getFromCache(anyString())).thenReturn(null);
        when(chainRepository.findById(Math.toIntExact(chainId))).thenReturn(Optional.of(expectedChain));

        // Test
        Chain result = chainService.getChainById(chainId);

        // Verify
        assertEquals(expectedChain, result);
        verify(cache, times(1)).addToCache(anyString(), eq(expectedChain));
    }

    @Test
    void testGetAllChains() {
        // Mock data
        List<Chain> expectedChains = asList(new Chain(), new Chain());
        when(chainRepository.findAll()).thenReturn(expectedChains);

        // Test
        List<Chain> result = chainService.getAllChains();

        // Verify
        assertEquals(expectedChains, result);
        verify(chainRepository, times(1)).findAll();
    }

    @Test
    void testUpdateChain() {
        // Mock data
        Long chainId = 1L;
        Chain updatedChain = new Chain(1L, "test", new HashSet<>());
        updatedChain.setId(chainId);
        when(chainRepository.findById(Math.toIntExact(chainId))).thenReturn(Optional.of(updatedChain));
        when(chainRepository.save(updatedChain)).thenReturn(updatedChain);

        // Test
        Chain result = chainService.updateChain(chainId, updatedChain);

        // Verify
        assertEquals(updatedChain, result);
        verify(chainRepository, times(1)).save(updatedChain);
        verify(cache, times(1)).removeFromCache(anyString());
        verify(cache, times(1)).addToCache(anyString(), eq(updatedChain));
    }

    @Test
    void testDeleteChain() {
        // Mock data
        long chainId = 1L;
        Chain chain = new Chain(1L, "test", new HashSet<>());
        CryptoData cryptoData = new CryptoData(3L);
        chain.setCryptocurrencies(new HashSet<>(List.of(cryptoData)));
        when(chainRepository.findById(Math.toIntExact(chainId))).thenReturn(Optional.of(chain));

        // Test
        assertDoesNotThrow(() -> chainService.deleteChain(chainId));

        // Verify
        verify(chainRepository, times(1)).deleteById(Math.toIntExact(chainId));
        assertTrue(chain.getCryptocurrencies().isEmpty());
        verify(cache, times(1)).removeFromCache(anyString());
    }

    @Test
    void testDeleteChain_NotFound() {
        // Mock data
        long chainId = 1L;
        when(chainRepository.findById(Math.toIntExact(chainId))).thenReturn(Optional.empty());

        // Test and Verify
        assertThrows(EntityNotFoundException.class, () -> chainService.deleteChain(chainId));
    }
}
