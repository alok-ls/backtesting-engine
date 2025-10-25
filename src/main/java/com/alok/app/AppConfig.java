package com.alok.app;

public class AppConfig {
    public final String stockCsvPath;
    public final String outDir;
    public final int emaShortWindow;
    public final int emaLongWindow;
    public final int smaShortWindow;
    public final int smaLongWindow;
    public final double valueTopPercentile; 
    public final double initialCapital;
    public final boolean enableUI;
    public final double positionSize;

    private AppConfig(Builder b) {
        this.stockCsvPath = b.stockCsvPath;
        this.outDir = b.outDir;
        this.emaShortWindow = b.emaShortWindow;
        this.emaLongWindow = b.emaLongWindow;
        this.smaShortWindow = b.smaShortWindow;
        this.smaLongWindow = b.smaLongWindow;
        this.valueTopPercentile = b.valueTopPercentile;
        this.initialCapital = b.initialCapital;
        this.enableUI = b.enableUI;
        this.positionSize = b.positionSize;
    }

    public static Builder builder() { return new Builder(); }

    public static AppConfig defaults() {
        return builder()
                .stockCsvPath("src/main/resources/daily_stock_data.csv")
                .outDir("out")
                .emaShortWindow(5)
                .emaLongWindow(20)
                .smaShortWindow(20)
                .smaLongWindow(100)
                .valueTopPercentile(10.0)
                .initialCapital(1_000_000.0)
                .enableUI(true)
                .positionSize(0.15)
                .build();
    }

    public static class Builder {
        private String stockCsvPath;
        private String outDir = "out";
        private int emaShortWindow = 5;
        private int emaLongWindow = 20;
        private int smaShortWindow = 20;
        private int smaLongWindow = 100;
        private double valueTopPercentile = 10.0;
        private double initialCapital = 1_000_000.0;
        private boolean enableUI = true;
        private double positionSize = 0.15; 

        public Builder stockCsvPath(String v) { this.stockCsvPath = v; return this; }
        public Builder outDir(String v) { this.outDir = v; return this; }
        public Builder emaShortWindow(int v) { this.emaShortWindow = v; return this; }
        public Builder emaLongWindow(int v) { this.emaLongWindow = v; return this; }
        public Builder smaShortWindow(int v) { this.smaShortWindow = v; return this; }
        public Builder smaLongWindow(int v) { this.smaLongWindow = v; return this; }
        public Builder valueTopPercentile(double v) { this.valueTopPercentile = v; return this; }
        public Builder initialCapital(double v) { this.initialCapital = v; return this; }
        public Builder enableUI(boolean v) { this.enableUI = v; return this; }
        public Builder positionSize(double v) { this.positionSize = v; return this; }
        public AppConfig build() { return new AppConfig(this); }
    }
}
