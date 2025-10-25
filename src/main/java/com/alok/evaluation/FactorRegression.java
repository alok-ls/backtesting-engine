package com.alok.evaluation;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Map;

public class FactorRegression {
    private final Map<LocalDate, Double> strategyReturns;
    private final Map<LocalDate, Double> marketReturns;

    private double alpha;
    private double beta;
    private double rSquared;

    public FactorRegression(Map<LocalDate, Double> strategyReturns, Map<LocalDate, Double> marketReturns) {
        this.strategyReturns = strategyReturns;
        this.marketReturns = marketReturns;
    }

    public void runRegression() {
        int n = 0;
        for (LocalDate d : strategyReturns.keySet()) if (marketReturns.containsKey(d)) n++;
        if (n < 3) {
            JOptionPane.showMessageDialog(null, "Not enough overlapping dates for regression.", "Regression", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double[] y = new double[n];
        double[][] x = new double[n][1];
        int i = 0;
        for (LocalDate d : strategyReturns.keySet()) {
            if (marketReturns.containsKey(d)) {
                y[i] = strategyReturns.get(d);
                x[i][0] = marketReturns.get(d);
                i++;
            }
        }
        OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
        reg.setNoIntercept(false);
        reg.newSampleData(y, x);
        try {
            double[] params = reg.estimateRegressionParameters();
            this.alpha = params[0];
            this.beta = params[1];
            this.rSquared = reg.calculateRSquared();
            String msg = String.format(
                "=== Factor Regression ===%nAlpha: %.6f%nBeta: %.4f%nR^2: %.4f",
                alpha, beta, rSquared);
            JTextArea area = new JTextArea(msg); area.setEditable(false);
            JOptionPane.showMessageDialog(null, new JScrollPane(area), "Factor Regression Results", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Regression failed: " + ex.getMessage(), "Regression Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public double getAlpha(){ return alpha; }
    public double getBeta(){ return beta; }
    public double getRSquared(){ return rSquared; }
}
