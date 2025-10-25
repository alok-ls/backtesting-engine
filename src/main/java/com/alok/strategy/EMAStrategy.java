package com.alok.strategy;

import java.time.LocalDate;
import java.util.*;

import com.alok.model.StockData;

public class EMAStrategy implements Strategy {
    private final int shortW;
    private final int longW;
    private final Map<String, TreeMap<LocalDate, Double>> priceMap = new HashMap<>();
    private final Map<LocalDate, List<String>> longs = new TreeMap<>();

    public EMAStrategy(int shortW, int longW) {
        if (shortW >= longW) throw new IllegalArgumentException("shortW must be < longW");
        this.shortW = shortW; 
        this.longW = longW;
    }

    @Override public void prepareData(List<StockData> data) {
        for (StockData d : data) priceMap.computeIfAbsent(d.getSymbol(), k->new TreeMap<>()).put(d.getDate(), d.getClosePrice());
    }

    @Override public void generateSignals() {
        for (String sym : priceMap.keySet()) {
            TreeMap<LocalDate, Double> px = priceMap.get(sym);
            if (px.size() < longW) continue;
            TreeMap<LocalDate, Double> sE = ema(px, shortW);
            TreeMap<LocalDate, Double> lE = ema(px, longW);
            
            boolean inPosition = false;
            LocalDate prev = null;
            
            for (LocalDate d : px.keySet()) {
                if (!sE.containsKey(d) || !lE.containsKey(d)) { prev = d; continue; }
                
                // Check for crossover signals
                if (prev != null && sE.containsKey(prev) && lE.containsKey(prev)) {
                    double ps = sE.get(prev), pl = lE.get(prev), cs = sE.get(d), cl = lE.get(d);
                    
                    // Bullish crossover: short EMA crosses above long EMA
                    if (ps <= pl && cs > cl) {
                        inPosition = true;
                    }
                    // Bearish crossover: short EMA crosses below long EMA
                    else if (ps >= pl && cs < cl) {
                        inPosition = false;
                    }
                }
                
                // If we're in a position, add to signals for this date
                if (inPosition) {
                    longs.computeIfAbsent(d, k->new ArrayList<>()).add(sym);
                }
                
                prev = d;
            }
        }
    }

    private TreeMap<LocalDate, Double> ema(TreeMap<LocalDate, Double> px, int w) {
        TreeMap<LocalDate, Double> out = new TreeMap<>();
        double k = 2.0/(w+1);
        double ema = 0.0; int c=0;
        for (Map.Entry<LocalDate, Double> e : px.entrySet()) {
            double p = e.getValue();
            if (c < w) { ema += p; c++; if (c==w) { ema /= w; out.put(e.getKey(), ema);} continue; }
            ema = (p - ema)*k + ema;
            out.put(e.getKey(), ema);
        }
        return out;
    }

    @Override public Map<LocalDate, List<String>> getLongs(){ return longs; }
    @Override public Map<String, TreeMap<LocalDate, Double>> getPriceMap(){ return priceMap; }
}
