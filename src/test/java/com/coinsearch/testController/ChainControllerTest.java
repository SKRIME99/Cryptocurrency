package com.coinsearch.testController;
import com.coinsearch.controller.ChainController;
import com.coinsearch.model.Chain;
import com.coinsearch.model.CryptoData;
import com.coinsearch.service.ChainService;
import com.coinsearch.service.CoinCapService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChainControllerTest {

    @Mock
    private ChainService chainService;

    @Mock
    private CoinCapService coinCapService;

    @InjectMocks
    private ChainController chainController;


    @Test
    void testCreate() {
        // Mock data
        Chain chain = new Chain();
        chain.setId(1L);
        when(chainService.createChain(chain)).thenReturn(chain);

        // Test
        ResponseEntity<Chain> responseEntity = chainController.create(chain);

        // Verify
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        verify(chainService, times(1)).createChain(chain);
    }

    @Test
    void testGetChainById() {
        // Mock data
        Long chainId = 1L;
        Chain mockChain = new Chain();
        mockChain.setId(chainId);
        when(chainService.getChainById(chainId)).thenReturn(mockChain);

        // Test
        Chain result = chainController.getChainById(chainId);

        // Verify
        assertEquals(mockChain, result);
        verify(chainService, times(1)).getChainById(chainId);
    }


    @Test
    void testUpdateChain() {
        // Mock data
        Long chainId = 1L;
        Chain updatedChain = new Chain();
        updatedChain.setId(chainId);
        when(chainService.updateChain(chainId, updatedChain)).thenReturn(updatedChain);

        // Test
        Chain result = chainController.updateChain(chainId, updatedChain);

        // Verify
        assertEquals(updatedChain, result);
        verify(chainService, times(1)).updateChain(chainId, updatedChain);
    }

    @Test
    void testDeleteChain() {
        // Mock data
        Long chainId = 1L;

        // Test
        ResponseEntity<String> responseEntity = chainController.deleteChain(chainId);

        // Verify
        verify(chainService, times(1)).deleteChain(chainId);
    }
}
