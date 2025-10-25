package com.alok.app;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.alok.backtest.Backtester;
import com.alok.backtest.PortfolioManager;
import com.alok.data.DataLoader;
import com.alok.evaluation.FactorRegression;
import com.alok.evaluation.SignalAccuracy;
import com.alok.model.StockData;
import com.alok.performance.PerformanceMetrics;
import com.alok.strategy.*;
import com.alok.ui.ReportUI;
import com.alok.util.Log;
import com.alok.visualization.Plotter;

public class App {
    private final AppConfig config;

    public App(AppConfig config) {
        this.config = config;
    }
    // Have added logger to the code to log the progress of the backtest
    public void run() {
        Log.init(config.outDir);
        Log.info("=== Backtest starting ===");
        Log.info("Initial capital: $" + String.format("%,.2f", config.initialCapital));
        Log.info("Reading stock data from: " + config.stockCsvPath);

        DataLoader loader = new DataLoader(config.stockCsvPath, config.outDir);
        try {
            loader.loadStockData();
            loader.computeAndWriteMarketReturns(); // writes to out/market_returns.csv and stores in memory
        } catch (IOException | com.opencsv.exceptions.CsvValidationException e) {
            Log.info("Error loading data: " + e.getMessage());
            return;
        }

        List<StockData> all = loader.getStockDataList();
        Map<LocalDate, Double> market = loader.getMarketReturns();

        // Strategies (long-only) - using all data to avoid missing signals in backtest period
        Strategy ema = new EMAStrategy(config.emaShortWindow, config.emaLongWindow);
        ema.prepareData(all);
        ema.generateSignals();

        Strategy sma = new SmaCrossoverStrategy(config.smaShortWindow, config.smaLongWindow);
        sma.prepareData(all);
        sma.generateSignals();

        Strategy value = new FundamentalStrategy(config.valueTopPercentile);
        value.prepareData(all);
        value.generateSignals();

        // Backtests
        Log.info("Position size: " + String.format("%.1f%%", config.positionSize * 100));
        Backtester emaBT = new Backtester(ema.getLongs(), ema.getPriceMap(), all, config.initialCapital, config.outDir, config.positionSize);
        emaBT.runBacktest();

        Backtester smaBT = new Backtester(sma.getLongs(), sma.getPriceMap(), all, config.initialCapital, config.outDir, config.positionSize);
        smaBT.runBacktest();

        Backtester valueBT = new Backtester(value.getLongs(), value.getPriceMap(), all, config.initialCapital, config.outDir, config.positionSize);
        valueBT.runBacktest();

        Map<LocalDate, Double> emaR = emaBT.getStrategyReturns();
        Map<LocalDate, Double> smaR = smaBT.getStrategyReturns();
        Map<LocalDate, Double> valR = valueBT.getStrategyReturns();

        // Factor regression (vs market)
        FactorRegression emaReg = new FactorRegression(emaR, market);
        emaReg.runRegression();
        FactorRegression smaReg = new FactorRegression(smaR, market);
        smaReg.runRegression();
        FactorRegression valReg = new FactorRegression(valR, market);
        valReg.runRegression();

        // Metrics
        PerformanceMetrics emaM = new PerformanceMetrics(emaR); emaM.setBeta(emaReg.getBeta());
        PerformanceMetrics smaM = new PerformanceMetrics(smaR); smaM.setBeta(smaReg.getBeta());
        PerformanceMetrics valM = new PerformanceMetrics(valR); valM.setBeta(valReg.getBeta());

        // Accuracy (sanity)
        SignalAccuracy emaAcc = new SignalAccuracy(ema.getLongs(), ema.getPriceMap());
        SignalAccuracy smaAcc = new SignalAccuracy(sma.getLongs(), sma.getPriceMap());
        SignalAccuracy valAcc = new SignalAccuracy(value.getLongs(), value.getPriceMap());
        double emaAccPct = emaAcc.calculateAccuracy() * 100.0;
        double smaAccPct = smaAcc.calculateAccuracy() * 100.0;
        double valAccPct = valAcc.calculateAccuracy() * 100.0;

        // Charts
        Plotter.plotCumulativeReturns(emaR, "EMA Strategy", config.outDir);
        Plotter.plotDrawdowns(emaR, "EMA Strategy", config.outDir);
        Plotter.plotCumulativeReturns(smaR, "SMA Strategy", config.outDir);
        Plotter.plotDrawdowns(smaR, "SMA Strategy", config.outDir);
        Plotter.plotCumulativeReturns(valR, "Value Strategy", config.outDir);
        Plotter.plotDrawdowns(valR, "Value Strategy", config.outDir);
        Plotter.plotCombinedCumulativeReturns(emaR, smaR, valR, config.outDir + "/CumulativeReturns_Comparison.png");
        Plotter.plotCombinedDrawdowns(emaR, smaR, valR, config.outDir + "/Drawdowns_Comparison.png");

        // UI
        if (config.enableUI) {
            ReportUI.showSectorAllocationReports(
                emaBT.getPortfolioManager(), 
                smaBT.getPortfolioManager(), 
                valueBT.getPortfolioManager()
            );
            ReportUI.showMetrics(emaM, smaM, valM, emaAccPct, smaAccPct, valAccPct);
        }
        Log.info("=== Backtest finished ===");
        Log.close();
    }
}
