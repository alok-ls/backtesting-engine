package com.alok.performance;

import java.time.LocalDate;
import java.util.*;

public class PerformanceMetrics {
    private final Map<LocalDate, Double> strategyReturns; // daily
    private double beta = 0.0;

    public PerformanceMetrics(Map<LocalDate, Double> strategyReturns) {
        this.strategyReturns = strategyReturns;
    }
    public void setBeta(double b){ this.beta = b; }

    public double getAverageAnnualReturnPercent() {
        double total = 1.0;
        for (double r : strategyReturns.values()) total *= (1 + r);
        double years = strategyReturns.size() / 252.0;
        if (years <= 0) return 0.0;
        double annual = Math.pow(total, 1.0/years) - 1;
        return 100.0 * annual;
    }

    public double getVolatilityPercent() {
        if (strategyReturns.size() < 2) return 0.0;
        double mean = strategyReturns.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double var = strategyReturns.values().stream().mapToDouble(x -> (x-mean)*(x-mean)).sum() / (strategyReturns.size()-1);
        double dailyStd = Math.sqrt(var);
        return 100.0 * dailyStd * Math.sqrt(252.0);
    }

    public double getSharpeRatio() {
        if (strategyReturns.size() < 2) return 0.0;
        double mean = strategyReturns.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double var = strategyReturns.values().stream().mapToDouble(x -> (x-mean)*(x-mean)).sum() / (strategyReturns.size()-1);
        double dailyStd = Math.sqrt(var);
        return (dailyStd==0)?0.0: (mean / dailyStd) * Math.sqrt(252.0);
    }

    public double getMaxDrawdown() {
        double peak = Double.NEGATIVE_INFINITY;
        double cum = 1.0;
        double maxDD = 0.0;
        for (double r : strategyReturns.values()) {
            cum *= (1 + r);
            if (cum > peak) peak = cum;
            double dd = (cum - peak)/peak;
            if (dd < maxDD) maxDD = dd;
        }
        return maxDD;
    }

    public double getTreynorRatio() {
        double ann = getAverageAnnualReturnPercent()/100.0;
        if (beta == 0) return 0.0;
        return ann / beta;
    }
}
