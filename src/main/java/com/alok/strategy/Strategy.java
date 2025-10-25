package com.alok.strategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alok.model.StockData;

public interface Strategy {
    void prepareData(List<StockData> dataList);
    void generateSignals();

    Map<LocalDate, List<String>> getLongs();
    Map<String, TreeMap<LocalDate, Double>> getPriceMap();
}
