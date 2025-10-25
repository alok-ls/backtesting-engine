package com.alok.data;

import com.alok.model.StockData;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataLoader {
    private final String stockDataPath;
    private final String outDir;
    private final List<StockData> stockDataList;
    private final Map<LocalDate, Double> marketReturns; // equal-weighted across available symbols each day

    public DataLoader(String stockDataPath, String outDir) {
        this.stockDataPath = stockDataPath;
        this.outDir = outDir;
        this.stockDataList = new ArrayList<>();
        this.marketReturns = new TreeMap<>();
    }

    public void loadStockData() throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(stockDataPath))) {
            String[] line;
            String[] header = reader.readNext(); // header
            while ((line = reader.readNext()) != null) {
                if (line.length < 4) continue;
                String sector = line[0].trim();
                String symbol = line[1].trim();
                LocalDate date = LocalDate.parse(line[2].trim());
                double close = Double.parseDouble(line[3].trim());
                double eps = line.length >= 5 && !line[4].isEmpty() ? Double.parseDouble(line[4].trim()) : 0.0;
                double pe = line.length >= 6 && !line[5].isEmpty() ? Double.parseDouble(line[5].trim()) : 0.0;
                stockDataList.add(new StockData(date, symbol, sector, close, eps, pe));
            }
        }
    }

    public void computeAndWriteMarketReturns() throws IOException {
        // organize prices by symbol
        Map<String, TreeMap<LocalDate, Double>> prices = new HashMap<>();
        for (StockData d : stockDataList) {
            prices.computeIfAbsent(d.getSymbol(), k -> new TreeMap<>()).put(d.getDate(), d.getClosePrice());
        }
        // collect all dates
        Set<LocalDate> allDates = new TreeSet<>();
        for (TreeMap<LocalDate, Double> m : prices.values()) allDates.addAll(m.keySet());
        // compute equal-weighted market return per date
        LocalDate prev = null;
        for (LocalDate date : allDates) {
            double sum = 0.0;
            int count = 0;
            for (Map.Entry<String, TreeMap<LocalDate, Double>> e : prices.entrySet()) {
                Double p = e.getValue().get(date);
                Map.Entry<LocalDate, Double> prevEntry = e.getValue().lowerEntry(date);
                if (p != null && prevEntry != null) {
                    double r = (p - prevEntry.getValue()) / prevEntry.getValue();
                    sum += r;
                    count++;
                }
            }
            if (count > 0) {
                marketReturns.put(date, sum / count);
            }
        }
        // write CSV
        Files.createDirectories(Path.of(outDir));
        try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of(outDir, "market_returns.csv").toFile()))) {
            pw.println("Date,Return");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (Map.Entry<LocalDate, Double> e : marketReturns.entrySet()) {
                pw.println(e.getKey().format(fmt) + "," + String.format(java.util.Locale.US, "%.8f", e.getValue()));
            }
        }
    }

    public List<StockData> getStockDataList() { return stockDataList; }
    public Map<LocalDate, Double> getMarketReturns() { return marketReturns; }
}
