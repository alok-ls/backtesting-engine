package com.alok.strategy;

import java.time.LocalDate;
import java.util.*;

import com.alok.model.StockData;

public class SmaCrossoverStrategy implements Strategy {
    private final int shortW, longW;
    private final Map<String, TreeMap<LocalDate, Double>> priceMap = new HashMap<>();
    private final Map<LocalDate, List<String>> longs = new TreeMap<>();

    public SmaCrossoverStrategy(int shortW, int longW) {
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
            LinkedList<Double> sq = new LinkedList<>(), lq = new LinkedList<>();
            Map<LocalDate, Double> sS = new HashMap<>(), lS = new HashMap<>();
            for (Map.Entry<LocalDate, Double> e : px.entrySet()) {
                double p = e.getValue();
                sq.add(p); if (sq.size() > shortW) sq.removeFirst();
                lq.add(p); if (lq.size() > longW) lq.removeFirst();
                if (sq.size()==shortW && lq.size()==longW) {
                    sS.put(e.getKey(), avg(sq)); lS.put(e.getKey(), avg(lq));
                }
            }
            
            boolean inPosition = false;
            LocalDate prev = null;
            
            for (LocalDate d : px.keySet()) {
                if (!sS.containsKey(d) || !lS.containsKey(d)) { prev = d; continue; }
                
                // Check for crossover signals
                if (prev != null && sS.containsKey(prev) && lS.containsKey(prev)) {
                    double ps = sS.get(prev), pl = lS.get(prev), cs = sS.get(d), cl = lS.get(d);
                    
                    // Bullish crossover: short SMA crosses above long SMA
                    if (ps <= pl && cs > cl) {
                        inPosition = true;
                    }
                    // Bearish crossover: short SMA crosses below long SMA
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

    private double avg(LinkedList<Double> q){ double s=0; for(double x:q)s+=x; return s/q.size(); }

    @Override public Map<LocalDate, List<String>> getLongs(){ return longs; }
    @Override public Map<String, TreeMap<LocalDate, Double>> getPriceMap(){ return priceMap; }
}
