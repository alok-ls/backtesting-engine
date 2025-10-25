package com.alok.model;

import java.time.LocalDate;

public class StockData {
    private final LocalDate date;
    private final String symbol;
    private final String sector;
    private final double closePrice;
    private final double peRatio;

    public StockData(LocalDate date, String symbol, String sector, double closePrice, double eps, double peRatio) {
        this.date = date;
        this.symbol = symbol;
        this.sector = sector;
        this.closePrice = closePrice;
        this.peRatio = peRatio;
    }

    public LocalDate getDate(){ return date; }
    public String getSymbol(){ return symbol; }
    public String getSector(){ return sector; }
    public double getClosePrice(){ return closePrice; }
    public double getPeRatio(){ return peRatio; }
}
