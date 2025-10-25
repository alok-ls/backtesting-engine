package com.alok.backtest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import com.alok.model.StockData;
import com.alok.util.Log;

public class Backtester {
    private final Map<LocalDate, List<String>> longs; // monthly signal dates -> symbols
    private final Map<String, TreeMap<LocalDate, Double>> priceMap;
    private final Set<LocalDate> allDates;
    private final Map<LocalDate, Double> strategyReturns; // daily
    private final PortfolioManager pm;

    private final double positionSize; // Percentage of portfolio per position
    private final double transactionCost = 0.0; // user asked to ignore costs

    public Backtester(Map<LocalDate, List<String>> longs,
                      Map<String, TreeMap<LocalDate, Double>> priceMap,
                      List<StockData> allStockData,
                      double initialCapital,
                      String outDir,
                      double positionSize) {
        this.longs = longs;
        this.priceMap = priceMap;
        this.allDates = new TreeSet<>();
        this.strategyReturns = new TreeMap<>();
        this.pm = new PortfolioManager(initialCapital, allStockData);
        this.positionSize = positionSize;
        collectAllDates();
    }

    private void collectAllDates() {
        for (TreeMap<LocalDate, Double> symbolPrices : priceMap.values()) {
            allDates.addAll(symbolPrices.keySet());
        }
    }

    public void runBacktest() {
        List<LocalDate> dates = new ArrayList<>(allDates);
        Collections.sort(dates);
        YearMonth currentMonth = null;

        for (LocalDate d : dates) {
            YearMonth ym = YearMonth.from(d);
            if (!ym.equals(currentMonth)) {
                currentMonth = ym;
                List<String> newLongs = longs.getOrDefault(d, Collections.emptyList());
                pm.rebalanceLongOnly(newLongs, getPricesOnDate(d), positionSize, transactionCost);
                pm.recordSectorAllocation(currentMonth);
                Log.info("Rebalanced on " + d + " | Portfolio: $" + String.format("%,.2f", pm.getPortfolioValue())
                        + " | Positions: " + pm.getLongHoldings().keySet());
            }
            double dailyReturn = calcDailyReturn(d);
            double prev = pm.getPortfolioValue();
            double newVal = prev * (1 + dailyReturn);
            pm.updatePortfolioValue(newVal);
            strategyReturns.put(d, (newVal - prev)/prev);
            Log.info("Date: " + d + " | Daily Return: " + String.format("%.4f%%", dailyReturn*100) + " | NAV: $" + String.format("%,.2f", newVal));
        }
        Log.info("Final NAV: $" + String.format("%,.2f", pm.getPortfolioValue()));
    }

    private Map<String, Double> getPricesOnDate(LocalDate date) {
        Map<String, Double> map = new HashMap<>();
        for (String sym : priceMap.keySet()) {
            Double p = priceMap.get(sym).get(date);
            if (p != null) map.put(sym, p);
        }
        return map;
    }

    private double calcDailyReturn(LocalDate date) {
        double pv = pm.getPortfolioValue();
        if (pv <= 0) return 0.0;
        double contributed = 0.0;
        for (Map.Entry<String, Integer> e : pm.getLongHoldings().entrySet()) {
            String sym = e.getKey();
            int sh = e.getValue();
            TreeMap<LocalDate, Double> px = priceMap.get(sym);
            Double p = px.get(date);
            Map.Entry<LocalDate, Double> prev = px.lowerEntry(date);
            if (p != null && prev != null) {
                double ret = (p - prev.getValue()) / prev.getValue();
                contributed += ret * (sh * prev.getValue()) / pv;
            }
        }
        return contributed;
    }

    public Map<LocalDate, Double> getStrategyReturns() { return strategyReturns; }
    public PortfolioManager getPortfolioManager() { return pm; }
}
