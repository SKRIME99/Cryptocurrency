package com.coinsearch.model;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CryptocurrencyData {
    private Data data;
    private long timestamp;

    @Getter
    @Setter
    public static class Data {
        private String id;
        private String rank;
        private String symbol;
        private String name;
        private String supply;
        private String maxSupply;
        private String marketCapUsd;
        private String volumeUsd24Hr;
        private String priceUsd;
        private String changePercent24Hr;
        private String vwap24Hr;
    }
}