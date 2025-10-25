package com.alok.ui;

import javax.swing.*;

import com.alok.backtest.PortfolioManager;
import com.alok.performance.PerformanceMetrics;

import java.awt.*;
import java.time.YearMonth;
import java.util.Map;

public class ReportUI {
    public static void showSectorAllocationReports(PortfolioManager ema, PortfolioManager sma, PortfolioManager val) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sector Allocation (Monthly)");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLayout(new GridLayout(1,3));
            frame.add(panelFor("EMA Strategy", ema));
            frame.add(panelFor("SMA Strategy", sma));
            frame.add(panelFor("Value Strategy", val));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JPanel panelFor(String title, PortfolioManager pm) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(16f));
        panel.add(lbl, BorderLayout.NORTH);
        JTextArea area = new JTextArea(); area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<YearMonth, Map<String, Double>> e : pm.getSectorAllocation().entrySet()) {
            sb.append("Month: ").append(e.getKey()).append("\n");
            if (e.getValue().isEmpty()) sb.append("  No Holdings\n\n");
            else {
                for (Map.Entry<String, Double> s : e.getValue().entrySet()) {
                    sb.append(String.format("  %s: %.2f%%\n", s.getKey(), s.getValue()));
                }
                sb.append("\n");
            }
        }
        area.setText(sb.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    public static void showMetrics(PerformanceMetrics ema, PerformanceMetrics sma, PerformanceMetrics val,
                                   double emaAcc, double smaAcc, double valAcc) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Performance Metrics");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(700, 600);
            JTextArea a = new JTextArea(); a.setEditable(false);
            String fmt = "Avg Ann Return: %.2f%%\nAnn Vol: %.2f%%\nSharpe: %.3f\nMaxDD: %.2f%%\nTreynor: %.3f\n";
            StringBuilder sb = new StringBuilder();
            sb.append("=== EMA Strategy ===\n");
            sb.append(String.format(fmt, ema.getAverageAnnualReturnPercent(), ema.getVolatilityPercent(), ema.getSharpeRatio(), 100*ema.getMaxDrawdown(), ema.getTreynorRatio()));
            sb.append(String.format("Signal Accuracy: %.2f%%\n\n", emaAcc));
            sb.append("=== SMA Strategy ===\n");
            sb.append(String.format(fmt, sma.getAverageAnnualReturnPercent(), sma.getVolatilityPercent(), sma.getSharpeRatio(), 100*sma.getMaxDrawdown(), sma.getTreynorRatio()));
            sb.append(String.format("Signal Accuracy: %.2f%%\n\n", smaAcc));
            sb.append("=== Value Strategy ===\n");
            sb.append(String.format(fmt, val.getAverageAnnualReturnPercent(), val.getVolatilityPercent(), val.getSharpeRatio(), 100*val.getMaxDrawdown(), val.getTreynorRatio()));
            sb.append(String.format("Signal Accuracy: %.2f%%\n", valAcc));
            a.setText(sb.toString());
            frame.add(new JScrollPane(a));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
