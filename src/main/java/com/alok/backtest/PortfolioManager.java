package com.alok.backtest;

import java.time.YearMonth;
import java.util.*;

import com.alok.model.StockData;
import com.alok.util.Log;

public class PortfolioManager {
    private double portfolioValue;
    private final Map<String, Integer> longHoldings;
    private final Map<YearMonth, Map<String, Double>> sectorAllocation;
    private final Map<String, String> symbolSectorMap;

    public PortfolioManager(double initialCapital, List<StockData> allStockData) {
        this.portfolioValue = initialCapital;
        this.longHoldings = new HashMap<>();
        this.sectorAllocation = new TreeMap<>();
        this.symbolSectorMap = new HashMap<>();
        for (StockData s : allStockData) symbolSectorMap.put(s.getSymbol(), s.getSector());
    }

    public void buy(String symbol, int shares, double price, double tx) {
        double cost = shares * price * (1 + tx);
        if (cost > portfolioValue) return;
        portfolioValue -= cost;
        longHoldings.put(symbol, longHoldings.getOrDefault(symbol, 0) + shares);
        Log.info("BUY " + symbol + " x" + shares + " @ " + String.format("%.2f", price) + " | Cost: $" + String.format("%,.2f", cost));
    }

    public void sell(String symbol, int shares, double price, double tx) {
        int have = longHoldings.getOrDefault(symbol, 0);
        if (shares > have) shares = have;
        double proceeds = shares * price * (1 - tx);
        portfolioValue += proceeds;
        int left = have - shares;
        if (left <= 0) longHoldings.remove(symbol); else longHoldings.put(symbol, left);
        Log.info("SELL " + symbol + " x" + shares + " @ " + String.format("%.2f", price) + " | Proceeds: $" + String.format("%,.2f", proceeds));
    }

    public void rebalanceLongOnly(List<String> targetLongs, Map<String, Double> pricesToday, double positionSize, double tx) {
        // close positions not in target
        for (String held : new ArrayList<>(longHoldings.keySet())) {
            if (!targetLongs.contains(held)) {
                Double p = pricesToday.get(held);
                if (p != null) sell(held, longHoldings.get(held), p, tx);
            }
        }
        // open new
        double alloc = portfolioValue * positionSize;
        for (String sym : targetLongs) {
            if (longHoldings.containsKey(sym)) continue;
            Double p = pricesToday.get(sym);
            if (p == null || p <= 0) continue;
            int shares = (int)Math.floor(alloc / p);
            if (shares > 0) buy(sym, shares, p, tx);
        }
    }

    public void recordSectorAllocation(YearMonth month) {
        Map<String, Double> count = new HashMap<>();
        for (String sym : longHoldings.keySet()) {
            String sec = symbolSectorMap.getOrDefault(sym, "Unknown");
            count.put(sec, count.getOrDefault(sec, 0.0) + 1.0);
        }
        Map<String, Double> pct = new HashMap<>();
        int total = longHoldings.size();
        if (total > 0) {
            for (Map.Entry<String, Double> e : count.entrySet()) {
                pct.put(e.getKey(), 100.0 * e.getValue() / total);
            }
        }
        sectorAllocation.put(month, pct);
    }

    public void updatePortfolioValue(double v) { this.portfolioValue = v; }
    public double getPortfolioValue() { return portfolioValue; }
    public Map<String,Integer> getLongHoldings(){ return longHoldings; }
    public Map<YearMonth, Map<String, Double>> getSectorAllocation(){ return sectorAllocation; }
}
