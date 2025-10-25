package com.alok.strategy;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.alok.model.StockData;

public class FundamentalStrategy implements Strategy {
    private final double topPercentile; // long top X% by value (lowest PE)
    private final Map<String, TreeMap<LocalDate, Double>> priceMap = new HashMap<>();
    private final Map<LocalDate, List<String>> longs = new TreeMap<>();
    private final Map<LocalDate, Map<String, Double>> peByDate = new TreeMap<>();

    public FundamentalStrategy(double topPercentile) {
        if (topPercentile <= 0 || topPercentile >= 50) throw new IllegalArgumentException("percentile (0,50)");
        this.topPercentile = topPercentile;
    }

    @Override public void prepareData(List<StockData> data) {
        for (StockData d : data) {
            priceMap.computeIfAbsent(d.getSymbol(), k->new TreeMap<>()).put(d.getDate(), d.getClosePrice());
            peByDate.computeIfAbsent(d.getDate(), k->new HashMap<>()).put(d.getSymbol(), d.getPeRatio());
        }
    }

    @Override public void generateSignals() {
        for (LocalDate d : peByDate.keySet()) {
            Map<String, Double> pe = peByDate.get(d);
            List<Map.Entry<String, Double>> filtered = pe.entrySet().stream()
                    .filter(e -> e.getValue()!=null && e.getValue()>0.0 && priceMap.containsKey(e.getKey()) && priceMap.get(e.getKey()).containsKey(d))
                    .sorted(Map.Entry.comparingByValue()) // lowest PE first
                    .collect(Collectors.toList());
            int n = (int)Math.ceil(filtered.size() * (topPercentile/100.0));
            if (n <= 0) continue;
            List<String> pick = filtered.subList(0, n).stream().map(Map.Entry::getKey).collect(Collectors.toList());
            longs.put(d, pick);
        }
    }

    @Override public Map<LocalDate, List<String>> getLongs(){ return longs; }
    @Override public Map<String, TreeMap<LocalDate, Double>> getPriceMap(){ return priceMap; }
}
