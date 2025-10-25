package com.alok.evaluation;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SignalAccuracy {
    private final Map<LocalDate, List<String>> longs;
    private final Map<String, TreeMap<LocalDate, Double>> priceMap;

    public SignalAccuracy(Map<LocalDate, List<String>> longs,
                          Map<String, TreeMap<LocalDate, Double>> priceMap) {
        this.longs = longs;
        this.priceMap = priceMap;
    }

    public double calculateAccuracy() {
        int correct = 0, total = 0;
        for (Map.Entry<LocalDate, List<String>> e : longs.entrySet()) {
            LocalDate d = e.getKey();
            for (String sym : e.getValue()) {
                TreeMap<LocalDate, Double> px = priceMap.get(sym);
                if (px == null) continue;
                Double p = px.get(d);
                LocalDate next = px.higherKey(d);
                if (p != null && next != null) {
                    Double pn = px.get(next);
                    if (pn != null) {
                        if (pn > p) correct++;
                        total++;
                    }
                }
            }
        }
        return total == 0 ? 0.0 : (double)correct/total;
    }
}
