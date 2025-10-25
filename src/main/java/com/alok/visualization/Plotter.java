package com.alok.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class Plotter {

    public static void plotCumulativeReturns(Map<LocalDate, Double> strategyReturns, String strategyName, String outDir) {
        TimeSeries series = new TimeSeries(strategyName + " Cumulative");
        double cum = 1.0;
        for (Map.Entry<LocalDate, Double> e : strategyReturns.entrySet()) {
            cum *= (1 + e.getValue());
            LocalDate d = e.getKey();
            series.addOrUpdate(new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear()), cum);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(strategyName + " - Cumulative Returns", "Date", "Cumulative", dataset);
        try {
            String fn = outDir + "/" + strategyName.replaceAll(" ", "") + "_CumulativeReturns.png";
            ChartUtils.saveChartAsPNG(new File(fn), chart, 900, 600);
        } catch (IOException ex) { ex.printStackTrace(); }
        display(chart, strategyName + " Cumulative Returns");
    }

    public static void plotDrawdowns(Map<LocalDate, Double> strategyReturns, String strategyName, String outDir) {
        TimeSeries series = new TimeSeries(strategyName + " Drawdown");
        double peak = Double.NEGATIVE_INFINITY;
        double cum = 1.0;
        for (Map.Entry<LocalDate, Double> e : strategyReturns.entrySet()) {
            cum *= (1 + e.getValue());
            if (cum > peak) peak = cum;
            double dd = (cum - peak)/peak;
            LocalDate d = e.getKey();
            series.addOrUpdate(new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear()), dd);
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(strategyName + " - Drawdowns", "Date", "Drawdown", dataset);
        XYPlot plot = chart.getXYPlot();
        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setRange(-1.0, 0.0);
        try {
            String fn = outDir + "/" + strategyName.replaceAll(" ", "") + "_Drawdowns.png";
            ChartUtils.saveChartAsPNG(new File(fn), chart, 900, 600);
        } catch (IOException ex) { ex.printStackTrace(); }
        display(chart, strategyName + " Drawdowns");
    }

    public static void plotCombinedCumulativeReturns(Map<LocalDate, Double> s1, Map<LocalDate, Double> s2, Map<LocalDate, Double> s3, String outPath) {
        TimeSeries a = makeCumSeries(s1, "EMA");
        TimeSeries b = makeCumSeries(s2, "SMA");
        TimeSeries c = makeCumSeries(s3, "Value");
        TimeSeriesCollection ds = new TimeSeriesCollection();
        ds.addSeries(a); ds.addSeries(b); ds.addSeries(c);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Cumulative Returns Comparison", "Date", "Cumulative", ds);
        try { ChartUtils.saveChartAsPNG(new File(outPath), chart, 1000, 600);} catch (IOException ex){ ex.printStackTrace();}
        display(chart, "Cumulative Returns Comparison");
    }

    public static void plotCombinedDrawdowns(Map<LocalDate, Double> s1, Map<LocalDate, Double> s2, Map<LocalDate, Double> s3, String outPath) {
        TimeSeries a = makeDDSeries(s1, "EMA");
        TimeSeries b = makeDDSeries(s2, "SMA");
        TimeSeries c = makeDDSeries(s3, "Value");
        TimeSeriesCollection ds = new TimeSeriesCollection();
        ds.addSeries(a); ds.addSeries(b); ds.addSeries(c);
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Drawdowns Comparison", "Date", "Drawdown", ds);
        XYPlot plot = chart.getXYPlot(); ((NumberAxis)plot.getRangeAxis()).setRange(-1.0, 0.0);
        try { ChartUtils.saveChartAsPNG(new File(outPath), chart, 1000, 600);} catch (IOException ex){ ex.printStackTrace();}
        display(chart, "Drawdowns Comparison");
    }

    private static TimeSeries makeCumSeries(Map<LocalDate, Double> r, String name) {
        TimeSeries s = new TimeSeries(name);
        double cum=1.0;
        for (Map.Entry<LocalDate, Double> e : r.entrySet()) {
            cum *= (1 + e.getValue());
            LocalDate d = e.getKey();
            s.addOrUpdate(new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear()), cum);
        }
        return s;
    }
    private static TimeSeries makeDDSeries(Map<LocalDate, Double> r, String name) {
        TimeSeries s = new TimeSeries(name);
        double peak = Double.NEGATIVE_INFINITY, cum=1.0;
        for (Map.Entry<LocalDate, Double> e : r.entrySet()) {
            cum *= (1 + e.getValue()); if (cum>peak) peak=cum;
            LocalDate d = e.getKey();
            s.addOrUpdate(new Day(d.getDayOfMonth(), d.getMonthValue(), d.getYear()), (cum-peak)/peak);
        }
        return s;
    }

    private static void display(JFreeChart chart, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame(title);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.add(new ChartPanel(chart));
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
