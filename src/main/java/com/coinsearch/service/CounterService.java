package com.coinsearch.service;

import com.coinsearch.model.ServiceCounter;
import lombok.Data;


import java.util.concurrent.atomic.AtomicInteger;

@Data
public final class CounterService {

    private CounterService() {
    }

    private static ServiceCounter serviceCounter = new ServiceCounter();

    private static AtomicInteger newEnhanceCounter = new AtomicInteger(0);

    public static synchronized void enhanceCounter() {
        if (serviceCounter.getCounterRequest() != null) {
            newEnhanceCounter = serviceCounter.getCounterRequest();
        }
        newEnhanceCounter.incrementAndGet();
        serviceCounter.setCounterRequest(newEnhanceCounter);
    }

    public static synchronized int getCounter() {
        AtomicInteger newCounter = serviceCounter.getCounterRequest();
        return newCounter.get();
    }
}