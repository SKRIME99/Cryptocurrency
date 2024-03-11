package com.coinsearch.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CryptoData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long crytpoId;


    private String id;
    private String name;
    private String rank;
    private String symbol;
    private String supply;
    private String maxSupply;
    private String marketCapUsd;
    private String volumeUsd24Hr;
    private String priceUsd;
    private String changePercent24Hr;
    private String vwap24Hr;

    @ManyToOne
    @JoinColumn(name = "chain_id")
    @JsonBackReference
    private Chain chain;

    @JsonIgnore
    @ManyToMany(mappedBy = "cryptocurrencies")
    private Set<Person> persons;
}
