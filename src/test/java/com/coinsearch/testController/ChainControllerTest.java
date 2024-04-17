package com.coinsearch.testController;
import com.coinsearch.controller.ChainController;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.service.ChainService;
import com.coinsearch.service.CoinCapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ChainControllerTest.class)
class ChainControllerTest {

    @Mock
    private ChainService chainService;

    @Mock
    private CoinCapService coinCapService;

    @InjectMocks
    private ChainController chainController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        // Mock data
        Chain chain = new Chain(1L, "test", new HashSet<>());
        when(chainService.createChain(chain)).thenReturn(chain);

        // Test
        ResponseEntity<Chain> responseEntity = chainController.create(chain);

        // Verify
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(chain, responseEntity.getBody());
        verify(chainService, times(1)).createChain(chain);
    }

    @Test
    void testGetChainById() {
        // Mock data
        Long chainId = 1L;
        Chain mockChain = new Chain(1L, "test", new HashSet<>());
        when(chainService.getChainById(chainId)).thenReturn(mockChain);

        // Test
        Chain result = chainController.getChainById(chainId);

        // Verify
        assertEquals(mockChain, result);
        verify(chainService, times(1)).getChainById(chainId);
    }

    @Test
    void testGetAllChains() {
        // Mock data
        List<Chain> mockChains = Arrays.asList(new Chain(1L, "test", new HashSet<>()), new Chain(1L, "test", new HashSet<>()));
        when(chainService.getAllChains()).thenReturn(mockChains);

        // Test
        ResponseEntity<List<Chain>> responseEntity = chainController.getAllChains();

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockChains, responseEntity.getBody());
        verify(chainService, times(1)).getAllChains();
    }

    @Test
    void testUpdateChain() {
        // Mock data
        Long chainId = 1L;
        Chain updatedChain = new Chain(1L, "test", new HashSet<>());
        when(chainService.updateChain(chainId, updatedChain)).thenReturn(updatedChain);

        // Test
        Chain result = chainController.updateChain(chainId, updatedChain);

        // Verify
        assertEquals(updatedChain, result);
        verify(chainService, times(1)).updateChain(chainId, updatedChain);
    }

    @Test
    void testAddCryptoToChain() {
        // Mock data
        Long chainId = 1L;
        Long cryptoId = 1L;
        Chain chain = new Chain(1L, "test", new HashSet<>());
        CryptoData cryptoData = new CryptoData();
        when(chainService.getChainById(chainId)).thenReturn(chain);
        when(coinCapService.getCryptoDataById(cryptoId)).thenReturn(cryptoData);
        when(coinCapService.updateCryptoData(cryptoId, cryptoData)).thenReturn(cryptoData);
        when(chainService.updateChain(chainId, chain)).thenReturn(chain);

        // Test
        Chain result = chainController.addCryptoToChain(chainId, cryptoId);

        // Verify
        assertEquals(chain, result);
        verify(chainService, times(1)).getChainById(chainId);
        verify(coinCapService, times(1)).getCryptoDataById(cryptoId);
        verify(coinCapService, times(1)).updateCryptoData(cryptoId, cryptoData);
        verify(chainService, times(1)).updateChain(chainId, chain);
    }

    @Test
    void testDeleteChain() {
        // Mock data
        Long chainId = 1L;

        // Test
        ResponseEntity<String> responseEntity = chainController.deleteChain(chainId);

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(chainService, times(1)).deleteChain(chainId);
    }
}
